package com.xiii.wave.enums;

import com.xiii.wave.Wave;
import com.xiii.wave.utils.ChatUtils;

import java.util.List;

public enum MsgType {
    // ChatUtils.format(Wave.getInstance().getConfiguration().getString("prefix"))
    PREFIX(ChatUtils.format(Wave.getInstance().getConfiguration().getString("prefix"))),
    NO_PERMISSION(ChatUtils.format(Wave.getInstance().getConfiguration().getString("messages.no-permission"))),
    CONSOLE_COMMANDS(ChatUtils.format(PREFIX.getMessage()) + " " + ChatUtils.format(Wave.getInstance().getConfiguration().getString("messages.console-command"))),
    ALERT_MESSAGE(ChatUtils.format(PREFIX.getMessage()) + " " + ChatUtils.format(Wave.getInstance().getConfiguration().getString("messages.alert-message"))),
    ALERT_HOVER(stringFromList(Wave.getInstance().getConfiguration().getStringList("messages.alert-hover")));

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
