package net.gteam.wave.checks.impl.aimassist;

import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.annotations.Experimental;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.RotationData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

import java.util.ArrayList;
import java.util.Objects;

@Experimental
public class AimAssist10A extends Check {

    private final ArrayList<Float> yaw = new ArrayList<>();
    private final ArrayList<Float> pitch = new ArrayList<>();

    public AimAssist10A(final Profile profile) {
        super(profile, CheckType.AIMASSIST, "AA10A", "Duplicated Rotations");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        if (!clientPlayPacket.isRotation()) return;

        RotationData data = profile.getRotationData();

        if (yaw.size() > 4) yaw.remove(0);
        yaw.add(data.getYaw());

        if (pitch.size() > 4) pitch.remove(0);
        pitch.add(data.getPitch());

        if (pitch.size() > 3 && yaw.size() > 3) {
            if (Objects.equals(pitch.get(0), pitch.get(2)) && !Objects.equals(pitch.get(0), pitch.get(1)) && Math.abs(pitch.get(0) - pitch.get(1)) > 2 && Math.abs(pitch.get(0)) != 90 && Math.abs(pitch.get(1)) != 90) {
                if (increaseBufferBy(1) > 2) {
                    fail("Duplicate Pitch\n" + pitch.get(0) + "\n" + pitch.get(1) + "\n" + pitch.get(2));
                }

            } else decreaseBufferBy(0.01);

            if (Objects.equals(yaw.get(0), yaw.get(2)) && !Objects.equals(yaw.get(0), yaw.get(1)) && Math.abs(yaw.get(0) - yaw.get(1)) > 2) {
                if (increaseBufferBy(1) > 2) {
                    fail("Duplicate Yaw\n" + yaw.get(0) + "\n" + yaw.get(1) + "\n" + yaw.get(2));
                }
            }  else decreaseBufferBy(0.01);
        }

    }

    @Override
    public void handle(final ServerPlayPacket clientPlayPacket) {}
}
