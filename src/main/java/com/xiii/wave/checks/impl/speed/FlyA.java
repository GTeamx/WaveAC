package com.xiii.wave.checks.impl.speed;

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

        final boolean exempt = profile.isExempt().isFly() || profile.isExempt().isWater(150L) || profile.isExempt().isLava(150L) || profile.isExempt().isClimable(150L) || profile.isExempt().isCobweb(100L);

        if (!packet.isMovement()) return;

        MovementData data = profile.getMovementData();

        if (!data.isOnGround() && !exempt) {

            final double prediction = data.getDeltaY() - PredictionEngine.getVerticalPrediction(data.getLastDeltaY());

            if (prediction > 1.9262653090336062E-14 && increaseBuffer() > 1) fail("pred=" + prediction + " my=" + data.getDeltaY());

        } else decreaseBufferBy(1);
    }
}
