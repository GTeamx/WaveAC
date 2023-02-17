package com.xiii.wave.listener;

import com.xiii.wave.Wave;
import com.xiii.wave.api.events.WaveViolationEvent;
import com.xiii.wave.enums.MsgType;
import com.xiii.wave.files.Config;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.utils.JsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ViolationListener implements Listener {

    private final Wave plugin;

    public ViolationListener(Wave plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onViolation(WaveViolationEvent e) {

        this.plugin.getAlertManager().getAlertExecutor().execute(() -> {

            final Player p = e.getPlayer();

            if (p == null || !p.isOnline()) return;

            Profile profile = this.plugin.getProfileManager().getProfile(p);

            if (profile == null) return;

            final String tps = String.valueOf(20);
            // TODO: Fix TPS

            final String checkType = e.getType();

            final String checkName = e.getCheck();

            final String check = (checkType.isEmpty() ? checkName : checkName + " (" + checkType + ")")
                    + (e.isExperimental() ? " (Experimental)" : "");

            final String description = e.getDescription();

            final String information = e.getInformation();

            final String playerName = p.getName();

            final int vl = e.getVl();

            //this.plugin.getLogManager().addLogToQueue(new PlayerLog(
            //        Config.Setting.SERVER_NAME.getString(),
            //        playerName,
            //        p.getUniqueId().toString(),
            //        check,
            //        information
            //));
            // TODO: Log manager (disabled for now not needed)

            //We're sending the alerts by using the server chat packet, Making this much more efficient.
            alerts:
            {

                final String hoverMessage = MsgType.ALERT_HOVER.getMessage()
                        .replace("%description%", description)
                        .replace("%information%", information)
                        .replace("%tps%", tps);

                final String alertMessage = MsgType.ALERT_MESSAGE.getMessage()
                        .replace("%player%", playerName)
                        .replace("%check%", check)
                        .replace("%vl%", String.valueOf(vl));

                JsonBuilder jsonBuilder = new JsonBuilder(alertMessage)
                        .setHoverEvent(JsonBuilder.HoverEventType.SHOW_TEXT, hoverMessage)
                        .setClickEvent(JsonBuilder.ClickEventType.RUN_COMMAND, "/tp " + playerName)
                        .buildText();

                jsonBuilder.sendMessage(this.plugin.getAlertManager().getPlayersWithAlerts());

                if (!Config.Setting.CONSOLE_ALERT.getBoolean()) break alerts;

                Bukkit.getConsoleSender().sendMessage(alertMessage);
            }
        });
    }
}
