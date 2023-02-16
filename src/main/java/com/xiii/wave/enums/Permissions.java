package com.xiii.wave.enums;

import com.xiii.wave.Wave;

public enum Permissions {
    VPN_COMMAND(Wave.getInstance().getConfiguration().getString("permissions.wave-vpn-command")),
    PLAYERVERSION_COMMAND(Wave.getInstance().getConfiguration().getString("permissions.wave-commands-playerversion")),
    BRAND_COMMAND(Wave.getInstance().getConfiguration().getString("permissions.wave-commands-brand")),
    VERSION_COMMAND(Wave.getInstance().getConfiguration().getString("permissions.wave-version-command")),
    ALERTS_COMMAND(Wave.getInstance().getConfiguration().getString("permissions.wave-alerts-command")),
    AUTO_ALERTS(Wave.getInstance().getConfiguration().getString("permissions.wave-auto-alerts")),
    BYPASS(Wave.getInstance().getConfiguration().getString("permissions.wave-bypass"));

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
