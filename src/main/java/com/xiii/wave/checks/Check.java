package com.xiii.wave.checks;

import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import io.github.retrooper.packetevents.event.eventtypes.CancellableEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Check {

    public String checkName;
    public String checkDescription;
    public CheckCategory checkCategory;
    public CheckState checkState;

    public boolean canBan;
    public boolean canKick;
    public boolean isSilent;

    public int punishVL;

    public double maxBuffer;
    public double removeBuffer;
    public double addBuffer;
    public double currentBuffer;

    public static PlayerData data;

    public void flag(CancellableEvent packet, Object values) {

        // Update buffers
        currentBuffer += addBuffer;

        // Trigger alert
        if(currentBuffer > maxBuffer || maxBuffer == 0) {

            if(packet != null) packet.setCancelled(true);

            final CheckInfo checkInfo = this.getClass().getAnnotation(CheckInfo.class);
            final String hoverText = "§f* " + checkInfo.checkDescription() + "\n" + "\n" + "§f* §b" + values + "\n" + "§f* §b" + currentBuffer + "§f/§b" + maxBuffer;
            final String chatPrefix = "§f[§bWave§f] ";
            final PlayerData alertsData = Data.getPlayerData(Bukkit.getPlayer("?"));
            // TODO: Get prefix from config file
            //final String prefix = Vengeance.instance.configUtils.getStringFromConfig("config", "prefix","§4§lVengeance §8»§f");

            addFlag(checkName);

            assert alertsData != null;
            for (final Player p : alertsData.alertsList) {

                final TextComponent alertMessage;
                if(checkState.equals(CheckState.EXPERIMENTAL)) {

                    alertMessage = new TextComponent(chatPrefix + " §b" + data.player.getName() + " §7flagged §b△" + checkName + " §7(§bx" + getFlags(data.player.getName(), checkName) + "§7)");

                } else {

                    alertMessage = new TextComponent(chatPrefix + " §b" + data.player.getName() + " §7flagged §b" + checkName + " §7(§bx" + getFlags(data.player.getName(), checkName) + "§7)");

                }
                alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                p.spigot().sendMessage(alertMessage);
            }

        }

    }

    private void addFlag(String check) {

        HashMap<String, Integer> currentFlags = data.flagList.get(data.player.getName());

        if(currentFlags == null) currentFlags = new HashMap<>();
        currentFlags.put(check, currentFlags.getOrDefault(check, 0) + 1);
        data.flagList.put(data.player.getName(), currentFlags);

    }

    private int getFlags(String flagName, String flagType) {

        if(data.flagList.get(flagName) == null) return 0;
        return data.flagList.get(flagName).getOrDefault(flagType, 0);

    }

    public void removeBuffer() {

        if(currentBuffer <= 0 || removeBuffer <= 0) currentBuffer = 0;
        if(currentBuffer >= removeBuffer) currentBuffer -= removeBuffer;
        else currentBuffer = 0;

    }

}
