package com.xiii.wave.playerdata.data;

import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

public interface Data {
    void process(final ClientPlayPacket clientPlayPacket);

    void process(final ServerPlayPacket serverPlayPacket);
}