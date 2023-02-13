package com.xiii.wave.exempt;

import com.xiii.wave.data.PlayerData;
import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.utils.player.ClientVersion;
import lombok.Getter;
import org.bukkit.GameMode;

import java.util.function.Function;

@Getter
public enum ExemptType {

    LIQUID(data -> data.touchingLiquid);

    private final Function<PlayerData, Boolean> exception;

    ExemptType(final Function<PlayerData, Boolean> exception) {
        this.exception = exception;
    }

}
