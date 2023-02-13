package com.xiii.wave;

import com.xiii.wave.commands.AlertsCommand;
import com.xiii.wave.commands.WaveCommand;
import com.xiii.wave.data.Data;
import com.xiii.wave.listener.PacketListener;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.settings.PacketEventsSettings;
import io.github.retrooper.packetevents.utils.server.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class Wave extends JavaPlugin {

    public static Wave INSTANCE;
    public PacketListener packetListener;

    // PacketEvents
    @Override
    public void onLoad() {
        PacketEvents.create(this);
        PacketEventsSettings settings = PacketEvents.get().getSettings();
        settings
                .fallbackServerVersion(ServerVersion.v_1_7_10)
                .compatInjector(false)
                .checkForUpdates(false);
        PacketEvents.get().load();
    }

    @Override
    public void onEnable() {

        // Init base
        INSTANCE = this;
        packetListener = new PacketListener();
        Data.registerPlayerData(Bukkit.getPlayer("?"));

        // Startup
        Bukkit.getLogger().log(Level.INFO, "[Wave] Starting up...");
        Bukkit.getLogger().log(Level.INFO, "[Wave] Packet listener initialization...");
        PacketEvents.get().init();
        PacketEvents.get().registerListener(packetListener);
        Bukkit.getLogger().log(Level.INFO, "[Wave] Commands initialization...");
        Bukkit.getPluginCommand("alerts").setExecutor(new AlertsCommand());
        Bukkit.getPluginCommand("wave").setExecutor(new WaveCommand());
        Bukkit.getLogger().log(Level.INFO, "[Wave] Anti-Cheat loaded. Thank you for using Wave.");
    }

    @Override
    public void onDisable() {

        // Unload
        PacketEvents.get().terminate();
        Bukkit.getScheduler().cancelTasks(this);
        Data.clearData();
        Bukkit.getLogger().log(Level.INFO, "[Wave] Anti-Cheat unloaded. See ya next time!");

    }
}
