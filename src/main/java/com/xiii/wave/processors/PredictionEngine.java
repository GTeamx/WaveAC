package com.xiii.wave.processors;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.ActionData;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.playerdata.data.impl.RotationData;
import com.xiii.wave.utils.CollisionUtils;
import com.xiii.wave.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public final class PredictionEngine {

    final static boolean[] bools = new boolean[] {true, false};

    public static double getVerticalPrediction(final Profile profile, final double lastMotionY, final double currMotionY) {
        return ((lastMotionY - 0.08) * 0.9800000190734863) - (profile.isExempt().tookDamage(150L) ? currMotionY : 0);
    }

    public static double[] getHorizontalPrediction(final Profile profile) {
        final MovementData movementData = profile.getMovementData();
        final RotationData rotationData = profile.getRotationData();
        final ActionData actionData = profile.getActionData();

        double sDelta = 1.7976931348623157E308;
        float frictionBlock = getFriction(CollisionUtils.getBlock(movementData.getLocation().clone().subtract(0, 1, 0), true));

        double playerMotionX = 0;
        double playerMotionZ = 0;
        boolean step = mathOnGround(movementData.getDeltaY()) && mathOnGround(movementData.getLastLocation().getY());
        boolean jumped = movementData.getDeltaY() > 0 && movementData.getLastLocation().getY() % (1D / 64) == 0 && !movementData.isOnGround() && !step;
        boolean isSneaking = actionData.isSneaking();
        boolean isSprinting = actionData.isSprinting();
        for (int f = -1; f < 2; f++) {
            for (int s = -1; s < 2; s++) {
                for (boolean using : bools) {
                    for (boolean isAttacking : bools) {
                        float forwardMotion = f, strafeMotion = s;
                        if (isSneaking) {
                            forwardMotion *= 0.3;
                            strafeMotion *= 0.3;
                        }
                        if (using) {
                            forwardMotion *= 0.2;
                            strafeMotion *= 0.2;
                        }
                        forwardMotion *= 0.98;
                        strafeMotion *= 0.98;

                        float walkspeed = profile.getPlayer().getWalkSpeed() / 2f;
                        float friction = 0.91f;
                        double lastmotionX = movementData.getLastDeltaX();
                        double lastmotionZ = movementData.getLastDeltaZ();

                        lastmotionX *= (movementData.isLastOnGround() ? 0.6 : 1) * 0.91;
                        lastmotionZ *= (movementData.isLastOnGround() ? 0.6 : 1) * 0.91;

                        if (profile.getVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                            if (Math.abs(lastmotionX) < 0.003)
                                lastmotionX = 0;
                            if (Math.abs(lastmotionZ) < 0.003)
                                lastmotionZ = 0;
                        } else {
                            if (Math.abs(lastmotionX) < 0.005)
                                lastmotionX = 0;
                            if (Math.abs(lastmotionZ) < 0.005)
                                lastmotionZ = 0;
                        }
                        if (isAttacking) {
                            lastmotionX *= 0.6;
                            lastmotionZ *= 0.6;
                        }
                        if (isSprinting) walkspeed += walkspeed * 0.3f;
                        if (PlayerUtils.hasPotionEffect(profile, PotionEffectType.SPEED))
                            walkspeed += (PlayerUtils.getEffectByType(profile, PotionEffectType.SPEED)
                                    .get()
                                    .getAmplifier() + 1) * (double) 0.2f * walkspeed;
                        if (PlayerUtils.hasPotionEffect(profile, PotionEffectType.SLOW))
                            walkspeed += (PlayerUtils.getEffectByType(profile, PotionEffectType.SLOW)
                                    .get()
                                    .getAmplifier() + 1) * (double) -0.15f * walkspeed;
                        float frictionWalk;
                        if (movementData.isOnGround()) {
                            friction *= frictionBlock;

                            frictionWalk = (float) (walkspeed * (0.16277136F / Math.pow(friction, 3)));

                            if (jumped && isSprinting) {
                                float rot = rotationData.getYaw() * 0.017453292F;
                                lastmotionX -= Math.sin(rot) * 0.2F;
                                lastmotionZ += Math.cos(rot) * 0.2F;
                            }

                        } else {
                            frictionWalk = isSprinting ? 0.026f : 0.02f;
                        }

                        double keyMotion = forwardMotion * forwardMotion + strafeMotion * strafeMotion;

                        if (keyMotion >= 1.0E-4F) {
                            keyMotion = frictionWalk / Math.max(1.0, Math.sqrt(keyMotion));
                            forwardMotion *= keyMotion;
                            strafeMotion *= keyMotion;

                            final float yaws = (float) Math.sin(rotationData.getYaw() * (float) Math.PI / 180.F),
                                    yawc = (float) Math.cos(rotationData.getYaw() * (float) Math.PI / 180.F);

                            lastmotionX += ((strafeMotion * yawc) - (forwardMotion * yaws));
                            lastmotionZ += ((forwardMotion * yawc) + (strafeMotion * yaws));
                        }

                        double delta = Math.pow(movementData.getDeltaX() - lastmotionX, 2)
                                + Math.pow(movementData.getDeltaZ() - lastmotionZ, 2);

                        if (delta < sDelta) {
                            sDelta = delta;
                            playerMotionX = lastmotionX;
                            playerMotionZ = lastmotionZ;
                        }
                        sDelta = Math.min(delta, sDelta);
                    }
                }
            }
        }
        double playerMotion = Math.hypot(playerMotionX, playerMotionZ);
        return new double[] { playerMotion, sDelta };
    }

    public static float getFriction(Block block) {
        if (block == null) return 0.6f;
        Optional<Material> matched = Optional.of(block.getType());

        switch (matched.get()) {
            case SLIME_BLOCK:
                return 0.8f;
            case ICE:
            case BLUE_ICE:
            case FROSTED_ICE:
            case PACKED_ICE:
                return 0.98f;
            default:
                return 0.6f;
        }
    }

    public static boolean mathOnGround(final double posY) {
        return posY % 0.015625 == 0;
    }
}
