package com.xiii.wave.checks.types;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.processors.Packet;

public abstract class Check extends AbstractCheck {

    public Check(Profile profile, CheckType check, String type, String description) {
        super(profile, check, type, description);
    }

    public Check(Profile profile, CheckType check, String description) {
        super(profile, check, "", description);
    }

    public abstract void handle(Packet packet);
}
