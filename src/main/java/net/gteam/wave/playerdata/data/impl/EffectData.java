package net.gteam.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityEffect;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerRemoveEntityEffect;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.Data;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.custom.CustomEffect;
import net.gteam.wave.utils.custom.EffectType;

import java.util.concurrent.ConcurrentHashMap;

public class EffectData implements Data {

    private final Profile profile;
    private final ConcurrentHashMap<EffectType, CustomEffect> effects = new ConcurrentHashMap<>();

    public EffectData(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process(final ClientPlayPacket ignored) {}

    @Override
    public void process(final ServerPlayPacket packet) {

        switch (packet.getType()) {

            case ENTITY_EFFECT:

                final WrapperPlayServerEntityEffect entityEffect = packet.getEntityEffectWrapper();
                final CustomEffect effect = new CustomEffect(entityEffect.getPotionType().getId(profile.getVersion()), entityEffect.getEffectAmplifier(), entityEffect.getEffectDurationTicks());

                effects.put(EffectType.fromID(entityEffect.getPotionType().getId(ClientVersion.V_1_12)), effect);

                break;

            case REMOVE_ENTITY_EFFECT:

                final WrapperPlayServerRemoveEntityEffect removeEntityEffect = packet.getRemoveEntityEffectWrapper();

                effects.remove(EffectType.fromID(removeEntityEffect.getPotionType().getId(ClientVersion.V_1_12)));

                break;
        }

    }

    public ConcurrentHashMap<EffectType, CustomEffect> getEffects() {
        return effects;
    }
}
