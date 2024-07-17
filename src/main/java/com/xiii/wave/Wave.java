package com.xiii.wave;

import com.github.retrooper.packetevents.PacketEvents;
import com.xiii.wave.commands.CommandManager;
import com.xiii.wave.files.Checks;
import com.xiii.wave.files.Config;
import com.xiii.wave.files.commentedfiles.CommentedFileConfiguration;
import com.xiii.wave.listener.ClientBrandListener;
import com.xiii.wave.listener.ProfileListener;
import com.xiii.wave.listener.ViolationListener;
import com.xiii.wave.managers.AlertManager;
import com.xiii.wave.managers.profile.ProfileManager;
import com.xiii.wave.managers.threads.ThreadManager;
import com.xiii.wave.nms.NmsManager;
import com.xiii.wave.tasks.ViolationTask;
import com.xiii.wave.utils.ChatUtils;
import com.xiii.wave.utils.MiscUtils;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.logging.Level;

public final class Wave extends JavaPlugin {

    private static Wave instance;
    // Test

    private Config configuration;
    private Checks checks;

    private ProfileManager profileManager;
    private final NmsManager nmsManager = new NmsManager();
    //private LogManager logManager;
    private ThreadManager threadManager;

    private AlertManager alertManager;
    //private ThemeManager themeManager;

    private ChatUtils chatUtils;

    // PacketEvents
    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        //Are all listeners read only?
        PacketEvents.getAPI().getSettings().checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {

        //Initialize
        instance = this;
        ChatUtils.log(Level.INFO, "Initialization...");
        this.chatUtils = new ChatUtils(this);
        (this.threadManager = new ThreadManager(this)).initialize();
        (this.configuration = new Config(this)).initialize();
        (this.checks = new Checks(this)).initialize();
        (this.profileManager = new ProfileManager()).initialize();
        //(this.themeManager = new ThemeManager(this)).initialize();
        //(this.logManager = new LogManager(this)).initialize();
        (this.alertManager = new AlertManager()).initialize();

        new ViolationTask(this).runTaskTimerAsynchronously(this,
                Config.Setting.CHECKS_VL_CLEAR_RATE.getLong() * 1200L,
                Config.Setting.CHECKS_VL_CLEAR_RATE.getLong() * 1200L);

        //We're most likely going to be using transactions - ping pongs, So we need to do this for ViaVersion
        System.setProperty("com.viaversion.handlePingsAsInvAcknowledgements", "true");

        //Initialize static variables to make sure our threads won't get affected when they run for the first time.
        try {

            MiscUtils.initializeClasses(
                    "com.xiii.wave.utils.fastmath.FastMath",
                    "com.xiii.wave.utils.fastmath.NumbersUtils",
                    "com.xiii.wave.utils.fastmath.FastMathLiteralArrays",
                    //"me.nik.anticheatbase.utils.minecraft.MathHelper",
                    "com.xiii.wave.utils.CollisionUtils",
                    "com.xiii.wave.utils.MoveUtils"
            );

        } catch (ClassNotFoundException e) {

            //Impossible unless we made a mistake
            ChatUtils.log(Level.SEVERE, "An error occurred during initialization phase, please restart your server.");

            e.printStackTrace();
        }

        // Startup
        ChatUtils.log(Level.INFO, "Starting up...");
        ChatUtils.log(Level.INFO, "Listeners initialization...");

        //Packet Listeners
        Arrays.asList(
                new com.xiii.wave.processors.listeners.PacketListener(this),
                new ClientBrandListener(this)
        ).forEach(packetListener -> PacketEvents.getAPI().getEventManager().registerListener(packetListener));

        //Bukkit Listeners
        Arrays.asList(
                new ProfileListener(this),
                new ViolationListener(this)
        ).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        PacketEvents.getAPI().init();

        ChatUtils.log(Level.INFO, "Commands initialization...");

        //Load Commands
        getCommand("wave").setExecutor(new CommandManager(this));

        ChatUtils.log(Level.INFO, "Reading configuration files...");
        ChatUtils.log(Level.INFO, "Anti-Cheat loaded. Thank you for using Wave.");
    }

    @Override
    public void onDisable() {

        //Terminate PacketEvents
        PacketEvents.getAPI().terminate();

        //Cancel any bukkit tasks
        Bukkit.getScheduler().cancelTasks(this);

        //Shutdown all managers
        this.configuration.shutdown();
        this.checks.shutdown();
        this.profileManager.shutdown();
        this.alertManager.shutdown();
        this.threadManager.shutdown();
        //this.themeManager.shutdown();

        ChatUtils.log(Level.INFO, "Anti-Cheat unloaded. Goodbye!");

    }

    public static Wave getInstance() {
        return instance;
    }

    public CommentedFileConfiguration getConfiguration() {
        return this.configuration.getConfig();
    }

    public CommentedFileConfiguration getChecks() {
        return this.checks.getConfig();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public NmsManager getNmsManager() {
        return nmsManager;
    }

    public ThreadManager getThreadManager() {
        return threadManager;
    }

    public AlertManager getAlertManager() {
        return alertManager;
    }

    public ChatUtils getChatUtils() {
        return chatUtils;
    }
}
