package com.xiii.wave.checks.impl.ground;

import com.xiii.wave.checks.annotations.Experimental;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;
import com.xiii.wave.utils.BetterStream;

/*
FALSES: Slime, Boats, Teleporting may false
BYPASSES:
 */


@Experimental
public class GroundA extends Check {

    public GroundA(final Profile profile) {
        super(profile, CheckType.GROUND, "A", "Checks for invalid ground states");
    }

    @Override
    public void handle(ClientPlayPacket clientPlayPacket) {

        if (!clientPlayPacket.isMovement()) return;

        MovementData data = profile.getMovementData();

        final boolean invalid = (data.isServerGround() && BetterStream.anyMatch(data.getNearbyBlocks(), material -> !material.toString().equalsIgnoreCase("AIR") || !(material.toString().contains("VOID") && material.toString().contains("AIR")))) != data.isOnGround();

        final boolean exempt = profile.isExempt().isFly() || profile.isExempt().isTrapdoor_door() || profile.isExempt().isWater(150L) || profile.isExempt().isLava(150L) || profile.isExempt().isJoined(5000L);

        if (invalid && !exempt) {
            if (increaseBufferBy(1) > 2) fail("s=" + data.isServerGround() + " c=" + data.isOnGround());
        } else decreaseBufferBy(0.75);
    }

    @Override
    public void handle(ServerPlayPacket serverPlayPacket) {}
}
