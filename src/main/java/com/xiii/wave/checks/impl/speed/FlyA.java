package com.xiii.wave.checks.impl.speed;

import com.xiii.wave.checks.annotations.Development;
import com.xiii.wave.checks.annotations.Testing;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.Packet;
import com.xiii.wave.processors.PredictionEngine;

@Testing
public class FlyA extends Check {
    public FlyA(Profile profile) {
        super(profile, CheckType.FLY, "A", "Checks for gravity modifications");
    }

    @Override
    public void handle(Packet packet) {

        final boolean exempt = profile.isExempt().fly();

        if (!packet.isMovement()) return;

        MovementData data = profile.getMovementData();

        if (!data.isOnGround() && !exempt) {

            final double prediction = data.getDeltaY() - PredictionEngine.getVerticalPrediction(data.getLastDeltaY());

            if (prediction > 1.9262653090336062E-14 && increaseBuffer() > 1) fail("" + prediction);

        } else decreaseBufferBy(1);
    }
}
