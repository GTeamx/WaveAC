package com.xiii.wave.commands.subcommands;

import com.xiii.wave.Wave;
import com.xiii.wave.commands.SubCommand;
import com.xiii.wave.enums.MsgType;
import com.xiii.wave.enums.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AlertsCommand extends SubCommand {

    private final Wave plugin;

    public AlertsCommand(Wave plugin) {
        this.plugin = plugin;
    }

    @Override
    protected String getName() {
        return "alerts";
    }

    @Override
    protected String getDescription() {
        return "Toggle the alerts";
    }

    @Override
    protected String getSyntax() {
        return "alerts";
    }

    @Override
    protected String getPermission() {
        return Permissions.ALERTS_COMMAND.getPermission();
    }

    @Override
    protected int maxArguments() {
        return 1;
    }

    @Override
    protected boolean canConsoleExecute() {
        return false;
    }

    @Override
    protected void perform(CommandSender sender, String[] args) {

        final UUID uuid = ((Player) sender).getUniqueId();

        if (this.plugin.getAlertManager().hasAlerts(uuid)) {

            this.plugin.getAlertManager().removePlayerFromAlerts(uuid);

            sender.sendMessage(MsgType.PREFIX.getMessage() + " Alerts output §cdisabled");

        } else {

            this.plugin.getAlertManager().addPlayerToAlerts(uuid);

            sender.sendMessage(MsgType.PREFIX.getMessage() + " Alerts output §aenabled");
        }
    }
}
