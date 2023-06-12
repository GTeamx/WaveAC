package com.xiii.wave.exempt;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.CombatData;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.utils.BetterStream;
import com.xiii.wave.utils.MathUtils;
import com.xiii.wave.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.List;

public class Exempt {

    private final Profile profile;

    public Exempt(Profile profile) {
        this.profile = profile;
    }

    private boolean fly, water, lava, climable, cobweb, trapdoor_door, cake, wall;

    private long lastWater, lastLava, lastClimable, lastCobweb;

    public void handleExempts(long timeStamp) {

        final MovementData movementData = profile.getMovementData();
        final CombatData combatData = profile.getCombatData();

        final List<Material> nearbyBlocks = movementData.getNearbyBlocks();

        //Fly
        this.fly = movementData.getLastFlyingAbility() < (20*5); //5s

        /*
        Easier to maintain using matchMaterial for future updates
        matchMaterial only works if it starts with the specified string
         */

        //Water Liquid
        this.water = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.matchMaterial("WATER", true)  || mat == Material.BUBBLE_COLUMN);

        //Lava Liquid
        this.lava = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.matchMaterial("LAVA", true));

        //Climables
        this.climable = BetterStream.anyMatch(nearbyBlocks, mat -> mat.toString().contains("LADDER") || mat.toString().contains("VINE"));

        //Cobweb
        this.cobweb = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.COBWEB);

        //Trapdoors
        this.trapdoor_door = BetterStream.anyMatch(nearbyBlocks, mat -> mat.toString().contains("DOOR"));

        //Cakes
        this.cake = BetterStream.anyMatch(nearbyBlocks, mat -> mat.toString().contains("CAKE"));

        this.wall = movementData.getLastNearWallTicks() < (5); // 0.25s

        if (this.water) this.lastWater = System.currentTimeMillis();

        if (this.lava) this.lastLava = System.currentTimeMillis();

        if (this.climable) this.lastClimable = System.currentTimeMillis();

        if (this.cobweb) this.lastCobweb = System.currentTimeMillis();

    }

    public boolean isFly() {
        return fly;
    }

    public boolean isWater(final long delay) {
        return MathUtils.elapsed(lastWater) <= delay;
    }

    public boolean isWall() {
        return wall;
    }

    public boolean isLava(final long delay) {
        return MathUtils.elapsed(lastLava) <= delay;
    }

    public boolean isClimable(final long delay) {
        return MathUtils.elapsed(lastClimable) <= delay;
    }

    public boolean isCobweb(final long delay) {
        return MathUtils.elapsed(lastCobweb) <= delay;
    }

    public boolean tookDamage(final long delay) {
        return MathUtils.elapsed(profile.getCombatData().getLastDamageTaken()) <= delay;
    }

    public boolean tookHandDamage(final long delay) {
        return MathUtils.elapsed(profile.getCombatData().getLastHandDamageTaken()) <= delay;
    }

    public boolean isTrapdoor_door() {
        return trapdoor_door;
    }

    public boolean isCake() {
        return cake;
    }
}
