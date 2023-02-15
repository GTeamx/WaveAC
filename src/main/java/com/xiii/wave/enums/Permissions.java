package com.xiii.wave.enums;

import com.xiii.wave.Wave;

public enum Permissions {
    VPN_COMMAND(Wave.getInstance().getConfigUtils().getString("config", "permissions.wave-main-command", "Wave.commands.main")),
    PLAYERVERSION_COMMAND(Wave.getInstance().getConfigUtils().getString("config", "permissions.wave-main-command", "Wave.commands.main")),
    BRAND_COMMAND(Wave.getInstance().getConfigUtils().getString("config", "permissions.wave-main-command", "Wave.commands.main")),
    VERSION_COMMAND(Wave.getInstance().getConfigUtils().getString("config", "permissions.wave-main-command", "Wave.commands.main")),
    MAIN_COMMAND_ALERTS(Wave.getInstance().getConfigUtils().getString("config", "permissions.wave-main-command", "Wave.commands.main"));

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
