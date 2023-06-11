package com.xiii.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;
import com.xiii.wave.utils.custom.Equipment;

public class CombatData implements Data {

    private final Profile profile;

    private final Equipment equipment;

    private Vector3d entityVelocity;

    private long lastDamageTaken = 10000L, lastHandDamageTaken = 10000L;

    public CombatData(final Profile profile) {
        this.profile = profile;

        this.equipment = new Equipment();
    }

    @Override
    public void process(final ClientPlayPacket clientPlayPacket) {

    }

    @Override
    public void process(final ServerPlayPacket serverPlayPacket) {

        switch (serverPlayPacket.getType()) {

            case ENTITY_VELOCITY:

                final WrapperPlayServerEntityVelocity entityVelocityWrapper = serverPlayPacket.getEntityVelocityWrapper();

                if (entityVelocityWrapper.getEntityId() == profile.getPlayer().getEntityId() && !profile.getPlayer().isDead()) {

                    this.lastDamageTaken = serverPlayPacket.getTimeStamp();
                    this.entityVelocity = entityVelocityWrapper.getVelocity();
                }

                break;

            case ENTITY_ANIMATION:

                final WrapperPlayServerEntityAnimation entityAnimationWrapper = serverPlayPacket.getEntityAnimationWrapper();

                if (entityAnimationWrapper.getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.HURT || entityAnimationWrapper.getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.SWING_MAIN_ARM) {

                    if (entityAnimationWrapper.getEntityId() != profile.getPlayer().getEntityId()) {

                        this.lastDamageTaken = serverPlayPacket.getTimeStamp();
                        this.lastHandDamageTaken = serverPlayPacket.getTimeStamp();
                    }
                }

        }

    }

    public long getLastDamageTaken() {
        return lastDamageTaken;
    }

    public long getLastHandDamageTaken() {
        return lastHandDamageTaken;
    }

    public Vector3d getEntityVelocity() {
        return entityVelocity;
    }
}