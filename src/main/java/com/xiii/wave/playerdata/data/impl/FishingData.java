package com.xiii.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.playerdata.data.enums.FishingState;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

public class FishingData implements Data {

    private final Profile profile;

    private FishingState fishingState = FishingState.UNKNOWN;

    private long lastBite = 10000L, lastBiteFail = 10000L, lastCaught = 10000L;

    public FishingData(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process(final ClientPlayPacket clientPlayPacket) {
    }

    @Override
    public void process(final ServerPlayPacket serverPlayPacket) {

        if (serverPlayPacket.getType() == PacketType.Play.Server.ENTITY_METADATA) {

            final WrapperPlayServerEntityMetadata wrapperPlayServerEntityMetadata = serverPlayPacket.getEntityMetadataWrapper();

            for (final EntityData entityData : wrapperPlayServerEntityMetadata.getEntityMetadata()) {

                final Object value = entityData.getValue();
                final Object index = entityData.getIndex();

                //Bite successful
                if (value.equals(true) && index.equals(9)) {
                    this.fishingState = FishingState.BITE;
                    this.lastBite = System.currentTimeMillis();
                }

                //Bite fail
                if (value.equals(false) && index.equals(9)) {
                    this.fishingState = FishingState.BITE_FAIL;
                    this.lastBiteFail = System.currentTimeMillis();
                }

                //Caught
                if (index.equals(16)) {
                    this.fishingState = FishingState.CAUGHT;
                    this.lastCaught = System.currentTimeMillis();
                }
            }
        }
    }

    public FishingState getFishingState() {
        final FishingState tempFishingState = this.fishingState;
        this.fishingState = FishingState.UNKNOWN;
        return tempFishingState;
    }

    public long getLastBite() {
        return lastBite;
    }

    public long getLastBiteFail() {
        return lastBiteFail;
    }

    public long getLastCaught() {
        return lastCaught;
    }
}
