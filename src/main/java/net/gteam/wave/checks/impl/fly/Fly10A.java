package net.gteam.wave.checks.impl.fly;

import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.MathUtils;
import net.gteam.wave.utils.MoveUtils;
import net.gteam.wave.utils.custom.CustomEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ConcurrentHashMap;

@Development
public class Fly10A extends Check {

    public Fly10A(final Profile profile) {
        super(profile, CheckType.FLY, "FL10A", "Checks for gravity modifications");
    }

    @Override
    public void handle(final ClientPlayPacket clientPlayPacket) {

        if (!clientPlayPacket.isMovement()) return;

        final MovementData movementData = this.profile.getMovementData();

        if (movementData.isOnGround()) return;

        final ConcurrentHashMap<PotionEffectType, CustomEffect> effects = this.profile.getEffectData().getEffects();

        final double deltaY = movementData.getDeltaY();
        final double predictedDeltaY = movementData.getPredictionProcessor().getPredictedDeltaY();
        final double maximumOffset = 1.9262653090336062E-14;

        final boolean nearGround = movementData.getNearGroundTicks() <= 4
                && (MathUtils.decimalRound(deltaY, 8) == -0.07840000
                    || MathUtils.decimalRound(deltaY, 8) == 0.01250005 // Hit head with a trapdoor above
                    || MathUtils.decimalRound(deltaY, 8) == 0.20000005); // Weird hit head spam

        final boolean jumpBlockAbove = (movementData.getBlocksAboveTicks() <= 1 || movementData.getLastBlocksAboveTicks() <= 1)
                && Math.abs(0.2000000476837 - MathUtils.decimalRound(deltaY, 13)) < maximumOffset;

        final boolean jumpLowBlock = movementData.getHalfBlocksTicks() <= 2
                || (movementData.getOffGroundTicks() == 1 && deltaY == 0.5);

        final boolean jumped = movementData.isLastOnGround()
                && Math.abs(deltaY - ((double)0.42F + (double)(profile.getEffectData().hasPotionEffect(PotionEffectType.JUMP_BOOST) ? (profile.getEffectData().getEffect(PotionEffectType.JUMP_BOOST).getAmplifier() + 1) * 0.1F : 0))) < 0.00004;

        final boolean exempt = jumped
                || jumpLowBlock
                || nearGround
                || profile.getTeleportData().getTeleportTicks() <= 2 // 2: validated
                || movementData.getFlyingTicks() <= 1 // 1: validated
                || profile.getPlayer().getAllowFlight() // true: validated
                || movementData.getSlimeTicks() <= 2 // 2: validated
                || movementData.getHoneyTicks() <= 1 // 1: validated
                || movementData.getClimbableTicks() <= 0 // 0: validated
                || movementData.getLiquidTicks() <= 3 // 3: validated
                || movementData.getBubbleTicks() <= 2 // 2: validated
                || movementData.getWebTicks() <= 2 // 2: validated
                || movementData.getBedTicks() <= 2 // 2: validated
                || movementData.getShulkerTicks() <= 5 // 5: validated
                || movementData.getGlidingTicks() <= 6 // 6: validated
                || movementData.getBerriesTicks() <= 1 // 1: validated
                || movementData.getPistonTicks() <= 1 // 1: validated
                //|| effects.getOrDefault(PotionEffectType.LEVITATION, null) != null // true: validated
                //|| effects.getOrDefault(PotionEffectType.SLOW_FALLING, null) != null // true: validated
                //|| effects.getOrDefault(PotionEffectType.JUMP_BOOST, null) != null // true: validated
                || profile.getVelocityData().getTicks() <= 300; // 300: validated // TODO: make better damage handler

        final double math = deltaY -
                (jumpBlockAbove
                ? deltaY // if jump block above
                : predictedDeltaY); // else
        final boolean invalid = Math.abs(math) > maximumOffset;

        if (invalid && !exempt) {

            fail("§a" + "deltaY=" + deltaY + "\n"
                    + "predictedDeltaY=" + predictedDeltaY + "\n"
                    + "maximumOffset=" + maximumOffset + "\n" // GREEN (§a) till here
                    + "§e" + "nearGround=" + nearGround + "\n"
                    + "  -- nearGroundTicks=" + movementData.getNearGroundTicks() + "\n"
                    + "  && decimalRound(8)=" + MathUtils.decimalRound(deltaY, 8) + "\n"
                    + "jumpBlockAbove=" + jumpBlockAbove + "\n"
                    + "  || blockAboveTicks=" + movementData.getBlocksAboveTicks() + "\n"
                    + "  || lastBlocksAboveTicks=" + movementData.getLastBlocksAboveTicks() + "\n"
                    + "  && decimalRound(13)=" + Math.abs(0.2000000476837 - MathUtils.decimalRound(deltaY, 13)) + "\n"
                    + "jumpLowBlock=" + jumpLowBlock + "\n"
                    + "  -- halfBlocksTicks=" + movementData.getHalfBlocksTicks() + "\n"
                    + "  || offGroundTicks=" + movementData.getOffGroundTicks() + "\n"
                    + "  || deltaY=" + deltaY + "\n"
                    + "jumped=" + false + "\n"
                    + "  -- lastOnGround=" + movementData.isLastOnGround() + "\n"
                    + "  && deltaY=" + deltaY + "\n" // YELLOW (§e) till here
                    + "§6" + "exempt=" + false + "\n"
                    + "  || jumped=" + false + "\n"
                    + "  || jumpedLowBlock=" + jumpLowBlock + "\n"
                    + "  || nearGround=" + nearGround + "\n"
                    + "  || teleportTicks=" + profile.getTeleportData().getTeleportTicks() + "\n"
                    + "  || flyTicks=" + movementData.getFlyingTicks() + "\n"
                    + "  || allowFlight=" + profile.getPlayer().getAllowFlight() + "\n"
                    + "  || slimeTicks=" + movementData.getSlimeTicks() + "\n"
                    + "  || honeyTicks=" + movementData.getHoneyTicks() + "\n"
                    + "  || climableTicks=" + movementData.getClimbableTicks() + "\n"
                    + "  || liquidTicks=" + movementData.getLiquidTicks() + "\n"
                    + "  || bubbleTicks=" + movementData.getBubbleTicks() + "\n"
                    + "  || webTicks=" + movementData.getWebTicks() + "\n"
                    + "  || bedTicks=" + movementData.getBedTicks() + "\n"
                    + "  || shulkerTicks=" + movementData.getShulkerTicks() + "\n"
                    + "  || glidingTicks=" + movementData.getGlidingTicks() + "\n"
                    + "  || berriesTicks=" + movementData.getBerriesTicks() + "\n"
                    + "  || pistonTicks=" + movementData.getPistonTicks() + "\n"
                    + "  || levitationEffect=" + (effects.getOrDefault(PotionEffectType.LEVITATION, null) != null) + "\n"
                    + "  || slowFallingEffect=" + (effects.getOrDefault(PotionEffectType.SLOW_FALLING, null) != null) + "\n"
                    + "  || jumpBoostEffect=" + (effects.getOrDefault(PotionEffectType.JUMP_BOOST, null) != null) + "\n"
                    + "  || velocityTicks=" + profile.getVelocityData().getTicks() + "\n" // ORANGE (§6) till here
                    + "§c" + "math=" + math + "\n"
                    + "  -- deltaY=" + deltaY + "\n"
                    + "  -- lastDeltaY=" + movementData.getLastDeltaY() + "\n"
                    + "  -- jumpBlockAbove=" + jumpBlockAbove + "\n"
                    + "  ?? deltaY=" + deltaY + "\n"
                    + "  :: predictedDeltaY=" + predictedDeltaY + "\n"
                    + "invalid=" + true + "\n"
                    + "  -- math=" + math + "\n"
                    + "  >> maximumOffset=" + maximumOffset + "\n"); // RED (§c) till here

        }
    }

    @Override
    public void handle(final ServerPlayPacket ignored) {}
}