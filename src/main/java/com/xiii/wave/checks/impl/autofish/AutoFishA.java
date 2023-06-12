package com.xiii.wave.checks.impl.autofish;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
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

    public AutoFishA(Profile profile) {
        super(profile, CheckType.AUTOFISH, "A", "Impossible reaction time pattern");
    }

    private long lastReactionTime = 10000L;

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {}

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {

        if (serverPlayPacket.getType() == PacketType.Play.Server.ENTITY_METADATA) {

            final FishingData fishingData = profile.getFishingData();

            if (fishingData.getFishingState().equals(FishingState.CAUGHT)) {

                final long reactionTime =  Math.abs(this.lastReactionTime - (serverPlayPacket.getTimeStamp() - fishingData.getLastBite()));

                if (reactionTime == 0) fail("delay=" + reactionTime);

                if (reactionTime <= 99 && increaseBuffer() > 2) fail("delay=" + reactionTime);
                else decreaseBufferBy(1);

                this.lastReactionTime = (serverPlayPacket.getTimeStamp() - fishingData.getLastBite());
            }
        }
    }
}
