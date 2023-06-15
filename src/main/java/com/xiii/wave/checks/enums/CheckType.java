package com.xiii.wave.checks.enums;

public enum CheckType {
    AIM("Aim", CheckCategory.COMBAT),
    AUTOCLICKER("AutoClicker", CheckCategory.COMBAT),
    BADPACKETS("BadPackets", CheckCategory.WORLD),
    FLY("Fly", CheckCategory.MOVEMENT),
    KILLAURA("KillAura", CheckCategory.COMBAT),
    SCAFFOLD("Scaffold", CheckCategory.WORLD),
    SPEED("Speed", CheckCategory.MOVEMENT),
    GROUND("Ground", CheckCategory.MOVEMENT),
    MOTION("Motion", CheckCategory.MOVEMENT),
    NOFALL("NoFall", CheckCategory.MOVEMENT),
    JESUS("Jesus", CheckCategory.MOVEMENT),
    VEHICLE("Vehicle", CheckCategory.MOVEMENT),
    ELYTRA("Elytra", CheckCategory.MOVEMENT),
    TIMER("Timer", CheckCategory.WORLD),
    OMNISPRINT("OmniSprint", CheckCategory.MOVEMENT),
    NOSLOW("NoSlow", CheckCategory.MOVEMENT),
    REACH("Reach", CheckCategory.COMBAT),
    VELOCITY("Velocity", CheckCategory.COMBAT),
    INVENTORY("Inventory", CheckCategory.WORLD),
    INTERACT("Interact", CheckCategory.WORLD),
    FASTCLIMB("FastClimb", CheckCategory.MOVEMENT),
    VPN("VPN/Proxy", CheckCategory.PACKET),
    AUTOFISH("AutoFish", CheckCategory.WORLD),
    HITBOX("Hitbox", CheckCategory.COMBAT);

    private final String checkName;
    private final CheckCategory checkCategory;

    CheckType(String checkName, CheckCategory checkCategory) {
        this.checkName = checkName;
        this.checkCategory = checkCategory;
    }

    public String getCheckName() {
        return checkName;
    }

    public CheckCategory getCheckCategory() {
        return checkCategory;
    }
}
