package net.gteam.wave.playerdata.data;

import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

public interface Data {
    void process(final ClientPlayPacket clientPlayPacket);

    void process(final ServerPlayPacket serverPlayPacket);
}
