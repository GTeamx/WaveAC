package com.xiii.wave.utils;

import com.github.retrooper.packetevents.PacketEvents;
import com.xiii.wave.Wave;
import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public final class ConfigUtils {

    private final Wave INSTANCE;
    private final FileConfiguration config;
    private File checksFile = null;
    private FileConfiguration checks = null;

    public ConfigUtils(final Wave instancePlugin) {

        this.INSTANCE = instancePlugin;
        this.config = Wave.INSTANCE.getConfig();
        saveDefaults("config");
        saveDefaults("checks");

    }


    public synchronized void reloadConfigs() {

        INSTANCE.reloadConfig();

        if(checksFile == null) checksFile = new File(INSTANCE.getDataFolder(), "checks.yml");

        checks = YamlConfiguration.loadConfiguration(checksFile);

        final InputStream checksFileStream = INSTANCE.getResource("checks.yml");

        if(checksFileStream != null) {

            final YamlConfiguration defaultChecksFile = YamlConfiguration.loadConfiguration(new InputStreamReader(checksFileStream));
            checks.setDefaults(defaultChecksFile);

        }

    }


    public synchronized boolean getBoolean(final String configName, final String value, final boolean defaultValue) {

        try {

            if(configName.equalsIgnoreCase("config")) config.get(value);
            if(configName.equalsIgnoreCase("checks")) checks.get(value);

        } catch(final NullPointerException e) {
            return defaultValue;
        }

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) reloadConfigs();

            return config.getBoolean(value);

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checks == null) reloadConfigs();

            return checks.getBoolean(value);
        }

        return defaultValue;

    }

    public synchronized String getString(final String configName, final String value, final String defaultValue) {

        try {

            if(configName.equalsIgnoreCase("config")) config.get(value);
            if(configName.equalsIgnoreCase("checks")) checks.get(value);

        } catch(final NullPointerException e) {
            return defaultValue;
        }

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) INSTANCE.reloadConfig();

            return config.getString(value);

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checks == null) reloadConfigs();

            return checks.getString(value);

        }

        return defaultValue;

    }

    public synchronized String getStringConverted(final String configName, final Player player, final String value, final String defaultValue) {

        PlayerData data = Data.getPlayerData(player);

        try {

            if(configName.equalsIgnoreCase("config")) config.get(value);
            if(configName.equalsIgnoreCase("checks")) checks.get(value);

        } catch(final NullPointerException e) {

            final String defaultVal = defaultValue;
            final String prefix = config.getString("prefix");
            String newStringConverted = defaultVal;

            if(defaultVal.contains("%prefix%")) newStringConverted = newStringConverted.replace("%prefix%", prefix);
            if(defaultVal.contains("%player%")) newStringConverted = newStringConverted.replace("%player%", player.getName());
            if(defaultVal.contains("%version%")) newStringConverted = newStringConverted.replace("%version%", PacketEvents.getAPI().getPlayerManager().getClientVersion(player).name().replaceAll("_", ".").substring(2));
            if(defaultVal.contains("%brand%")) newStringConverted = newStringConverted.replace("%brand%", data.clientBrand);

            return newStringConverted;

        }

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) INSTANCE.reloadConfig();

            final String configPathValue = config.getString(value);
            final String prefix = config.getString("prefix");
            String newStringConverted = configPathValue;

            if(configPathValue.contains("%prefix%")) newStringConverted = newStringConverted.replace("%prefix%", prefix);
            if(configPathValue.contains("%player%")) newStringConverted = newStringConverted.replace("%player%", player.getName());
            if(configPathValue.contains("%version%")) newStringConverted = newStringConverted.replace("%version%", PacketEvents.getAPI().getPlayerManager().getClientVersion(player).name().replaceAll("_", ".").substring(2));
            if(configPathValue.contains("%brand%")) newStringConverted = newStringConverted.replace("%brand%", data.clientBrand);

            return newStringConverted;

        } else if(configName.equalsIgnoreCase("checks")) {

            if(checks == null) reloadConfigs();
            if(config == null) INSTANCE.reloadConfig();

            final String configPathValue = checks.getString(value);
            final String prefix = config.getString("prefix");
            String newStringConverted = configPathValue;

            if(configPathValue.contains("%prefix%")) newStringConverted = newStringConverted.replace("%prefix%", prefix);
            if(configPathValue.contains("%player%")) newStringConverted = newStringConverted.replace("%player%", player.getName());
            if(configPathValue.contains("%version%")) newStringConverted = newStringConverted.replace("%player version%", PacketEvents.getAPI().getPlayerManager().getClientVersion(player).name().replaceAll("_", ".").substring(2));
            if(configPathValue.contains("%brand%")) newStringConverted = newStringConverted.replace("%brand%", data.clientBrand);

            return newStringConverted;

        } else {

            final String defaultVal = defaultValue;
            String newStringConverted = defaultVal;

            if(defaultVal.contains("%player%")) newStringConverted = newStringConverted.replace("%player%", player.getName());

            return newStringConverted;

        }
    }
    public synchronized int getInt(final String configName, final String value, final int defaultValue) {

        try {

            if(configName.equalsIgnoreCase("config")) config.get(value);
            if(configName.equalsIgnoreCase("checks")) checks.get(value);

        } catch(final NullPointerException e) {
            return defaultValue;
        }

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) INSTANCE.reloadConfig();

            return config.getInt(value);

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checks == null) reloadConfigs();

            return checks.getInt(value);

        }

        return defaultValue;

    }

    public synchronized double getDouble(final String configName, final String value, final double defaultValue) {

        try {

            if(configName.equalsIgnoreCase("config")) config.get(value);
            if(configName.equalsIgnoreCase("checks")) checks.get(value);

        } catch(final NullPointerException e) {
            return defaultValue;
        }

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) INSTANCE.reloadConfig();

            return config.getDouble(value);

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checks == null) reloadConfigs();

            return checks.getDouble(value);

        }

        return defaultValue;

    }


    public synchronized FileConfiguration getConfig(final String configName) {

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) INSTANCE.reloadConfig();

            return config;

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checks == null) reloadConfigs();

            return checks;

        }

        return null;

    }

    public synchronized void saveConfig(final String configName) {

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) return;
            INSTANCE.saveDefaultConfig();

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checks == null || checksFile == null) return;

            try {

                checks.save(checksFile);

            } catch(final IOException e) {
                INSTANCE.getLogger().log(Level.SEVERE, "Could not save checks.yml!");
            }

        }

    }

    public synchronized void saveDefaults(final String configName) {

        if(configName.equalsIgnoreCase("config")) {

            if(config == null) return;
            INSTANCE.saveDefaultConfig();

        }

        if(configName.equalsIgnoreCase("checks")) {

            if(checksFile == null) checksFile = new File(INSTANCE.getDataFolder(), "checks.yml");

            if(!checksFile.exists()) INSTANCE.saveResource("checks.yml", false);

        }

    }

}