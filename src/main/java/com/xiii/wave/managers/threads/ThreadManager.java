package com.xiii.wave.managers.threads;

import com.xiii.wave.Wave;
import com.xiii.wave.managers.Initializer;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.utils.ChatUtils;
import com.xiii.wave.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

public class ThreadManager implements Listener, Initializer {

    //Can't really * 2 core count since modern CPUs don't really have double the amount of cores as their threads count (example: i9 13900K)
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    private final List<ProfileThread> profileThreads = new ArrayList<>();

    private final Wave plugin;

    public ThreadManager(Wave plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        Bukkit.getPluginManager().registerEvents(this, Wave.getInstance());
    }

    public ProfileThread getAvailableProfileThread() {

        ProfileThread profileThread;

        //Check whether we should create a new thread based on the thread limit
        if (this.profileThreads.size() < MAX_THREADS) {

            //Create a new profile thread and set it to our variable in order to use it
            profileThread = new ProfileThread();

            //Add our new profile thread to the list in order to use it for future profiles
            this.profileThreads.add(profileThread);

        } else {

            //Get an available thread based on the profiles using it, Otherwise grab a random element to avoid issues.
            profileThread = this.profileThreads
                    .stream()
                    .min(Comparator.comparing(ProfileThread::getProfileCount))
                    .orElse(MiscUtils.randomElement(this.profileThreads));
        }

        //Throw an exception if the profile thread is null, Which should be impossible.
        if (profileThread == null) {

            ChatUtils.log(Level.SEVERE, "Encountered a null profile thread, please restart the server.");
        }

        //Return the available thread and increment the profile count
        return profileThread.incrementAndGet();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent e) {

        Profile profile = this.plugin.getProfileManager().getProfile(e.getPlayer());

        if (profile == null) return;

        ProfileThread profileThread = profile.getProfileThread();

        /*
        If this is the only profile using this thread, Shut it down and remove it from the list
        Otherwise decrease the counter and return.
        */
        if (profileThread.getProfileCount() > 1) {

            profileThread.decrement();

            return;
        }

        this.profileThreads.remove(profileThread.shutdownThread());
    }

    @Override
    public void shutdown() {
        this.profileThreads.forEach(ProfileThread::shutdownThread);

        this.profileThreads.clear();
    }
}
