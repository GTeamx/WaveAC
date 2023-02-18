package com.xiii.wave.listener;

import com.xiii.wave.Wave;
import com.xiii.wave.enums.MsgType;
import com.xiii.wave.enums.Permissions;
import com.xiii.wave.files.Config;
import com.xiii.wave.utils.HTTPUtils;
import com.xiii.wave.utils.TaskUtils;
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

        final String vpnKey = Wave.getInstance().getConfiguration().getString("vpn-checker-key");
        if (!vpnKey.equalsIgnoreCase("DISABLED")) {

            final String httpResponse = HTTPUtils.readUrl("https://proxycheck.io/v2/" + e.getPlayer().getAddress().getHostName() + "?key=" + vpnKey + "&vpn=3");
            //final String riskLevel = httpResponse.substring(httpResponse.indexOf("\"risk\":"));
            final String riskLevel = "Unknown";
            // TODO: Fix riskLevel

            if (httpResponse.contains("\"proxy\": \"yes\"") || httpResponse.contains("\"vpn\": \"yes\"") || httpResponse.contains("\"WaveACVPNCheckResult\": \"REJECTED\"") || httpResponse.contains("blacklist") || httpResponse.contains("compromised")) {

                TaskUtils.task(() -> e.getPlayer().kickPlayer(MsgType.PREFIX.getMessage() + " §cVPN/Proxy"));

            }

            if (Config.Setting.TOGGLE_ALERTS_ON_JOIN.getBoolean() && player.hasPermission(Permissions.AUTO_ALERTS.getPermission())) {

                this.plugin.getAlertManager().addPlayerToAlerts(player.getUniqueId());
                player.sendMessage(MsgType.PREFIX.getMessage() + " Alerts outptut §aenabled");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(PlayerQuitEvent e) {
        this.plugin.getProfileManager().removeProfile(e.getPlayer());
    }
}
