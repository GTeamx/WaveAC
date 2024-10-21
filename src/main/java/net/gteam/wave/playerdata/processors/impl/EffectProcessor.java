package net.gteam.wave.playerdata.processors.impl;

import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.EffectData;
import net.gteam.wave.playerdata.processors.Processor;
import net.gteam.wave.utils.custom.EffectType;

public class EffectProcessor implements Processor {

    private final Profile profile;

    public EffectProcessor(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() {

        final EffectData effectData = profile.getEffectData();

        for (final EffectType effect : effectData.getEffects().keySet()) {



        }

    }
}
