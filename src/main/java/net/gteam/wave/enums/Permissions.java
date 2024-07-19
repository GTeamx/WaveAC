package net.gteam.wave.enums;

public enum Permissions {
    ADMIN("wave.admin"),
    BYPASS("wave.bypass"),
    COMMAND_ALERTS("wave.commands.alerts");

    private final String permission;

    Permissions(final String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
