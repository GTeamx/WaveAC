package net.gteam.wave.playerdata.processors.impl;

import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.playerdata.processors.Processor;

public final class PredictionProcessor implements Processor {

    private final Profile profile;

    private double predictedDeltaY;

    public PredictionProcessor(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() {

        final MovementData movementData = profile.getMovementData();

        this.predictedDeltaY = (movementData.getLastDeltaY() - 0.08) * 0.9800000190734863;
    }

    public double getPredictedDeltaY() {
        return predictedDeltaY;
    }
}
