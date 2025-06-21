package com.adamk33n3r.runelite.recommendedequipment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
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
            .registerTypeAdapter(ActivitySlotTier.class, new ActivitySlotTierDeserializer())
            .create();
    }

    public void downloadActivities(boolean forceDownload, Consumer<List<Activity>> callback) throws IOException {
        HttpUrl allActivities = GITHUB.newBuilder()
            .addPathSegment("master")
            .addPathSegment("recs")
            .addPathSegment("all.min.json")
            .build();
        Request.Builder reqBuilder = new Request.Builder().url(allActivities);
        if (forceDownload) {
            reqBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        }
        Request request = reqBuilder.build();
        final Gson gson = this.gson;
        this.cachingClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                log.error("Error with request at: {}", request.url(), e);
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
                if (response.code() != 200) {
                    log.error("Error with request at: {} {}", request.url(), response.body() != null ? response.body().string() : null);
                    throw new IOException("Non-OK response code: " + response.code());
                }

                List<Activity> activities = gson.fromJson(
                    Objects.requireNonNull(response.body()).string(),
                    new TypeToken<List<Activity>>() {}.getType()
                );
                callback.accept(activities);
            }
        });
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
