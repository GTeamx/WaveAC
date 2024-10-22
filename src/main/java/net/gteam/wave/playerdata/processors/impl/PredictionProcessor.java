package net.gteam.wave.playerdata.processors.impl;

import com.github.retrooper.packetevents.protocol.player.ClientVersion;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.ActionData;
import net.gteam.wave.playerdata.data.impl.EffectData;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.playerdata.data.impl.RotationData;
import net.gteam.wave.playerdata.processors.Processor;
import net.gteam.wave.utils.CollisionUtils;
import net.gteam.wave.utils.MoveUtils;
import net.gteam.wave.utils.custom.EffectType;
import net.gteam.wave.utils.fastmath.FastMath;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.potion.PotionEffectType;

import java.util.Optional;

public final class PredictionProcessor implements Processor {

    private final Profile profile;

    private double predictedDeltaY;

    public PredictionProcessor(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() {

        final MovementData movementData = profile.getMovementData();
        final EffectData effectData = profile.getEffectData();

        if (effectData.hasPotionEffect(PotionEffectType.LEVITATION)) {
            this.predictedDeltaY = ((movementData.getLastDeltaY() + (0.05 * (double)(effectData.getEffect(PotionEffectType.LEVITATION).getAmplifier() + 1) - movementData.getLastDeltaY()) * 0.2)) * 0.9800000190734863;
        } else if (effectData.hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
            if (movementData.getLastDeltaY() <= 0 && !movementData.isLastOnGround()) {
                this.predictedDeltaY = (movementData.getLastDeltaY() - 0.08 + 0.07) * 0.9800000190734863;
            } else {
                this.predictedDeltaY = (movementData.getLastDeltaY() - 0.08) * 0.9800000190734863;
            }
        } else {
            this.predictedDeltaY = (movementData.getLastDeltaY() - 0.08) * 0.9800000190734863;
        }

    }

    public double getPredictedDeltaY() {
        return predictedDeltaY;
    }

    final boolean[] bools = new boolean[] {true, false};

    public double getVerticalPrediction(final double lastMotionY) {
        return ((lastMotionY - 0.08) * MoveUtils.MOTION_Y_FRICTION);
    }

    public double[] getHorizontalPredictionOld(final Profile profile) {
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
        for (int f = -1; f < 2; f++) {
            for (int s = -1; s < 2; s++) {
                for (boolean using : bools) {
                    for (boolean isAttacking : bools) {
                        for (boolean isSprinting : bools) {
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

                            if (profile.getEffectData().getEffects().containsKey(EffectType.SPEED))
                                walkspeed += (float) ((profile.getEffectData().getEffects().get(EffectType.SPEED)
                                        .getAmplifier() + 1) * (double) 0.2f * walkspeed);

                            if (profile.getEffectData().getEffects().containsKey(EffectType.SLOWNESS))
                                walkspeed += (float) ((profile.getEffectData().getEffects().get(EffectType.SLOWNESS)
                                        .getAmplifier() + 1) * (double) -0.15f * walkspeed);

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
        }
        double playerMotion = FastMath.hypot(playerMotionX, playerMotionZ);
        return new double[] { playerMotion, sDelta };
    }


    public double[] getHorizontalPrediction(final Profile profile) {
        final MovementData movementData = profile.getMovementData();
        final RotationData rotationData = profile.getRotationData();
        final ActionData actionData = profile.getActionData();
        final float frictionBlock = CollisionUtils.getBlockSlipperiness(CollisionUtils.getBlock(movementData.getLocation().clone().subtract(0, 1, 0), true).getType()); //getFriction()
        final boolean step = mathOnGround(movementData.getDeltaY()) && mathOnGround(movementData.getLastLocation().getY());
        final boolean jumped = movementData.getDeltaY() > 0 && movementData.getLastLocation().getY() % (1D / 64) == 0 && !movementData.isOnGround() && !step;
        final boolean isSneaking = actionData.isSneaking();
        float walkSpeed;
        double lastMotionX;
        double lastMotionZ;
        double sDelta = 1.7976931348623157E308;
        double playerMotionX = 0;
        double playerMotionZ = 0;
        // Here we go through the different forward states
        for (int forward = -1; forward < 2; forward++) {
            // Here we go through the different strafe states
            for (int strafe = -1; strafe < 2; strafe++) {
                // Here we test if the Player is using an Item or not
                for (boolean using : bools) {
                    // Here we test if the Player is attacking or not
                    // if we don't do all this and rely on packets for this, it could lead to falses
                    for (boolean isAttacking : bools) {
                        for (boolean isSprinting : bools) {


                            // Doing this, so we can modify the forward and strafe for the calculation
                            float forwardMotion = forward, strafeMotion = strafe;

                            //Setting this to the Player's walkSpeed for calculating, like the command /speed from essentials
                            walkSpeed = profile.getPlayer().getWalkSpeed() / 2f;

                            // If the player is sneaking we must multiply the forward
                            // and strafe motion by 0.3
                            if (isSneaking) {
                                forwardMotion *= 0.3;
                                strafeMotion *= 0.3;
                            }
                            // If the player is using an Item it will be 0.2
                            // instead of the 0.3, cause using an item is slower than sneaking
                            if (using) {
                                forwardMotion *= 0.2;
                                strafeMotion *= 0.2;
                            }

                            // Here we multiply the normal friction
                            forwardMotion *= 0.98;
                            strafeMotion *= 0.98;
                            // Here we set the lastDeltaXZ so we can use this for
                            // calculating if the player is following minecraft's physics
                            lastMotionX = movementData.getLastDeltaX();
                            lastMotionZ = movementData.getLastDeltaZ();

                            // Here we add the friction to the lastMotion and obviously
                            // check if we are onGround, cause then the friction changes.
                            lastMotionX *= (movementData.isLastOnGround() ? 0.6 : 1) * frictionBlock;
                            lastMotionZ *= (movementData.isLastOnGround() ? 0.6 : 1) * frictionBlock;

                            // Here we round the lastMotionXZ down if its under the specific values,
                            // for 1.9+ its 0.003 and for everything under that 0.005. We do this
                            // cause Minecraft rounds them to 0, and we obviously want full prediction
                            if (profile.getVersion().isNewerThanOrEquals(ClientVersion.V_1_9)) {
                                if (Math.abs(lastMotionX) < 0.003) lastMotionX = 0;
                                if (Math.abs(lastMotionZ) < 0.003) lastMotionZ = 0;
                            } else {
                                if (Math.abs(lastMotionX) < 0.005) lastMotionX = 0;
                                if (Math.abs(lastMotionZ) < 0.005) lastMotionZ = 0;
                            }

                            // If the player is attacking the player obviously slow down and the value for this is 0.6
                            if (isAttacking) {
                                forwardMotion *= 0.6;
                                strafeMotion *= 0.6;
                            }

                            // if the player is Sprinting we add the walkSpeed multiplied with 0.3f
                            if (isSprinting) walkSpeed += walkSpeed * 0.3f;

                            // Adding the Speed Effect multiplier to walkSpeed, cause this is easier to work with
                            if (profile.getEffectData().getEffects().containsKey(EffectType.SPEED))
                                walkSpeed += (float) ((profile.getEffectData().getEffects().get(EffectType.SPEED)
                                        .getAmplifier() + 1) * (double) 0.2f * walkSpeed);

                            // Adding the Slowness Effect multiplier to walkSpeed, cause this is easier to work with
                            if (profile.getEffectData().getEffects().containsKey(EffectType.SLOWNESS))
                                walkSpeed += (float) ((profile.getEffectData().getEffects().get(EffectType.SLOWNESS)
                                        .getAmplifier() + 1) * (double) -0.15f * walkSpeed);

                            // Here we check if the player is onGround
                            if (movementData.isOnGround()) {
                                // Here is the calculation for the actual WalkSpeed
                                walkSpeed = (float) (walkSpeed * (0.16277136F / Math.pow(frictionBlock, 3)));
                                // Here we check if the player is Sprinting and jumping,
                                // this will multiply the Players XZ motion by 0.2F in the specific yaw angle
                                if (jumped && isSprinting) {
                                    float rot = rotationData.getYaw() * 0.017453292F;
                                    lastMotionX -= Math.sin(rot) * 0.2F;
                                    lastMotionZ += Math.cos(rot) * 0.2F;
                                }

                            } else {
                                // Here we set the WalkSpeed to the Minecraft's friction.
                                // We don't need to check for friction block or walkSpeed, cause in the air these dont matter
                                walkSpeed = isSprinting ? 0.026f : 0.02f;
                            }

                            double keyMotion = forwardMotion * forwardMotion + strafeMotion * strafeMotion;

                            if (keyMotion >= 1.0E-4F) {
                                keyMotion = walkSpeed / Math.max(1.0, Math.sqrt(keyMotion));
                                forwardMotion *= keyMotion;
                                strafeMotion *= keyMotion;


                                final float yaws = (float) Math.sin(rotationData.getYaw() * (float) Math.PI / 180.F),
                                        yawc = (float) Math.cos(rotationData.getYaw() * (float) Math.PI / 180.F);

                                lastMotionX += ((strafeMotion * yawc) - (forwardMotion * yaws));
                                lastMotionZ += ((forwardMotion * yawc) + (strafeMotion * yaws));
                            }

                            double delta = Math.pow(movementData.getDeltaX() - lastMotionX, 2)
                                    + Math.pow(movementData.getDeltaZ() - lastMotionZ, 2);

                            if (delta < sDelta) {
                                sDelta = delta;
                                playerMotionX = lastMotionX;
                                playerMotionZ = lastMotionZ;
                            }
                            sDelta = Math.min(delta, sDelta);
                        }
                    }
                }
            }
        }

        return new double[]{playerMotionX,playerMotionZ,sDelta};
    }

    public float getFriction(Block block) {
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

    public boolean mathOnGround(final double posY) {
        return posY % 0.015625 == 0;
    }
}
