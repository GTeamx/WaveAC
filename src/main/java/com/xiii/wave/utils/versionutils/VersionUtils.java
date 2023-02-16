package com.xiii.wave.utils.versionutils;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.xiii.wave.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public final class VersionUtils {

    public static String getClientVersionAsString(Player player) {
        return PacketEvents.getAPI().getPlayerManager().getClientVersion(player).name().replaceAll("_", ".").substring(2);
    }

    public static ClientVersion getClientVersion(Player player) {
        return PacketEvents.getAPI().getPlayerManager().getClientVersion(player);
    }

    public static ServerVersion getServerVersion() {
        String serverPackageName = Bukkit.getServer().getClass().getPackage().getName();

        ServerVersion version;

        try {

            version = ServerVersion.valueOf(serverPackageName.substring(serverPackageName.lastIndexOf(".") + 1).trim());

        } catch (IllegalArgumentException e) {

            version = ServerVersion.V_1_8;
        }

        return version;
    }
    // TODO: Fix getServerVersion

}
