package com.xiii.wave;

import com.github.retrooper.packetevents.PacketEvents;
import com.xiii.wave.commands.AlertsCommand;
import com.xiii.wave.commands.WaveCommand;
import com.xiii.wave.data.Data;
import com.xiii.wave.listener.PacketListener;
import com.xiii.wave.utils.ConfigUtils;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Wave extends JavaPlugin {

    public static Wave INSTANCE;
    public PacketListener packetListener;
    public ConfigUtils configUtils;

    // PacketEvents
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        //Are all listeners read only?
        PacketEvents.getAPI().getSettings().readOnlyListeners(true)
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        // Init base
        INSTANCE = this;
        packetListener = new PacketListener();
        configUtils = new ConfigUtils(this);

        // Startup
        Bukkit.getLogger().log(Level.INFO, "[Wave] Starting up...");
        Bukkit.getLogger().log(Level.INFO, "[Wave] Packet listener initialization...");
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener());
        PacketEvents.getAPI().init();
        Bukkit.getLogger().log(Level.INFO, "[Wave] Commands initialization...");
        Bukkit.getPluginCommand("alerts").setExecutor(new AlertsCommand());
        Bukkit.getPluginCommand("wave").setExecutor(new WaveCommand());
        Bukkit.getLogger().log(Level.INFO, "[Wave] Reading configuration files...");
        configUtils.reloadConfigs();
        Bukkit.getLogger().log(Level.INFO, "[Wave] Anti-Cheat loaded. Thank you for using Wave.");
    }

    @Override
    public void onDisable() {

        // Unload
        PacketEvents.getAPI().terminate();
        Bukkit.getScheduler().cancelTasks(this);
        Data.clearData();
        Bukkit.getLogger().log(Level.INFO, "[Wave] Anti-Cheat unloaded. Goodbye!");

    }
}
