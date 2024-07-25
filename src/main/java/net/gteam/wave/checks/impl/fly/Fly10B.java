package net.gteam.wave.checks.impl.fly;

import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.CollisionUtils;
import net.gteam.wave.utils.custom.CustomEffect;
import net.gteam.wave.utils.custom.EffectType;

public class Fly10B extends Check {

    public Fly10B(final Profile profile) {
        super(profile, CheckType.FLY, "FL10B", "Checks for gravity modifications");
    }

    @Override
    public void handle(ClientPlayPacket clientPlayPacket) {
        if (!clientPlayPacket.isMovement()) return;

        final MovementData movementData = this.profile.getMovementData();
        final double motionY = movementData.getDeltaY();
        final boolean step = CollisionUtils.isServerGround(motionY) && CollisionUtils.isServerGround(movementData.getLastLocation().getY());
        final boolean jumped = motionY > 0 && movementData.getLastLocation().getY() % (1D/64) == 0 && !movementData.isOnGround() && !step;
        final CustomEffect jumpEffect = profile.getEffectData().getEffects().get(EffectType.JUMP_BOOST);
        final boolean blockAbove = movementData.getBlocksAboveTicks() <= 1;
        final double expectedJumpMotion = blockAbove ? motionY : 0.42F + (double)(jumpEffect != null ? (jumpEffect.getAmplifier() + 1) * 0.1F : 0);

        if (jumped && movementData.getSlimeTicks() > 3 && movementData.getHoneyTicks() > 3 && movementData.getClimbableTicks() > 0 && movementData.getLiquidTicks() > 0 && movementData.getBubbleTicks() > 0) {
            if (Math.abs(expectedJumpMotion - motionY) > 1E-7) {
                fail("jumpDiff=" + Math.abs(motionY - expectedJumpMotion));
            }
        }
    }

    @Override
    public void handle(ServerPlayPacket clientPlayPacket) {

    }
}
