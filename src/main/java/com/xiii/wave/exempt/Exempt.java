package com.xiii.wave.exempt;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.impl.MovementData;
import com.xiii.wave.utils.BetterStream;
import com.xiii.wave.utils.MathUtils;
import org.bukkit.Material;

import java.util.List;

public class Exempt {

    private final Profile profile;

    public Exempt(Profile profile) {
        this.profile = profile;
    }

    private boolean fly, water, lava, climable, cobweb;

    private long lastWater, lastLava, lastClimable, lastCobweb;

    public void handleExempts(long timeStamp) {

        MovementData movementData = profile.getMovementData();

        final List<Material> nearbyBlocks = movementData.getNearbyBlocks();

        //Fly
        this.fly = movementData.getLastFlyingAbility() < (20*5); //5s

        //Water Liquid
        this.water = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.WATER  || mat == Material.BUBBLE_COLUMN || mat == Material.LEGACY_STATIONARY_WATER || mat == Material.LEGACY_WATER);

        //Lava Liquid
        this.lava = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.LAVA || mat == Material.LEGACY_LAVA || mat == Material.LEGACY_STATIONARY_LAVA);

        //Climables
        this.climable = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.LADDER || mat == Material.VINE || mat == Material.TWISTING_VINES || mat == Material.TWISTING_VINES_PLANT || mat == Material.WEEPING_VINES || mat == Material.WEEPING_VINES_PLANT || mat == Material.LEGACY_VINE || mat == Material.LEGACY_LADDER);

        //Cobweb
        this.cobweb = BetterStream.anyMatch(nearbyBlocks, mat -> mat == Material.COBWEB);

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

    public boolean isLava(final long delay) {
        return MathUtils.elapsed(lastLava) <= delay;
    }

    public boolean isClimable(final long delay) {
        return MathUtils.elapsed(lastClimable) <= delay;
    }

    public boolean isCobweb(final long delay) {
        return MathUtils.elapsed(lastCobweb) <= delay;
    }
}
