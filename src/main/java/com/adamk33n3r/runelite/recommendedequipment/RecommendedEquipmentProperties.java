package com.adamk33n3r.runelite.recommendedequipment;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RecommendedEquipmentProperties {
    @Getter()
    private static final Properties properties = new Properties();

    static {
//        try (InputStream in = RecommendedEquipmentProperties.class.getResourceAsStream("recequip.properties")) {
//            properties.load(in);
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }

        try (InputStream in = RecommendedEquipmentProperties.class.getResourceAsStream("version.properties")) {
            properties.load(in);
            String pluginVersion = String.format(
                "%s.%s.%s",
                properties.getProperty("VERSION_MAJOR"),
                properties.getProperty("VERSION_MINOR"),
                properties.getProperty("VERSION_PATCH"));
            properties.put("recequip.pluginVersion", pluginVersion);
            String phase = properties.getProperty("VERSION_PHASE");
            String build = properties.getProperty("VERSION_BUILD");
            String pluginVersionFull = String.format("%s-%s+%s", pluginVersion, phase, build);
            properties.put("recequip.pluginVersionFull", pluginVersionFull);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
