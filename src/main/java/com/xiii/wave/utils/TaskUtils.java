package com.xiii.wave.utils;

import com.xiii.wave.Wave;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class TaskUtils {

    private TaskUtils() {
    }

    public static BukkitTask taskTimer(Runnable runnable, long delay, long interval) {
        return Bukkit.getScheduler().runTaskTimer(Wave.getInstance(), runnable, delay, interval);
    }

    public static BukkitTask taskTimerAsync(Runnable runnable, long delay, long interval) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Wave.getInstance(), runnable, delay, interval);
    }

    public static BukkitTask task(Runnable runnable) {
        return Bukkit.getScheduler().runTask(Wave.getInstance(), runnable);
    }

    public static BukkitTask taskAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(Wave.getInstance(), runnable);
    }

    public static BukkitTask taskLater(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(Wave.getInstance(), runnable, delay);
    }

    public static BukkitTask taskLaterAsync(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(Wave.getInstance(), runnable, delay);
    }
}
