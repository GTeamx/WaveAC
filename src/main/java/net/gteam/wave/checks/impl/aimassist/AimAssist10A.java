package net.gteam.wave.checks.impl.aimassist;

import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.RotationData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.custom.aim.RotationHeuristics;

@Development
public class AimAssist10A extends Check {

    private final RotationHeuristics heuristics = new RotationHeuristics(30, 1.25F, 7.5F);

    public AimAssist10A(final Profile profile) {
        super(profile, CheckType.AIMASSIST, "AA10A", "Test");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        if (!clientPlayPacket.isRotation()) return;

        RotationData data = profile.getRotationData();
        this.heuristics.process(data.getDeltaYaw());

        if (!this.heuristics.isFinished()) return;

        final RotationHeuristics.HeuristicsResult result = this.heuristics.getResult();

        final float average = result.getAverage();
        final float min = result.getMin();
        final float max = result.getMax();
        final int lowCount = result.getLowCount();
        final int highCount = result.getHighCount();
        final int duplicates = result.getDuplicates();
        final int roundedCount = result.getRoundedCount();

        if (String.valueOf(min).contains("000") && String.valueOf(max).contains("000")) {
            debug("avg=" + average);
            debug("min=" + min + " " + String.valueOf(min).contains("000"));
            debug("max=" + max + " " + String.valueOf(max).contains("000"));
            debug("lowCount=" + lowCount);
            debug("highCount=" + highCount);
            debug("duplicates=" + duplicates);
            debug("roundedCount=" + roundedCount);
        }

        heuristics.reset();
    }

    @Override
    public void handle(final ServerPlayPacket clientPlayPacket) {}
}
