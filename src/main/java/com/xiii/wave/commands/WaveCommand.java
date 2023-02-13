package com.xiii.wave.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class WaveCommand implements CommandExecutor {

    @Override
    public synchronized boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("wave")) {

            // TODO: Change permission to be changed from config file
            if(sender.hasPermission("Wave.commands.wave.main")) {

                // TODO: Do Wave commands

            } else sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));

        }

        return true;
    }
}
