package com.xiii.wave.playerdata.data.impl;

import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.processors.Packet;

public class TeleportData implements Data {

    private int teleportTicks;

    @Override
    public void process(Packet packet) {
        /*
        Handle the packet
         */
    }

    public int getTeleportTicks() {
        return teleportTicks;
    }
}