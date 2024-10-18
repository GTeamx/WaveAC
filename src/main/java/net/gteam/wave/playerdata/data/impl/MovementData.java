package net.gteam.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import net.gteam.wave.Wave;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.nms.NMSInstance;
import net.gteam.wave.playerdata.data.Data;
import net.gteam.wave.playerdata.processors.impl.PredictionProcessor;
import net.gteam.wave.playerdata.processors.impl.SetbackProcessor;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.BetterStream;
import net.gteam.wave.utils.CollisionUtils;
import net.gteam.wave.utils.MoveUtils;
import net.gteam.wave.utils.custom.CustomLocation;
import net.gteam.wave.utils.custom.Equipment;
import net.gteam.wave.utils.custom.MaterialType;
import net.gteam.wave.utils.fastmath.FastMath;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class MovementData implements Data {

    private final Profile profile;

    private final Equipment equipment;

    private final SetbackProcessor setbackProcessor;
    private final PredictionProcessor predictionProcessor;

    private double deltaX, lastDeltaX, deltaZ, lastDeltaZ, deltaY, lastDeltaY, deltaXZ, lastDeltaXZ,
            accelXZ, lastAccelXZ, accelY, lastAccelY;

    private float fallDistance, lastFallDistance,
            baseGroundSpeed, baseAirSpeed,
            frictionFactor = MoveUtils.FRICTION_FACTOR, lastFrictionFactor = MoveUtils.FRICTION_FACTOR;

    private CustomLocation location, lastLocation;

    private List<Material> nearbyBlocks = null, nearbyBlocksBellow = null;
    private final HashMap<MaterialType, Integer> materialTypeTicks = new HashMap<>();
    private final HashMap<MaterialType, Integer> lastMaterialTypeTicks = new HashMap<>();
    private boolean onGround, lastOnGround, serverGround, lastServerGround;

    private int offGroundTicks, clientGroundTicks, serverGroundTicks, lastServerGroundTicks, flyingTicks,
            lastUnloadedChunkTicks = 100,
            lastFrictionFactorUpdateTicks, lastNearEdgeTicks, lastNearWallTicks, nearGroundTicks, lastNearGroundTicks, blocksAboveTicks, lastBlocksAboveTicks;

    public MovementData(final Profile profile) {
        this.profile = profile;

        this.equipment = new Equipment();
        this.setbackProcessor = new SetbackProcessor(profile);
        this.predictionProcessor = new PredictionProcessor(profile);

        this.location = this.lastLocation = new CustomLocation(profile.getPlayer().getLocation());
    }

    @Override
    public void process(final ClientPlayPacket packet) {

        final World world = profile.getPlayer().getWorld();

        final long currentTime = packet.getTimeStamp();

        switch (packet.getType()) {

            case PLAYER_POSITION:

                final WrapperPlayClientPlayerPosition move = packet.getPositionWrapper();

                this.lastOnGround = this.onGround;
                this.onGround = move.isOnGround();

                this.offGroundTicks = this.onGround ? 0 : this.offGroundTicks + 1;
                this.clientGroundTicks = this.onGround ? this.clientGroundTicks + 1 : 0;

                this.lastLocation = this.location;
                this.location = new CustomLocation(
                        world,
                        move.getLocation().getX(), move.getLocation().getY(), move.getLocation().getZ(),
                        this.location.getYaw(), this.location.getPitch(),
                        currentTime
                );

                processLocationData();

                break;

            case PLAYER_POSITION_AND_ROTATION:

                final WrapperPlayClientPlayerPositionAndRotation posLook = packet.getPositionLookWrapper();

                this.lastOnGround = this.onGround;
                this.onGround = posLook.isOnGround();

                this.offGroundTicks = this.onGround ? 0 : this.offGroundTicks + 1;
                this.clientGroundTicks = this.onGround ? this.clientGroundTicks + 1 : 0;

                this.lastLocation = this.location;
                this.location = new CustomLocation(
                        world,
                        posLook.getLocation().getX(), posLook.getLocation().getY(), posLook.getLocation().getZ(),
                        posLook.getYaw(), posLook.getPitch(),
                        currentTime
                );

                processLocationData();

                break;

            case PLAYER_ROTATION:

                final WrapperPlayClientPlayerRotation look = packet.getLookWrapper();

                this.lastOnGround = this.onGround;
                this.onGround = look.isOnGround();

                this.offGroundTicks = this.onGround ? 0 : this.offGroundTicks + 1;
                this.clientGroundTicks = this.onGround ? this.clientGroundTicks + 1 : 0;

                this.lastLocation = this.location;
                this.location = new CustomLocation(
                        world,
                        this.location.getX(), this.location.getY(), this.location.getZ(),
                        look.getYaw(), look.getPitch(),
                        currentTime
                );

                processLocationData();

                break;
        }
    }

    @Override
    public void process(final ServerPlayPacket packet) {}

    private void processLocationData() {

        final double lastDeltaX = this.deltaX;
        final double deltaX = this.location.getX() - this.lastLocation.getX();

        this.lastDeltaX = lastDeltaX;
        this.deltaX = deltaX;

        final double lastDeltaY = this.deltaY;
        final double deltaY = this.location.getY() - this.lastLocation.getY();

        this.lastDeltaY = lastDeltaY;
        this.deltaY = deltaY;

        final double lastAccelY = this.accelY;
        final double accelY = Math.abs(lastDeltaY - deltaY);

        this.lastAccelY = lastAccelY;
        this.accelY = accelY;

        final double lastDeltaZ = this.deltaZ;
        final double deltaZ = this.location.getZ() - this.lastLocation.getZ();

        this.lastDeltaZ = lastDeltaZ;
        this.deltaZ = deltaZ;

        final double lastDeltaXZ = this.deltaXZ;
        final double deltaXZ = FastMath.hypot(deltaX, deltaZ);

        this.lastDeltaXZ = lastDeltaXZ;
        this.deltaXZ = deltaXZ;

        final double lastAccelXZ = this.accelXZ;
        final double accelXZ = Math.abs(lastDeltaXZ - deltaXZ);

        this.lastAccelXZ = lastAccelXZ;
        this.accelXZ = accelXZ;

        //Process data
        processPlayerData();
    }

    private void handleNearbyBlocks() {

        final CollisionUtils.NearbyBlocksResult nearbyBlocksResult = CollisionUtils.getNearbyBlocks(this.location, false);

        // Near Ground

        this.lastNearGroundTicks = this.nearGroundTicks;

        this.nearGroundTicks = nearbyBlocksResult.isNearGround() ? 0 : this.nearGroundTicks + 1;

        // Block Above

        this.lastBlocksAboveTicks = this.blocksAboveTicks;

        this.blocksAboveTicks = nearbyBlocksResult.hasBlockAbove() ? 0 : this.blocksAboveTicks + 1;

        // Block Bellow

        this.nearbyBlocksBellow = nearbyBlocksResult.getBlockBelowTypes();

        // MaterialType ticks
        this.lastMaterialTypeTicks.putAll(this.materialTypeTicks);

        for (final MaterialType e : MaterialType.values()) {

            this.materialTypeTicks.putIfAbsent(e, 0);

            if (BetterStream.filter(nearbyBlocksResult.getBlockTypes(), material -> MaterialType.isMaterial(material.name(), e)).isEmpty()) this.materialTypeTicks.put(e, this.materialTypeTicks.get(e) + 1);
            else this.materialTypeTicks.put(e, 0);
        }
    }

    private void processPlayerData() {

        final Player player = profile.getPlayer();

        final NMSInstance nms = Wave.getInstance().getNmsManager().getNmsInstance();

        // Chunk

        if ((this.lastUnloadedChunkTicks = nms.isChunkLoaded(
                this.location.getWorld(), this.location.getBlockX(), this.location.getBlockZ())
                ? this.lastUnloadedChunkTicks + 1 : 0) > 10) {

            // Nearby Blocks

            handleNearbyBlocks();

            // Friction Factor

            this.frictionFactor = CollisionUtils.getBlockSlipperiness(
                    nms.getType(this.location.clone().subtract(0D, .825D, 0D).getBlock())
            );

            this.lastFrictionFactorUpdateTicks = this.frictionFactor != this.lastFrictionFactor ? 0 : this.lastFrictionFactorUpdateTicks + 1;

            this.lastFrictionFactor = this.frictionFactor;
        }

        // Setbacks

        if (this.nearGroundTicks > 1) this.setbackProcessor.process();

        // Near Wall

        this.lastNearWallTicks = CollisionUtils.isNearWall(this.location) ? 0 : this.lastNearWallTicks + 1;

        // Near Edge

        this.lastNearEdgeTicks = this.lastNearGroundTicks == 0 && CollisionUtils.isNearEdge(this.location) ? 0 : this.lastNearEdgeTicks + 1;

        // Server Ground

        final boolean lastServerGround = this.serverGround;

        final boolean serverGround = CollisionUtils.isServerGround(this.location.getY());

        this.lastServerGround = lastServerGround;

        this.serverGround = serverGround;

        this.serverGroundTicks = serverGround ? this.serverGroundTicks + 1 : 0;

        this.lastServerGroundTicks = serverGround ? 0 : this.lastServerGroundTicks + 1;

        // Equipment

        this.equipment.handle(player);

        // Fall Distance

        this.lastFallDistance = this.fallDistance;

        this.fallDistance = nms.getFallDistance(player);

        // Base Speed

        this.baseGroundSpeed = MoveUtils.getBaseGroundSpeed(profile);

        this.baseAirSpeed = MoveUtils.getBaseAirSpeed(profile);

        // Flying/Gamemode

        this.flyingTicks = player.isFlying() ? 0 : this.flyingTicks + 1;

        // Prediction Processor
        this.predictionProcessor.process();
    }

    public int getLastNearEdgeTicks() {
        return lastNearEdgeTicks;
    }

    public int getLastFrictionFactorUpdateTicks() {
        return lastFrictionFactorUpdateTicks;
    }

    public float getFrictionFactor() {
        return frictionFactor;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public float getBaseAirSpeed() {
        return baseAirSpeed;
    }

    public float getBaseGroundSpeed() {
        return baseGroundSpeed;
    }

    public int getLastNearWallTicks() {
        return lastNearWallTicks;
    }

    public int getClientGroundTicks() {
        return clientGroundTicks;
    }

    public int getLastUnloadedChunkTicks() {
        return lastUnloadedChunkTicks;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getLastDeltaX() {
        return lastDeltaX;
    }

    public double getDeltaZ() {
        return deltaZ;
    }

    public double getLastDeltaZ() {
        return lastDeltaZ;
    }

    public double getDeltaY() {
        return deltaY;
    }

    public double getLastDeltaY() {
        return lastDeltaY;
    }

    public double getDeltaXZ() {
        return deltaXZ;
    }

    public double getLastDeltaXZ() {
        return lastDeltaXZ;
    }

    public double getAccelXZ() {
        return accelXZ;
    }

    public double getLastAccelXZ() {
        return lastAccelXZ;
    }

    public double getAccelY() {
        return accelY;
    }

    public double getLastAccelY() {
        return lastAccelY;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    public float getLastFallDistance() {
        return lastFallDistance;
    }

    public CustomLocation getLocation() {
        return location;
    }

    public CustomLocation getLastLocation() {
        return lastLocation;
    }

    public SetbackProcessor getSetbackProcessor() {
        return setbackProcessor;
    }

    public PredictionProcessor getPredictionProcessor() {
        return predictionProcessor;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isServerGround() {
        return serverGround;
    }

    public int getOffGroundTicks() {
        return offGroundTicks;
    }

    public int getLastServerGroundTicks() {
        return lastServerGroundTicks;
    }

    public int getServerGroundTicks() {
        return serverGroundTicks;
    }

    public boolean isLastOnGround() {
        return lastOnGround;
    }

    public boolean isLastServerGround() {
        return lastServerGround;
    }

    public int getNearGroundTicks() {
        return nearGroundTicks;
    }

    public int getLastNearGroundTicks() {
        return lastNearGroundTicks;
    }

    public int getBlocksAboveTicks() {
        return blocksAboveTicks;
    }

    public int getLastBlocksAboveTicks() {
        return lastBlocksAboveTicks;
    }

    public List<Material> getNearbyBlocksBellow() {
        return nearbyBlocksBellow;
    }

    public int getHalfBlocksTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.HALF_BLOCK, -1);
    }

    public int getLastHalfBlocksTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.HALF_BLOCK, -1);
    }

    public int getBedTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.BED, -1);
    }

    public int getLastBedTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.BED, -1);
    }

    public int getSlimeTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.SLIME, -1);
    }

    public int getLastSlimeTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.SLIME, -1);
    }

    public int getClimbableTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.CLIMBABLE, -1);
    }

    public int getLastClimbableTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.CLIMBABLE, -1);
    }

    public int getAirTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.AIR, -1);
    }

    public int getLastAirTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.AIR, -1);
    }

    public int getFenceTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.FENCE, -1);
    }

    public int getLastFenceTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.FENCE, -1);
    }

    public int getSnowTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.SNOW, -1);
    }

    public int getLastSnowTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.SNOW, -1);
    }

    public int getShulkerTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.SHULKER, -1);
    }

    public int getLastShulkerTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.SHULKER, -1);
    }

    public int getPistonTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.PISTON, -1);
    }

    public int getLastPistonTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.PISTON, -1);
    }

    public int getTrapDoorTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.TRAPDOOR, -1);
    }

    public int getLastTrapDoorTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.TRAPDOOR, -1);
    }

    public int getStairsTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.STAIRS, -1);
    }

    public int getLastStairsTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.STAIRS, -1);
    }

    public int getIceTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.ICE, -1);
    }

    public int getLastIceTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.ICE, -1);
    }

    public int getWebTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.WEB, -1);
    }

    public int getLastWebTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.WEB, -1);
    }

    public int getSoulBlockTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.SOUL_BLOCK, -1);
    }

    public int getLastSoulBlockTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.SOUL_BLOCK, -1);
    }

    public int getHoneyTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.HONEY, -1);
    }

    public int getLastHoneyTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.HONEY, -1);
    }

    public int getBerriesTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.BERRIES, -1);
    }

    public int getLastBerriesTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.BERRIES, -1);
    }

    public int getScaffoldingTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.SCAFFOLDING, -1);
    }

    public int getLastScaffoldingTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.SCAFFOLDING, -1);
    }

    public int getBubbleTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.BUBBLE, -1);
    }

    public int getLastBubbleTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.BUBBLE, -1);
    }

    public int getLiquidTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.LIQUID, -1);
    }

    public int getLastLiquidTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.LIQUID, -1);
    }

    public int getWaterPlantTicks() {
        return materialTypeTicks.getOrDefault(MaterialType.WATER_PLANT, -1);
    }

    public int getLastWaterPlantTicks() {
        return lastMaterialTypeTicks.getOrDefault(MaterialType.WATER_PLANT, -1);
    }

    public int getFlyingTicks() {
        return flyingTicks;
    }

    public List<Material> getNearbyBlocks() {
        return nearbyBlocks;
    }
}
