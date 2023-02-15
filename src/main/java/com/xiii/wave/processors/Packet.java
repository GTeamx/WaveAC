package com.xiii.wave.processors;

import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.*;

public class Packet {

    private final PacketType.Play.Client type;
    private final long timeStamp;

    /*
    Use entity cache
     */
    private WrapperPlayClientInteractEntity useEntityWrapper;
    private boolean attack;

    /*
    Block Dig cache
     */
    private WrapperPlayClientPlayerDigging blockDigWrapper;

    /*
    Window Click cache
     */
    private WrapperPlayClientClickWindow windowClickWrapper;

    /*
    Entity Action cache
     */
    private WrapperPlayClientEntityAction entityActionWrapper;

    /*
    Chat cache
     */
    private WrapperPlayClientChatMessage chatWrapper;

    /*
    Movement - Flying cache
     */
    private WrapperPlayClientPlayerPosition positionWrapper;
    private WrapperPlayClientPlayerPositionAndRotation positionLookWrapper;
    private WrapperPlayClientPlayerRotation lookWrapper;
    private boolean movement, rotation, flying;

    public Packet(PacketType.Play.Client type, PacketPlayReceiveEvent packet, long timeStamp) {
        this.timeStamp = timeStamp;

        switch (this.type = type) {

            case INTERACT_ENTITY:

                this.useEntityWrapper = new WrapperPlayClientInteractEntity(packet);

                this.attack = this.useEntityWrapper.getAction() == WrapperPlayClientInteractEntity.InteractAction.ATTACK;

                break;

            case PLAYER_DIGGING:

                this.blockDigWrapper = new WrapperPlayClientPlayerDigging(packet);

                break;

            case CLICK_WINDOW:

                this.windowClickWrapper = new WrapperPlayClientClickWindow(packet);

                break;

            case ENTITY_ACTION:

                this.entityActionWrapper = new WrapperPlayClientEntityAction(packet);

                break;

            case CHAT_MESSAGE:

                this.chatWrapper = new WrapperPlayClientChatMessage(packet);

                break;

            case PLAYER_FLYING:
            case PLAYER_POSITION:

                this.positionWrapper = new WrapperPlayClientPlayerPosition(packet);

                this.flying = this.movement = true;

                break;

            case PLAYER_POSITION_AND_ROTATION:

                this.positionLookWrapper = new WrapperPlayClientPlayerPositionAndRotation(packet);

                this.flying = this.movement = this.rotation = true;

                break;

            case PLAYER_ROTATION:

                this.lookWrapper = new WrapperPlayClientPlayerRotation(packet);

                this.flying = this.rotation = true;

                break;
        }
    }

    public boolean isAttack() {
        return attack;
    }

    public boolean isMovement() {
        return movement;
    }

    public boolean isRotation() {
        return rotation;
    }

    public boolean isFlying() {
        return flying;
    }

    public WrapperPlayClientInteractEntity getUseEntityWrapper() {
        return useEntityWrapper;
    }

    public WrapperPlayClientPlayerDigging getBlockDigWrapper() {
        return blockDigWrapper;
    }

    public WrapperPlayClientClickWindow getWindowClickWrapper() {
        return windowClickWrapper;
    }

    public WrapperPlayClientEntityAction getEntityActionWrapper() {
        return entityActionWrapper;
    }

    public WrapperPlayClientChatMessage getChatWrapper() {
        return chatWrapper;
    }

    public WrapperPlayClientPlayerPosition getPositionWrapper() {
        return positionWrapper;
    }

    public WrapperPlayClientPlayerPositionAndRotation getPositionLookWrapper() {
        return positionLookWrapper;
    }

    public WrapperPlayClientPlayerRotation getLookWrapper() {
        return lookWrapper;
    }

    public boolean is(PacketType.Play.Client type) {
        return this.type == type;
    }

    public PacketType.Play.Client getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
