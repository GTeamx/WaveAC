package com.xiii.wave.checks.impl.movement.fly;

import com.xiii.wave.checks.*;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.packettype.PacketType;

@CheckInfo(checkName = "Fly A", checkCategory = CheckCategory.MOVEMENT, checkState = CheckState.EXPERIMENTAL, addBuffer = 0, maxBuffer = 0, removeBuffer = 0)
public class FlyA extends Check {

    @Packets(packets = {PacketType.Play.Client.POSITION, PacketType.Play.Client.POSITION_LOOK, PacketType.Play.Client.FLYING})
    public synchronized void handle(PacketPlayReceiveEvent packet) {

        if(data.motionY > 0.42) flag(packet, "motionY §b" + data.motionY);

    }
}
