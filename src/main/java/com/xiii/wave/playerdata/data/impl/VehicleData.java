package com.xiii.wave.playerdata.data.impl;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;
import com.xiii.wave.utils.MathUtils;

public class VehicleData implements Data {

    private final Profile profile;

    public VehicleData(final Profile profile) {
        this.profile = profile;
    }

    private long lastRide = 10000L;

    @Override
    public void process(final ClientPlayPacket clientPlayPacket) {

        if (profile.getPlayer().getVehicle() != null) this.lastRide = System.currentTimeMillis();
    }

    @Override
    public void process(final ServerPlayPacket serverPlayPacket) {

    }

    public boolean isRiding(final long delay) {
        return MathUtils.elapsed(lastRide) <= delay;
    }
}