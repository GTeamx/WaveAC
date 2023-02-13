package com.xiii.wave.commands;

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

            // TODO: Switch permission to be custom from config file
            if(sender.hasPermission("Wave.commands.alerts")) {

                // TODO: Toggle alerts & test.
                final PlayerData data = Data.getPlayerData(Bukkit.getPlayer("?"));

                if(data.alertsList.contains((Player) sender)) {

                    // TODO: Get prefix from config
                    sender.sendMessage("%prefix% Alerts output §cdisabled");
                    data.alertsList.remove((Player) sender);

                } else {

                    sender.sendMessage("%prefix% Alerts output §aenabled");
                    data.alertsList.add((Player) sender);

                }

            } else sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));

        }

        return true;
    }
}
