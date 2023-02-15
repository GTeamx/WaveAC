package com.xiii.wave.commands;

import com.xiii.wave.Wave;
import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public final class AlertsCommand implements CommandExecutor {

    @Override
    public synchronized boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("alerts")) {

            if(sender.hasPermission(Wave.INSTANCE.configUtils.getString("config", "permissions.alerts-command", "Wave.commands.alerts"))) {

                final PlayerData data = Data.getPlayerData((Player) sender);

                if(data.alertsState) {

                    sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]") + " Alerts output §cdisabled");
                    data.alertsState = false;

                } else {

                    sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]") + " Alerts output §aenabled");
                    data.alertsState = true;

                }

            } else {

                if(Wave.INSTANCE.configUtils.getString("config", "permissions.wave-nopermission-message", "unknown-command").equalsIgnoreCase("unknown-command")) sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
                else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "permissions.wave-nopermission-message", Bukkit.spigot().getConfig().getString("messages.unknown-command")));

            }

        }

        return true;

    }
}
