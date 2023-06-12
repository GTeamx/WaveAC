package com.xiii.wave.checks.impl.autofish;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.enums.FishingState;
import com.xiii.wave.playerdata.data.impl.FishingData;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;
import com.xiii.wave.utils.TaskUtils;
import org.bukkit.Bukkit;

public class AutoFishA extends Check {

    public AutoFishA(Profile profile) {
        super(profile, CheckType.AUTOFISH, "A", "Fishing in similar reaction time");
    }

    private long lastReactionTime = 10000L;

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {}

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {

        if (serverPlayPacket.is(PacketType.Play.Server.ENTITY_STATUS)) {
            TaskUtils.task(() -> Bukkit.broadcastMessage("STS=" + serverPlayPacket.getEntityStatusWrapper().getStatus()));
        }

        if (serverPlayPacket.is(PacketType.Play.Server.ENTITY_METADATA)) {

            final FishingData fishingData = profile.getFishingData();

            if (fishingData.getFishingState().equals(FishingState.CAUGHT)) {

                TaskUtils.task(() -> Bukkit.broadcastMessage("delay=" + (this.lastReactionTime - (fishingData.getLastBite() - System.currentTimeMillis()))));

                this.lastReactionTime = (fishingData.getLastBite() - System.currentTimeMillis());
            }
        }
    }
}
