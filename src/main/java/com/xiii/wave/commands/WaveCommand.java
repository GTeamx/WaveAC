package com.xiii.wave.commands;

import com.github.retrooper.packetevents.protocol.player.User;
import com.xiii.wave.Wave;
import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import com.xiii.wave.utils.HTTPUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class WaveCommand implements CommandExecutor {

    @Override
    public synchronized boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("wave")) {

            if (sender.hasPermission(Wave.INSTANCE.configUtils.getString("config", "permissions.wave-main-command", "Wave.commands.main"))) {

                if(args[0] == null || args[0].equalsIgnoreCase("help")) {

                    sender.sendMessage("");
                    sender.sendMessage("   " + Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]"));
                    sender.sendMessage("");
                    sender.sendMessage("§fAvailable commands:");
                    sender.sendMessage("");
                    sender.sendMessage("§f/§bwave §9version §8| §fReturn the version of Wave");
                    sender.sendMessage("§f/§bwave §9brand §3<Player> §8| §fReturn the client brand of the targeted player");
                    sender.sendMessage("§f/§bwave §9playerversion §3<Player> §8| §fReturn the client version of the targeted player");
                    sender.sendMessage("§f/§bwave §9vpn §3<Player> §8| §fReturn if the targeted player is using a VPN/Proxy");
                    sender.sendMessage("");

                }

                if(args[0].equalsIgnoreCase("version")) {

                    if(sender.hasPermission(Wave.INSTANCE.configUtils.getString("config", "permissions.wave-version-command", "Wave.commands.version"))) {

                        sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]") + " You are running §b" + Wave.INSTANCE.getDescription().getVersion());

                    } else {

                        if (Wave.INSTANCE.configUtils.getString("config", "permissions.wave-nopermission-message", "unknown-command").equalsIgnoreCase("unknown-command")) sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
                        else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "permissions.wave-nopermission-message", Bukkit.spigot().getConfig().getString("messages.unknown-command")));

                    }

                }

                if(args[0].equalsIgnoreCase("brand")) {

                    if(sender.hasPermission(Wave.INSTANCE.configUtils.getString("config", "permissions.wave-brand-command", "Wave.commands.brand"))) {

                        if(args[1] != null && args[1].length() > 0) {

                            final Player target = Bukkit.getPlayer(args[1]);
                            final PlayerData data = Data.getPlayerData(target);

                            if(data.clientBrand != null && target != null && data != null) {

                                sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " Client brand of player §3" + target.getName() + " is §9" + data.clientBrand);

                            } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " §cError! Player isn't valid.");

                        } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]") + " §cError! Player isn't valid.");

                    } else {

                        if (Wave.INSTANCE.configUtils.getString("config", "permissions.wave-nopermission-message", "unknown-command").equalsIgnoreCase("unknown-command")) sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
                        else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "permissions.wave-nopermission-message", Bukkit.spigot().getConfig().getString("messages.unknown-command")));

                    }

                }

                if(args[0].equalsIgnoreCase("playerversion")) {

                    if(sender.hasPermission(Wave.INSTANCE.configUtils.getString("config", "permissions.wave-playerversion-command", "Wave.commands.playerversion"))) {

                        if(args[1] != null && args[1].length() > 0) {

                            final Player target = Bukkit.getPlayer(args[1]);
                            final PlayerData data = Data.getPlayerData(target);

                            if(data.clientVersion != null && target != null && data != null) {

                                sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " Client version of player §3" + target.getName() + " is §9" + data.clientVersion);

                            } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " §cError! Player isn't valid.");

                        } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]") + " §cError! Player isn't valid.");

                    } else {

                        if (Wave.INSTANCE.configUtils.getString("config", "permissions.wave-nopermission-message", "unknown-command").equalsIgnoreCase("unknown-command")) sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
                        else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "permissions.wave-nopermission-message", Bukkit.spigot().getConfig().getString("messages.unknown-command")));

                    }

                }

                if(args[0].equalsIgnoreCase("vpn")) {

                    if(sender.hasPermission(Wave.INSTANCE.configUtils.getString("config", "permissions.wave-vpn-command", "Wave.commands.vpn"))) {

                        if(args[1] != null && args[1].length() > 0) {

                            Player target = Bukkit.getPlayer(args[1]);

                            final String vpnKey = Wave.INSTANCE.configUtils.getString("config", "vpn-checker-key", "DISABLED");
                            if(!vpnKey.equalsIgnoreCase("DISABLED")) {

                                final String httpResponse =  HTTPUtils.readUrl("https://proxycheck.io/v2/" + target.getAddress().getHostName() + "?key=" + vpnKey + "&risk=1&vpn=1");
                                //final String riskLevel = httpResponse.substring(httpResponse.indexOf("\"risk\":"));
                                final String riskLevel = null;
                                // TODO: Fix riskLevel

                                if(httpResponse.contains("\"proxy\": \"yes\"") || httpResponse.contains("vpn")) {

                                    sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " §cVPN/Proxy detected for §3" + target.getName() + " §crisk level is §9" + riskLevel);

                                } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " §aNo VPN/Proxy were detected for §3" + target.getName() + " §arisk level is §9" + riskLevel);

                            } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) target, "prefix", "§f[§b§lWave§f]") + " §cError! VPN checker is disabled.");

                        } else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "prefix", "§f[§b§lWave§f]") + " §cError! Player isn't valid.");

                    } else {

                        if (Wave.INSTANCE.configUtils.getString("config", "permissions.wave-nopermission-message", "unknown-command").equalsIgnoreCase("unknown-command")) sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
                        else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "permissions.wave-nopermission-message", Bukkit.spigot().getConfig().getString("messages.unknown-command")));

                    }

                }

            } else {

                if (Wave.INSTANCE.configUtils.getString("config", "permissions.wave-nopermission-message", "unknown-command").equalsIgnoreCase("unknown-command")) sender.sendMessage(Bukkit.spigot().getConfig().getString("messages.unknown-command"));
                else sender.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", (Player) sender, "permissions.wave-nopermission-message", Bukkit.spigot().getConfig().getString("messages.unknown-command")));

            }

        }

        return true;

    }
}
