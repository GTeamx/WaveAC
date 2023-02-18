package com.xiii.wave.processors;

public final class PredictionEngine {

    public static double getVerticalPrediction(final double lastMotionY) {
        return (lastMotionY - 0.08) * 0.9800000190734863;
    }
}
