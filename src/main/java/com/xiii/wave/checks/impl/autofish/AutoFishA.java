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
BYPASSES:
 */

public class AutoFishA extends Check {
    public AutoFishA(final Profile profile) {
        super(profile, CheckType.AUTOFISH, "A", "Impossible reaction time pattern");
    }

    private long lastReactionTime = 10000L;

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {}

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {

        final FishingData fishingData = profile.getFishingData();

        if (fishingData.getFishingState().equals(FishingState.CAUGHT)) {

            final long reactionTime = Math.abs(this.lastReactionTime - (System.currentTimeMillis() - fishingData.getLastBite()));

            if (reactionTime <= 100) {
                if(increaseBuffer() > 4) fail("delay=" + reactionTime);
            } else decreaseBufferBy(1);

            this.lastReactionTime = (System.currentTimeMillis() - fishingData.getLastBite());
        }
    }
}
