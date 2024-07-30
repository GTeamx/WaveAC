package net.gteam.wave.checks;

import net.gteam.wave.checks.annotations.Disabled;
import net.gteam.wave.checks.impl.fly.Fly10A;
import net.gteam.wave.checks.impl.jump.Jump10A;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

import java.util.Arrays;

public class CheckHolder {

    private final Profile profile;
    private Check[] checks;
    private int checksSize;
    private boolean testing; //Used for testing new checks

    public CheckHolder(final Profile profile) {
        this.profile = profile;
    }

    public void runChecks(final ClientPlayPacket clientPlayPacket) {
        for (int i = 0; i < this.checksSize; i++) this.checks[i].handle(clientPlayPacket);
    }

    public void runChecks(final ServerPlayPacket serverPlayPacket) {
        for (int i = 0; i < this.checksSize; i++) this.checks[i].handle(serverPlayPacket);
    }

    public void registerAll() {

        addChecks(

                new Fly10A(this.profile),
                new Jump10A(this.profile)

        );

        // TODO: Make disabled: disable check
    }

    private void addChecks(final Check... checks) {

        this.checks = new Check[0];

        this.checksSize = 0;

        for (final Check check : checks) {

            //if (this.profile != null && (!check.isEnabled() || isDisabled(check))) continue; // TODO: fix (it just doesn't register any checks if enabled)

            this.checks = Arrays.copyOf(this.checks, this.checksSize + 1);

            this.checks[this.checksSize] = check;

            this.checksSize++;
        }
    }

    private boolean isDisabled(final Check check) {

        if (this.testing) return true;

        if (check.getClass().isAnnotationPresent(Disabled.class)) this.testing = true;

        return false;
    }

    public Check[] getChecks() {
        return checks;
    }
}