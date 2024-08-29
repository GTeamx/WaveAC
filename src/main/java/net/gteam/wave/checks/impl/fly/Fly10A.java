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
        final double predictedDeltaY = movementData.getPredictionProcessor().getPredictedDeltaY();
        final double math = Math.abs(predictedDeltaY - movementData.getDeltaY());
        final boolean invalid = math > 1E-10;

        if (!movementData.isOnGround() && profile.getTeleportData().getTeleportTicks() > 2 && movementData.getFlyTicks() > 5 && movementData.getSlimeTicks() > 3 && movementData.getHoneyTicks() > 3 && movementData.getClimbableTicks() > 0 && movementData.getLiquidTicks() > 2 && movementData.getBubbleTicks() > 2) {

            if (invalid && increaseBuffer(2) > 1) fail("predDiff=" + math + System.lineSeparator() + "dY=" + movementData.getDeltaY() + System.lineSeparator() + "pY=" + predictedDeltaY);

        } else decreaseBufferBy(0.2);
    }

    @Override
    public void handle(final ServerPlayPacket ignored) {}
}