package com.xiii.wave.checks.types;

import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;

public abstract class Check extends AbstractCheck {

    public Check(final Profile profile, final CheckType check, final String type, final String description) {
        super(profile, check, type, description);
    }

    public Check(final Profile profile, final CheckType check, final String description) {
        super(profile, check, "", description);
    }

    public abstract void handle(final ClientPlayPacket clientPlayPacket);

    public abstract void handle(final ServerPlayPacket serverPlayPacket);
}
