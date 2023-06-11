package com.xiii.wave.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.xiii.wave.Wave;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;

public class ClientBrandListener extends SimplePacketListenerAbstract {

    private final Wave plugin;

    public ClientBrandListener(Wave plugin) {
        super(PacketListenerPriority.LOWEST);

        this.plugin = plugin;

        PacketEvents.getAPI().getEventManager().registerListener(this);

    }

    public void handle(ClientPlayPacket clientPlayPacket) {

        /*

        // ClientBrandListener
        if(packet.getType() == PacketType.Play.Client.PLUGIN_MESSAGE) {

            final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
            final WrapperPlayClientPluginMessage wrappedPacketInCustomPayload = new WrapperPlayClientPluginMessage(event);
            final String brand = ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(new String(wrappedPacketInCustomPayload.getData(), StandardCharsets.UTF_8).substring(1)).replaceAll(""));
            final Profile profile = Wave.getInstance().getProfileManager().getProfile(packet.pl);

            profile.clientBrand = brand;
            data.clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(event.getUser()).name().replaceAll("_", ".").substring(2);

            for (Player p : Bukkit.getOnlinePlayers()) {

                if (p.hasPermission(Wave.INSTANCE.configUtils.getStringConverted("config", Bukkit.getPlayer(event.getUser().getName()), "permissions.brand-alerts", "Wave.alerts.brand"))) p.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", data.getPlayer(), "prefix", "§f[§b§lWave§f]") + " §b" + event.getUser().getName() + " §fhas joined using §b" + data.clientBrand + " §fin §b" + data.clientVersion);

            }
        }

         */

    }

    // TODO: Do ClientBraNDlISTENER

}
