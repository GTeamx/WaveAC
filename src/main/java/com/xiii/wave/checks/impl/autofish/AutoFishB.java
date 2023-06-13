package com.xiii.wave.checks.impl.autofish;

import com.xiii.wave.checks.annotations.Experimental;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.enums.FishingState;
import com.xiii.wave.playerdata.data.impl.FishingData;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

/*
FALSES:
BYPASSES
 */

@Experimental
public class AutoFishB extends Check {
    public AutoFishB(final Profile profile) {
        super(profile, CheckType.AUTOFISH, "B", "Impossible reaction time");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {}

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {

        final FishingData fishingData = profile.getFishingData();

        if (fishingData.getFishingState().equals(FishingState.CAUGHT)) {

            fail("rtime=" + (System.currentTimeMillis() - fishingData.getLastBite()));

            if ((System.currentTimeMillis() - fishingData.getLastBite()) <= 300) {
                if (increaseBuffer() > 2) fail("react=" + (System.currentTimeMillis() - fishingData.getLastBite()));
            } else decreaseBufferBy(1);
        }
    }
}
