package com.xiii.wave.checks.impl.speed;

import com.xiii.wave.checks.annotations.Experimental;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.PredictionEngine;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

/*
FALSES: Teleporting
BYPASSES:
 */

@Experimental
public class SpeedA extends Check {
    public SpeedA(final Profile profile) {
        super(profile, CheckType.SPEED, "A", "Checks for irregular movement");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        if (!clientPlayPacket.isMovement()) return;

        final boolean exempt = profile.isExempt().isFly() || profile.isExempt().isWater(50L) || profile.isExempt().isWall(5);

        final MovementData data = profile.getMovementData();

        final double[] predictions = PredictionEngine.getHorizontalPrediction(profile);

        if (!exempt && data.getDeltaXZ() >= predictions[0] && predictions[1] > 7.0E-5 && data.getDeltaX() != 0 && predictions[0] != 0 && data.getDeltaX() > 0.04) {

            if (increaseBuffer() > 4 || predictions[1] > 1) fail("xz=" + Math.abs(data.getDeltaXZ() - predictions[0]) + " s=" + predictions[1]);
        } else decreaseBufferBy(1);
    }

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {}
}
