package net.gteam.wave.utils;

import net.gteam.wave.Wave;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public final class TaskUtils {

    private TaskUtils() {
    }

    public static BukkitTask taskTimer(final Runnable runnable, final long delay, final long interval) {
        return Bukkit.getScheduler().runTaskTimer(Wave.getInstance(), runnable, delay, interval);
    }

    public static BukkitTask taskTimerAsync(final Runnable runnable, final long delay, final long interval) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(Wave.getInstance(), runnable, delay, interval);
    }

    public static BukkitTask task(final Runnable runnable) {
        return Bukkit.getScheduler().runTask(Wave.getInstance(), runnable);
    }

    public static BukkitTask taskAsync(final Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(Wave.getInstance(), runnable);
    }

    public static BukkitTask taskLater(final Runnable runnable, final long delay) {
        return Bukkit.getScheduler().runTaskLater(Wave.getInstance(), runnable, delay);
    }

    public static BukkitTask taskLaterAsync(final Runnable runnable, final long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(Wave.getInstance(), runnable, delay);
    }
}
