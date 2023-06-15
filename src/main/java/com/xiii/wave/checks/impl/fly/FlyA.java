package com.xiii.wave.checks.impl.fly;

import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.PredictionEngine;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

/*
FALSES: Block glitching/ghost blocks, levitation potion effect, handle damage better, teleport, glitching in a boat/block, slime standing still, block placing below you
BYPASSES: Ground spoof
 */

public class FlyA extends Check {
    public FlyA(final Profile profile) {
        super(profile, CheckType.FLY, "A", "Checks for gravity modifications");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        final boolean exempt = profile.isExempt().isFly() || profile.isExempt().isWater(150L) || profile.isExempt().isLava(150L) || profile.isExempt().isTrapdoor_door() || profile.isExempt().isCobweb(50L) || profile.isExempt().isCake() || profile.getVehicleData().isRiding(150L) || profile.isExempt().isJoined(5000L);

        if (!clientPlayPacket.isMovement()) return;

        final MovementData movementData = profile.getMovementData();

        final double deltaY = movementData.getDeltaY();

        //Handle damage
        int maxBuffer;
        if (profile.isExempt().tookDamage(300L) || profile.isExempt().isClimable(50L)) maxBuffer = 3;
        else maxBuffer = 1;

        if (!movementData.isOnGround()) {

            final double prediction = deltaY - PredictionEngine.getVerticalPrediction(movementData.getLastDeltaY());

            double prediction_limit = 1.9262653090336062E-14;

            if (profile.isExempt().isClimable(50L)) {
                if (deltaY >= 0) prediction_limit = 0.08075199932861336; //TODO: might have slightly changed in newer version
                else prediction_limit = 0.07540000438690189;
                if (deltaY == 0.1176000022888175 || deltaY == -0.1499999999999858) decreaseBufferBy(1);
            }

            if (!exempt && prediction > prediction_limit && increaseBuffer() > maxBuffer) fail("pred=" + prediction + " my=" + deltaY);

        } else decreaseBufferBy(1);
    }

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {}
}
