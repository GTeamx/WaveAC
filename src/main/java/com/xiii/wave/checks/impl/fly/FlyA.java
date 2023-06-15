package com.xiii.wave.checks.impl.fly;

import com.xiii.wave.checks.annotations.Experimental;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.PredictionEngine;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;
import com.xiii.wave.utils.MathUtils;

/*
FALSES: Block glitching/ghost blocks, levitation potion effect, handle damage better, teleport, glitching in a boat/block, slime standing still, block placing below you, weird jump
BYPASSES: Ground spoof
 */

@Experimental
public class FlyA extends Check {
    public FlyA(final Profile profile) {
        super(profile, CheckType.FLY, "A", "Checks for gravity modifications");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        final boolean exempt = profile.isExempt().isFly() || profile.isExempt().isWater(150L) || profile.isExempt().isLava(150L) || profile.isExempt().isTrapdoor_door() || profile.isExempt().isCobweb(50L) || profile.isExempt().isCake() || profile.getVehicleData().isRiding(150L) || profile.isExempt().isJoined(50L);

        final boolean damageExempt = profile.isExempt().tookDamage(300L);

        if (!clientPlayPacket.isMovement()) return;

        final MovementData movementData = profile.getMovementData();

        final double deltaY = movementData.getDeltaY();

        if (!exempt && !damageExempt && deltaY >= 0 && movementData.getAirTicks() == 1 && deltaY != MathUtils.JUMP) fail("emy=" + MathUtils.JUMP + " my=" + deltaY);

        if (!movementData.isOnGround()) {

            final double prediction = deltaY - PredictionEngine.getVerticalPrediction(profile, movementData.getLastDeltaY(), deltaY);

            double prediction_limit = 1.9262653090336062E-14;

            if (profile.isExempt().isClimable(50L)) {

                if (deltaY >= 0) prediction_limit = 0.08075199932861336; //TODO: might have slightly changed in newer version
                else prediction_limit = 0.07540000438690189;
            }

            //For falling on a ladder then air, then ladder...
            if (profile.isExempt().isClimable(350L)) {

                if (MathUtils.decimalRound(deltaY, 4) == 0.1176 || MathUtils.decimalRound(deltaY, 4) == -0.15) decreaseBufferBy(1);
            }

            //Handle first jump
            boolean postExempt;
            if (!exempt && !damageExempt && movementData.getAirTicks() == 1) {
                if (deltaY != MathUtils.JUMP) {
                    increaseBuffer();
                    postExempt = false;
                }
                else {
                    decreaseBufferBy(1);
                    postExempt = true;
                }
            } else postExempt = false;


            if (!exempt && !postExempt && prediction > prediction_limit && increaseBuffer() > 1) fail("pred=" + prediction + " my=" + deltaY);

        } else decreaseBufferBy(1);
    }

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {}
}
