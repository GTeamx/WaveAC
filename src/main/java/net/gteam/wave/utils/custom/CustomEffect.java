package net.gteam.wave.utils.custom;

public class CustomEffect {

    private final int id;
    private final int amplifier;
    private final int duration;

    public CustomEffect(int id, int amplifier, int duration) {
        this.id = id;
        this.amplifier = amplifier;
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public int getAmplifier() {
        return amplifier;
    }

    public int getDuration() {
        return duration;
    }
}
