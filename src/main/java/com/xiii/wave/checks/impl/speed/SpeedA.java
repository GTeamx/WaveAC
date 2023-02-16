package com.xiii.wave.checks.impl.speed;

import com.xiii.wave.checks.enums.CheckType;
import com.xiii.wave.checks.types.Check;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.processors.Packet;
import com.xiii.wave.utils.TaskUtils;
import org.bukkit.Bukkit;

public class SpeedA extends Check {
    public SpeedA(Profile profile) {
        super(profile, CheckType.SPEED, "A", "Checks for speed");
    }

    @Override
    public void handle(Packet packet) {
        if (!packet.isMovement() || profile.isExempt().movement()) return;

        //Let's make an example check

        MovementData data = profile.getMovementData();

        if (data.getDeltaY() > 0.42) {

            if (increaseBuffer() > 2) fail(
                    "desc"
            );

        } else decreaseBufferBy(Float.MIN_VALUE);
    }
}
