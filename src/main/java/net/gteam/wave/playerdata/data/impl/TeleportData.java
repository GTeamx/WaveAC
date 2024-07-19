package net.gteam.wave.playerdata.data.impl;

import net.gteam.wave.playerdata.data.Data;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

public class TeleportData implements Data {

    private final int teleportTicks = 2;

    @Override
    public void process(final ClientPlayPacket packet) {
    }

    @Override
    public void process(final ServerPlayPacket packet) {
    }

    public int getTeleportTicks() {
        return teleportTicks;
    }
}
