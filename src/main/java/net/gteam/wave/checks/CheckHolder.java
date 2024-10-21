package net.gteam.wave.checks;

import net.gteam.wave.checks.annotations.Disabled;
import net.gteam.wave.checks.impl.aimassist.AimAssist10A;
import net.gteam.wave.checks.impl.fly.Fly10A;
import net.gteam.wave.checks.impl.ground.Ground10A;
import net.gteam.wave.checks.impl.jump.Jump5A;
import net.gteam.wave.checks.impl.motion.Speed10A;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

import java.util.Arrays;

public class CheckHolder {

    private final Profile profile;
    private Check[] checks;
    private int checksSize;

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
                new Jump5A(this.profile),
                new Speed10A(this.profile),
                new AimAssist10A(this.profile),
                new Ground10A(this.profile)

        );

        // TODO: Make disabled: disable check
    }

    private void addChecks(final Check... checks) {

        this.checks = new Check[0];

        this.checksSize = 0;

        for (final Check check : checks) {

            if (this.profile != null && isDisabled(check)) continue;

            this.checks = Arrays.copyOf(this.checks, this.checksSize + 1);

            this.checks[this.checksSize] = check;

            this.checksSize++;
        }
    }

    private boolean isDisabled(final Check check) {

        if (check.getClass().isAnnotationPresent(Disabled.class)) return true;

        return false;
    }

    public Check[] getChecks() {
        return checks;
    }
}