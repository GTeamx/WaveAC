package com.xiii.wave.nms;

import com.github.retrooper.packetevents.manager.server.ServerVersion;
import com.xiii.wave.utils.versionutils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InstanceDefault implements NmsInstance {

    @Override
    public float getAttackCooldown(Player player) {
        return VersionUtils.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_16_1) ? player.getAttackCooldown() : 1F;
    }

    @Override
    public boolean isChunkLoaded(World world, int x, int z) {
        return world.isChunkLoaded(x >> 4, z >> 4);
    }

    @Override
    public Material getType(Block block) {
        return block.getType();
    }

    @Override
    public Entity[] getChunkEntities(World world, int x, int z) {
        return world.isChunkLoaded(x >> 4, z >> 4) ? world.getChunkAt(x >> 4, z >> 4).getEntities() : new Entity[0];
    }

    @Override
    public boolean isWaterLogged(Block block) {
        return VersionUtils.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13_1)
                && (block.getBlockData() instanceof org.bukkit.block.data.Waterlogged
                && ((org.bukkit.block.data.Waterlogged) block).isWaterlogged());
    }

    @Override
    public boolean isDead(Player player) {
        return player.isDead();
    }

    @Override
    public boolean isSleeping(Player player) {
        return player.isSleeping();
    }

    @Override
    public boolean isGliding(Player player) {
        return VersionUtils.getServerVersion().isNewerThan(ServerVersion.V_1_8_3) && player.isGliding();
    }

    @Override
    public boolean isInsideVehicle(Player player) {
        return player.isInsideVehicle();
    }

    @Override
    public boolean isRiptiding(Player player) {
        return VersionUtils.getServerVersion().isNewerThanOrEquals(ServerVersion.V_1_13_1) && player.isRiptiding();
    }

    @Override
    public boolean isBlocking(Player player) {
        return player.isBlocking();
    }

    @Override
    public boolean isSneaking(Player player) {
        return player.isSneaking();
    }

    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getItemInHand();
    }

    @Override
    public ItemStack getItemInOffHand(Player player) {
        return VersionUtils.getServerVersion().isNewerThan(ServerVersion.V_1_8_3) ? player.getInventory().getItemInOffHand() : null;
    }

    @Override
    public float getWalkSpeed(Player player) {
        return player.getWalkSpeed();
    }

    @Override
    public float getAttributeSpeed(Player player) {
        return VersionUtils.getServerVersion().isNewerThan(ServerVersion.V_1_8_3) ? (float) player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() : 0F;
    }

    @Override
    public boolean getAllowFlight(Player player) {
        return player.getAllowFlight();
    }

    @Override
    public boolean isFlying(Player player) {
        return player.isFlying();
    }

    @Override
    public float getFallDistance(Player player) {
        return player.getFallDistance();
    }
}