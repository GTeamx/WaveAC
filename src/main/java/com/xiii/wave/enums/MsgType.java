package com.xiii.wave.enums;

import com.xiii.wave.Wave;
import com.xiii.wave.utils.ChatUtils;

import java.util.List;

public enum MsgType {
    // TODO: Change it all to the config
    PREFIX(Wave.getInstance().getConfigUtils().getString("config", "prefix", "§f[§b§lWave§f]")),
    NO_PERMISSION(PREFIX.getMessage() + " " + Wave.getInstance().getConfigUtils().getString("config", "permissions.wave-nopermission-message", "unknown-command")),
    CONSOLE_COMMANDS(PREFIX.getMessage() + ChatUtils.format(Anticheat.getInstance().getThemeManager().getTheme().getString("console_commands"))),
    ALERT_MESSAGE(PREFIX.getMessage() + ChatUtils.format(Anticheat.getInstance().getThemeManager().getTheme().getString("alert_message"))),
    ALERT_HOVER(stringFromList(Anticheat.getInstance().getThemeManager().getTheme().getConfig().getStringList("alert_hover")));

    private final String message;

    MsgType(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private static String stringFromList(List<String> list) {

        StringBuilder sb = new StringBuilder();

        int size = list.size();

        for (int i = 0; i < size; i++) {

            sb.append(list.get(i));

            if (size - 1 != i) sb.append("\n");
        }

        return ChatUtils.format(sb.toString());
    }
}
