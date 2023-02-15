package com.xiii.wave.utils.versionutils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import org.bukkit.entity.Player;

public final class VersionUtils {

    public static String getClientVersionAsString(Player player) {
        return PacketEvents.getAPI().getPlayerManager().getClientVersion(player).name().replaceAll("_", ".").substring(2);
    }

    public static ClientVersion getClientVersion(Player player) {
        return PacketEvents.getAPI().getPlayerManager().getClientVersion(player);
    }

    public static ServerVersion getServerVersion() {
        return PacketEvents.getAPI().getServerManager().getVersion();
    }

}
