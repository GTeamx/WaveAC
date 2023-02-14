package com.xiii.wave.data;

import com.github.retrooper.packetevents.protocol.player.User;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Data {

    private static List<PlayerData> playerData = new ArrayList<>();

    public static void registerPlayerData(Player player) { playerData.add(new PlayerData(player)); }

    public static PlayerData getPlayerData(Player player) {

        if(player == null) return null;

        for(int i = 0; i < playerData.size(); i++) {

            PlayerData data = playerData.get(i);

            if(data.getUuid() == player.getUniqueId()) return data;

        }

        return null;

    }

    public static void clearPlayerData(Player player) {

        for(PlayerData data : playerData) {

            if(data.getUuid().equals(player.getUniqueId())) playerData.remove(getPlayerData(player));

        }

    }

    public static void clearData() {

        playerData.clear();

    }

}
