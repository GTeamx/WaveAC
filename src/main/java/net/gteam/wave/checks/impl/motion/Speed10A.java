package net.gteam.wave.checks.impl.motion;

import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

@Development
public class Speed10A extends Check {

    public Speed10A(final Profile profile) {
        super(profile, CheckType.SPEED, "SP10A", "Impossible XZ speed");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        final MovementData movementData = profile.getMovementData();

        final double[] predictionValues = movementData.getPredictionProcessor().getHorizontalPredictionOld(profile);

        final double diffXZ = movementData.getDeltaXZ();

        if (predictionValues[1] > 1E-7 && predictionValues[0] >= movementData.getDeltaXZ()) {

            if (increaseBufferBy(1) > 2) {

                fail("diffXZ=" + diffXZ);

                debug("\nxz=" + predictionValues[0] + "\nd=" + predictionValues[1]);
            }

        } else {

            decreaseBuffer();

        }
    }

    @Override
    public void handle(final ServerPlayPacket ignored) {}
}
