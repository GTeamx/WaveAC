package com.xiii.wave.exempt;

import com.xiii.wave.data.PlayerData;
import lombok.Getter;

import java.util.function.Function;

@Getter
public enum ExemptType {

    AIR(data -> data.touchingAir),
    CLIMB(data -> data.touchingClimbableAt || data.touchingClimbableUnder),
    LOW_BLOCK(data -> data.touchingLowBlock),
    //FLY(data -> data.getPlayer().getClientVersion().isRelease()),
    LIQUID(data -> data.touchingLiquidAt || data.touchingLiquidUnder),

    RESPAWN(data -> System.currentTimeMillis() - data.lastRespawn <= 1000L),
    DAMAGE(data -> System.currentTimeMillis() - data.lastDamageTaken <= 600L);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }

}
