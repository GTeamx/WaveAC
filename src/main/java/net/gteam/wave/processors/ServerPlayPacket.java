package net.gteam.wave.processors;

import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.*;

public class ServerPlayPacket {

    private final PacketType.Play.Server type;
    private final long timeStamp;

    // Entity Velocity cache
    private WrapperPlayServerEntityVelocity entityVelocityWrapper;

    // Arm Animation cache
    private WrapperPlayServerEntityAnimation entityAnimationWrapper;

    // Entity Metadata cache
    private WrapperPlayServerEntityMetadata entityMetadataWrapper;

    // Entity status cache
    private WrapperPlayServerEntityStatus entityStatusWrapper;

    // Block action cache
    private WrapperPlayServerBlockAction blockActionWrapper;

    // Position and Look cache
    private WrapperPlayServerPlayerPositionAndLook playerPositionAndLookWrapper;

    // Entity Effect cache
    private WrapperPlayServerEntityEffect entityEffectWrapper;

    // Remove Entity Effect cache
    private WrapperPlayServerRemoveEntityEffect removeEntityEffectWrapper;

    public ServerPlayPacket(final PacketType.Play.Server type, final PacketPlaySendEvent packet, final long timeStamp) {
        this.timeStamp = timeStamp;

        switch (this.type = type) {

            case ENTITY_VELOCITY:

                this.entityVelocityWrapper = new WrapperPlayServerEntityVelocity(packet);

                break;

            case ENTITY_ANIMATION:

                this.entityAnimationWrapper = new WrapperPlayServerEntityAnimation(packet);

                break;

            case ENTITY_METADATA:

                this.entityMetadataWrapper = new WrapperPlayServerEntityMetadata(packet);

                break;

            case ENTITY_STATUS:

                this.entityStatusWrapper = new WrapperPlayServerEntityStatus(packet);

                break;

            case BLOCK_ACTION:

                this.blockActionWrapper = new WrapperPlayServerBlockAction(packet);

                break;

            case PLAYER_POSITION_AND_LOOK:

                this.playerPositionAndLookWrapper = new WrapperPlayServerPlayerPositionAndLook(packet);

                break;

            case ENTITY_EFFECT:

                this.entityEffectWrapper = new WrapperPlayServerEntityEffect(packet);

                break;

            case REMOVE_ENTITY_EFFECT:

                this.removeEntityEffectWrapper = new WrapperPlayServerRemoveEntityEffect(packet);

                break;
        }
    }

    public WrapperPlayServerEntityVelocity getEntityVelocityWrapper() {
        return entityVelocityWrapper;
    }

    public WrapperPlayServerEntityAnimation getEntityAnimationWrapper() {
        return entityAnimationWrapper;
    }

    public WrapperPlayServerEntityMetadata getEntityMetadataWrapper() {
        return entityMetadataWrapper;
    }

    public WrapperPlayServerEntityStatus getEntityStatusWrapper() {
        return entityStatusWrapper;
    }

    public WrapperPlayServerBlockAction getBlockActionWrapper() {
        return blockActionWrapper;
    }

    public WrapperPlayServerPlayerPositionAndLook getPlayerPositionAndLookWrapper() {
        return playerPositionAndLookWrapper;
    }

    public WrapperPlayServerEntityEffect getEntityEffectWrapper() {
        return entityEffectWrapper;
    }

    public WrapperPlayServerRemoveEntityEffect getRemoveEntityEffectWrapper() {
        return removeEntityEffectWrapper;
    }

    public boolean is(final PacketType.Play.Server type) {
        return this.type == type;
    }

    public PacketType.Play.Server getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
