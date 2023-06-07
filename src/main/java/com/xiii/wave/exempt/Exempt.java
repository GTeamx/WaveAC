package com.xiii.wave.exempt;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.utils.BetterStream;

public class Exempt {

    private final Profile profile;

    public Exempt(Profile profile) {
        this.profile = profile;
    }

    private boolean fly, liquid;

    public void handleExempts(long timeStamp) {

        MovementData movementData = profile.getMovementData();

        //Fly
        this.fly = movementData.getLastFlyingAbility() < (20*5); //5s

        //Water Liquid
        this.liquid = BetterStream.anyMatch(movementData.getNearbyBlocks(), mat -> mat.toString().contains("WATER"));
    }

    public boolean isFly() {
        return fly;
    }

    public boolean isLiquid() {
        return liquid;
    }
}
