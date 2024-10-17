package net.gteam.wave.checks.enums;

public enum CheckType {
    FLY("Fly", "Vert. Movement", CheckCategory.MOVEMENT),
    JUMP("Jump", "Vert. Movement", CheckCategory.MOVEMENT),
    SPEED( "Speed", "Hori. Movement", CheckCategory.MOVEMENT),
    MOTION("Motion","Gene. Movement", CheckCategory.MOVEMENT),
    AIMASSIST("AimAssist","Aim Movement", CheckCategory.COMBAT);

    private final String checkCodeName;
    private final String checkDisplayName;
    private final CheckCategory checkCategory;

    CheckType(final String checkCodeName, final String checkDisplayName, final CheckCategory checkCategory) {
        this.checkCodeName = checkCodeName;
        this.checkDisplayName = checkDisplayName;
        this.checkCategory = checkCategory;
    }

    public String getCheckCodeName() {
        return checkCodeName;
    }

    public String getCheckDisplayName() {
        return checkDisplayName;
    }

    public CheckCategory getCheckCategory() {
        return checkCategory;
    }
}