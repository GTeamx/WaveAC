package com.xiii.wave.checks.impl.ground;

import com.xiii.wave.checks.annotations.Experimental;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

/*
FALSES:
BYPASSES:
 */


@Experimental
public class GroundB extends Check {

    private double highestY;

    public GroundB(final Profile profile) {
        super(profile, CheckType.GROUND, "B", "Checks for invalid fall distance");
    }

    @Override
    public void handle(ClientPlayPacket clientPlayPacket) {
        if (!clientPlayPacket.isMovement()) return;
        final float serverFallDistance;
        MovementData data = profile.getMovementData();
        if (data.getDeltaY() >= 0) {
            serverFallDistance = 0;
            highestY = data.getLocation().clone().getY();
        } else {
            if (data.isServerGround()) highestY = data.getLocation().clone().getY();
            serverFallDistance = (float) (highestY - data.getLocation().clone().getY());
        }
        final boolean invalid = Math.abs(serverFallDistance - data.getFallDistance()) > 0.2 && !data.isServerGround();

        final boolean exempt = profile.isExempt().isFly() || profile.isExempt().isTrapdoor_door() || profile.isExempt().isWater(150L) || profile.isExempt().isLava(150L) || profile.isExempt().isJoined(5000L);
        if (invalid && !exempt) {
            if (increaseBufferBy(1) > 2) fail("s=" + serverFallDistance + " c=" + data.getFallDistance() + " d=" + Math.abs(serverFallDistance - data.getFallDistance()));
        } else decreaseBufferBy(1);
    }

    @Override
    public void handle(ServerPlayPacket serverPlayPacket) {}
}
