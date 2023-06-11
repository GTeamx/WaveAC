package com.xiii.wave.processors.packet.server;

import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.*;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;

public class ServerPlayPacket {

    private final PacketType.Play.Server type;
    private final long timeStamp;

    /*
    Entity Velocity cache
     */
    private WrapperPlayServerEntityVelocity entityVelocityWrapper;

    /*
    Arm Animation cache
     */
    private WrapperPlayServerEntityAnimation entityAnimationWrapper;

    public ServerPlayPacket(final PacketType.Play.Server type, final PacketPlaySendEvent packet, final long timeStamp) {
        this.timeStamp = timeStamp;

        switch (this.type = type) {

            case ENTITY_VELOCITY:

                this.entityVelocityWrapper = new WrapperPlayServerEntityVelocity(packet);

                break;

            case ENTITY_ANIMATION:

                this.entityAnimationWrapper = new WrapperPlayServerEntityAnimation(packet);

                break;

        }
    }

    public WrapperPlayServerEntityVelocity getEntityVelocityWrapper() {
        return entityVelocityWrapper;
    }

    public WrapperPlayServerEntityAnimation getEntityAnimationWrapper() {
        return entityAnimationWrapper;
    }

    public boolean is(PacketType.Play.Server type) {
        return this.type == type;
    }

    public PacketType.Play.Server getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
