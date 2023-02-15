package com.xiii.wave.tasks;

import com.xiii.wave.Wave;
import org.bukkit.scheduler.BukkitRunnable;

public class ViolationTask extends BukkitRunnable {

    private final Wave plugin;

    public ViolationTask(Wave plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getProfileManager().getProfileMap().values().forEach(profile -> {
            for (Check check : profile.getCheckHolder().getChecks()) check.resetVl();
        });
    }

}
