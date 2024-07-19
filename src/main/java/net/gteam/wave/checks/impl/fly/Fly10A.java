package net.gteam.wave.checks.impl.fly;

import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

@Development
public class Fly10A extends Check {
    public Fly10A(final Profile profile) {
        super(profile, CheckType.FLY, "FL10A", "Checks for gravity modifications");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        if (!clientPlayPacket.isMovement()) return;

        final MovementData movementData = this.profile.getMovementData();

        if (movementData.getNearGroundTicks() <= 0 && movementData.getBlocksAboveTicks() > 0 && movementData.getHalfBlocksTicks() > 0) {

            final double predictionDifference = movementData.getPredictionProcessor().getPredictedDeltaY() - movementData.getDeltaY();

            if (predictionDifference > 9.71445146547012E-12) fail("predDiff=" + predictionDifference);
        } else decreaseBuffer();
    }

    @Override
    public void handle(final ServerPlayPacket ignored) {}
}