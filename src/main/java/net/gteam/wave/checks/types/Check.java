package net.gteam.wave.checks.types;

import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

// AbstractCheck related
public abstract class Check extends AbstractCheck {

    public Check(final Profile profile, final CheckType check, final String type, final String description) {
        super(profile, check, type, description);
    }

    public Check(final Profile profile, final CheckType check, final String description) {
        super(profile, check, "", description);
    }

    public abstract void handle(final ClientPlayPacket clientPlayPacket);

    public abstract void handle(final ServerPlayPacket clientPlayPacket);
}