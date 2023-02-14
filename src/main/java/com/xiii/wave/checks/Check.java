package com.xiii.wave.checks;

import com.github.retrooper.packetevents.event.CancellableEvent;
import com.github.retrooper.packetevents.protocol.player.User;
import com.xiii.wave.Wave;
import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import com.xiii.wave.exempt.ExemptType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Check {

    public boolean checkEnabled;

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

    public PlayerData data;

    public boolean isExempt(final ExemptType exemptType) {
        return data.getExempt().isExempt(exemptType);
    }

    public boolean isExempt(final ExemptType... type) {
        return data.getExempt().isExempt(type);
    }

    public void flag(final CancellableEvent packet, final Object values) {

        if(checkEnabled) {

            // Update buffers
            currentBuffer += addBuffer;

            // Trigger alert
            if(currentBuffer > maxBuffer || maxBuffer == 0) {

                if(packet != null) packet.setCancelled(true);

                final CheckInfo checkInfo = this.getClass().getAnnotation(CheckInfo.class);
                final String hoverText = "§f* " + checkInfo.checkDescription() + "\n" + "\n" + "§f* " + values + "\n" + "§f* §b" + currentBuffer + "§f/§b" + maxBuffer;

                addFlag(checkName);

                for(final Player p : Bukkit.getOnlinePlayers()) {

                    if(Data.getPlayerData(p).alertsState) {

                        final TextComponent alertMessage;
                        if(checkState.equals(CheckState.EXPERIMENTAL)) {

                            alertMessage = new TextComponent(Wave.INSTANCE.configUtils.getStringConverted("config", data.getPlayer(), "prefix", "§f[§b§lWave§f]") + " §b" + data.player.getName() + " §7flagged §b△" + checkName + " §7(§bx" + getFlags(data.player.getName(), checkName) + "§7)");

                        } else {

                            alertMessage = new TextComponent(Wave.INSTANCE.configUtils.getStringConverted("config", data.getPlayer(), "prefix", "§f[§b§lWave§f]") + " §b" + data.player.getName() + " §7flagged §b" + checkName + " §7(§bx" + getFlags(data.player.getName(), checkName) + "§7)");

                        }

                        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverText).create()));
                        p.spigot().sendMessage(alertMessage);

                    }
                }

            }

        }

    }

    private void addFlag(final String check) {

        HashMap<String, Integer> currentFlags = data.flagList.get(data.player.getName());

        if(currentFlags == null) currentFlags = new HashMap<>();
        currentFlags.put(check, currentFlags.getOrDefault(check, 0) + 1);
        data.flagList.put(data.player.getName(), currentFlags);

    }

    private int getFlags(final String flagName, final String flagType) {

        if(data.flagList.get(flagName) == null) return 0;
        return data.flagList.get(flagName).getOrDefault(flagType, 0);

    }

    public void removeBuffer() {

        if(currentBuffer <= 0 || removeBuffer <= 0) currentBuffer = 0;
        if(currentBuffer >= removeBuffer) currentBuffer -= removeBuffer;
        else currentBuffer = 0;

    }

}
