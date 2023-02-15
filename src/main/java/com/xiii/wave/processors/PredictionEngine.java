package com.xiii.wave.processors;

import com.xiii.wave.data.PlayerData;

public final class PredictionEngine {

    PlayerData data;

    public PredictionEngine(PlayerData data) {

        this.data = data;

    }

    public static double getVerticalPrediction(final double lastMotionY) {
        return (lastMotionY - 0.08) * 0.9800000190734863;
    }

}
