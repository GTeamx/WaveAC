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
import com.xiii.wave.utils.TaskUtils;
import org.bukkit.Bukkit;

@Experimental
public class AutoFishA extends Check {
    public AutoFishA(final Profile profile) {
        super(profile, CheckType.AUTOFISH, "A", "Fishing in similar reaction time");
    }

    private long lastReactionTime = 10000L;

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {}

    @Override
    public void handle(final ServerPlayPacket serverPlayPacket) {

        TaskUtils.taskTimer(() -> Bukkit.broadcastMessage("ServerPlayPacket -> AutoFishA.java"), 20*3, 20*3);

        if (serverPlayPacket.is(PacketType.Play.Server.ENTITY_METADATA)) {

            final FishingData fishingData = profile.getFishingData();

            TaskUtils.task(() -> Bukkit.broadcastMessage("delay=" + (this.lastReactionTime - (serverPlayPacket.getTimeStamp() - fishingData.getLastBite()))));

            if (fishingData.getFishingState().equals(FishingState.CAUGHT)) {

                this.lastReactionTime = (serverPlayPacket.getTimeStamp() - fishingData.getLastBite());
            }
        }
    }
}
