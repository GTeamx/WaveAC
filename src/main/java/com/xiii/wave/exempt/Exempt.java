package com.xiii.wave.exempt;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.nms.NmsInstance;
import com.xiii.wave.nms.NmsManager;
import com.xiii.wave.playerdata.data.impl.MovementData;

public class Exempt {

    private final Profile profile;

    public Exempt(Profile profile) {
        this.profile = profile;
    }

    private boolean fly;

    public void handleExempts(long timeStamp) {

        MovementData movementData = profile.getMovementData();

        //Fly
        this.fly = movementData.getLastFlyingAbility() < 600;
    }

    public boolean fly() {
        return this.fly;
    }
}
