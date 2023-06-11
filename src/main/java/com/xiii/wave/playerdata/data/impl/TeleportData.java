package com.xiii.wave.playerdata.data.impl;

import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

public class TeleportData implements Data {

    private int teleportTicks;

    @Override
    public void process(final ClientPlayPacket clientPlayPacket) {

    }

    @Override
    public void process(final ServerPlayPacket serverPlayPacket) {

    }

    public int getTeleportTicks() {
        return teleportTicks;
    }
}