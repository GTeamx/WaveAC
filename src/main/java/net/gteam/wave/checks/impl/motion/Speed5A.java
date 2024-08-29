package net.gteam.wave.checks.impl.motion;

import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

public class Speed5A extends Check {

    public Speed5A(final Profile profile) {
        super(profile, CheckType.SPEED, "SP5A", "Impossible XZ speed");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        final MovementData movementData = profile.getMovementData();

        final double diffXZ = movementData.getDeltaXZ();

        if (diffXZ > 0.62) fail("diffXZ=" + diffXZ);

    }

    @Override
    public void handle(final ServerPlayPacket clientPlayPacket) {}
}
