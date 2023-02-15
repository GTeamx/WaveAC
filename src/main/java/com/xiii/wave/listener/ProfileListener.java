package com.xiii.wave.listener;

import com.xiii.wave.Wave;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ProfileListener implements Listener {

    private final Wave plugin;

    public ProfileListener(Wave plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent e) {

        final Player player = e.getPlayer();

        this.plugin.getProfileManager().createProfile(player);

        // TODO: toggle alerts on join
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        this.plugin.getProfileManager().removeProfile(e.getPlayer());
    }
}
