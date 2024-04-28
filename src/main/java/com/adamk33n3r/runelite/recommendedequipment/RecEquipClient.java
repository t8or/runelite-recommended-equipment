package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RecEquipClient {
    private final OkHttpClient cachingClient;
    private final Gson gson;
    private static final HttpUrl GITHUB = Objects.requireNonNull(HttpUrl.parse("https://raw.githubusercontent.com/adamk33n3r/recgear-wiki-scraper"));

    @Inject
    public RecEquipClient(OkHttpClient cachingClient, Gson gson) {
        this.cachingClient = cachingClient.newBuilder()
            .addInterceptor(new CacheInterceptor(15))
            .build();
        this.gson = gson.newBuilder()
            .registerTypeAdapter(Activity.class, new ActivityDeserializer())
//            .registerTypeAdapter(ActivityEquipmentStyle.class, new ActivityEquipmentStyleDeserializer())
            .registerTypeAdapter(ActivitySlotTier.class, new ActivitySlotTierDeserializer())
//            .registerTypeAdapter(ActivityItem.class, new ActivityItemDeserializer())
            .create();
    }

    public List<Activity> downloadActivities(boolean forceDownload) throws IOException {
        HttpUrl allActivities = GITHUB.newBuilder()
            .addPathSegment("master")
            .addPathSegment("recs")
            .addPathSegment("all.min.json")
            .build();
        Request.Builder reqBuilder = new Request.Builder().url(allActivities);
        if (forceDownload) {
            reqBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        }
        try (Response res  = this.cachingClient.newCall(reqBuilder.build()).execute()) {
            if (res.code() != 200) {
                throw new IOException("Non-OK response code: " + res.code());
            }

            ArrayList<Activity> activities = new ArrayList<>();
            JsonElement parse = new JsonParser().parse(Objects.requireNonNull(res.body()).string());
            parse.getAsJsonObject().entrySet().forEach(entry -> {
                String key = entry.getKey();
                JsonElement value = entry.getValue();
                Activity activity = this.gson.fromJson(value, Activity.class);
                activity.setName(key);
//                activity.setCategory("TEST");
                activities.add(activity);
            });
            return activities;

//            String string = Objects.requireNonNull(res.body()).string();
//            System.out.println(string);
//            return this.gson.fromJson(string, new TypeToken<List<Activity>>() {}.getType());
//            ActivityAllManifest activityAllManifest = this.gson.fromJson(Objects.requireNonNull(res.body()).string(), ActivityAllManifest.class);
        }
    }

    static class CacheInterceptor implements Interceptor {
        private final int minutes;
        public CacheInterceptor(int minutes) {
            this.minutes = minutes;
        }

        @Override
        @Nonnull
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl = new CacheControl.Builder()
                .maxAge(this.minutes, TimeUnit.MINUTES)
                .build();

            return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build();
        }
    }
}
