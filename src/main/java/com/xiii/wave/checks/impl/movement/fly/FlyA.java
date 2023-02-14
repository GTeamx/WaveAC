package com.xiii.wave.checks.impl.movement.fly;

import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.xiii.wave.checks.*;
import com.xiii.wave.exempt.ExemptType;

@CheckInfo(checkName = "Fly A", checkDescription = "Check for vanilla gravity modifications.", checkCategory = CheckCategory.MOVEMENT, checkState = CheckState.EXPERIMENTAL, addBuffer = 1, maxBuffer = 1, removeBuffer = 1)
public final class FlyA extends Check {

    @Packets(playServer = {}, playClient = {PacketType.Play.Client.PLAYER_POSITION, PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION, PacketType.Play.Client.PLAYER_FLYING})
    public synchronized void handle(PacketPlayReceiveEvent packet) {

        final boolean exempt = isExempt(ExemptType.LIQUID, ExemptType.CLIMB, ExemptType.LOW_BLOCK);
        final double difference = data.motionY - PredictionEngine.getVerticalPrediction(data.lastMotionY);
        final double maxDifference = 2.3619994848900205E-14;

        /*
        if(isExempt(ExemptType.DAMAGE)) maxBuffer = 3;
        else maxBuffer = 1;
         */

        if(!data.serverGround) {

            if(difference > maxDifference && !exempt) flag(packet, "difference §b" + difference);

        } else removeBuffer();

    }
}
