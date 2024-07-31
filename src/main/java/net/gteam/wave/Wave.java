package net.gteam.wave;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.gteam.wave.files.Checks;
import net.gteam.wave.files.Config;
import net.gteam.wave.files.commentedfiles.CommentedFileConfiguration;
import net.gteam.wave.listeners.ProfileListener;
import net.gteam.wave.listeners.ViolationListener;
import net.gteam.wave.managers.AlertManager;
import net.gteam.wave.managers.profile.ProfileManager;
import net.gteam.wave.managers.themes.ThemeManager;
import net.gteam.wave.managers.threads.ThreadManager;
import net.gteam.wave.nms.NMSManager;
import net.gteam.wave.processors.listeners.BukkitListener;
import net.gteam.wave.processors.listeners.NetworkListener;
import net.gteam.wave.tasks.TickTask;
import net.gteam.wave.tasks.ViolationTask;
import net.gteam.wave.utils.ChatUtils;
import net.gteam.wave.utils.MiscUtils;
import net.gteam.wave.utils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

public class Wave extends JavaPlugin {

    // Test
    private static Wave INSTANCE;

    private ProfileManager profileManager;
    private ThreadManager threadManager;
    private AlertManager alertManager;
    private NMSManager nmsManager;
    private ThemeManager themeManager;

    private Config configuration;
    private Checks checks;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        //Are all listeners read only?
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(false);
        PacketEvents.getAPI().load();
    }

    public void onEnable() {

        INSTANCE = this;

        this.profileManager = new ProfileManager();
        this.threadManager = new ThreadManager(this);
        this.alertManager = new AlertManager();
        this.nmsManager = new NMSManager();
        this.themeManager = new ThemeManager(this);
        this.configuration = new Config(this);
        this.checks = new Checks(this);

        this.profileManager.initialize();
        this.threadManager.initialize();
        this.alertManager.initialize();
        //this.themeManager.initialize(); // TODO: Fix
        this.configuration.initialize();
        this.checks.initialize();

        // Bukkit Listeners
        Arrays.asList(
                new ProfileListener(this),
                new ViolationListener(this),
                new BukkitListener()
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        PacketEvents.getAPI().getEventManager().registerListener(new NetworkListener(this));
        PacketEvents.getAPI().init();

        // We're most likely going to be using transactions - ping pongs, So we need to do this for ViaVersion
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");

        // Initialize static variables to make sure our threads won't get affected when they run for the first time.
        try {

            MiscUtils.initializeClasses(
                    "net.gteam.wave.utils.fastmath.FastMath",
                    "net.gteam.wave.utils.fastmath.NumbersUtils",
                    "net.gteam.wave.utils.fastmath.FastMathLiteralArrays",
                    "net.gteam.wave.utils.minecraft.MathHelper",
                    "net.gteam.wave.utils.CollisionUtils",
                    "net.gteam.wave.utils.MoveUtils"
            );

        } catch (final ClassNotFoundException e) {

            // Impossible unless we made a mistake
            ChatUtils.log(Level.SEVERE, "An error was thrown during initialization, The anticheat may not work properly.");

            e.printStackTrace();
        }

        new TickTask(this).runTaskTimerAsynchronously(this, 50L, 0L);

        new ViolationTask(this).runTaskTimerAsynchronously(this,
                Config.Setting.CHECK_SETTINGS_VIOLATION_RESET_INTERVAL.getLong() * 1200L,
                Config.Setting.CHECK_SETTINGS_VIOLATION_RESET_INTERVAL.getLong() * 1200L);
    }

    public void onDisable() {

        this.profileManager.shutdown();
        this.threadManager.shutdown();
        this.alertManager.shutdown();
        this.themeManager.shutdown();
        this.configuration.shutdown();
        this.checks.shutdown();

        Bukkit.getScheduler().cancelTasks(this);

        PacketEvents.getAPI().terminate();

        // Clear reflection cache
        ReflectionUtils.clear();

        INSTANCE = null;
    }

    public static Wave getInstance() {
        return INSTANCE;
    }

    public ProfileManager getProfileManager() {
        return this.profileManager;
    }

    public ThreadManager getThreadManager() {
        return this.threadManager;
    }

    public AlertManager getAlertManager() {
        return this.alertManager;
    }

    public NMSManager getNmsManager() {
        return this.nmsManager;
    }

    public ThemeManager getThemeManager() {
        return this.themeManager;
    }

    public CommentedFileConfiguration getConfiguration() {
        return this.configuration.getConfig();
    }

    public CommentedFileConfiguration getChecks() {
        return this.checks.getConfig();
    }
}
