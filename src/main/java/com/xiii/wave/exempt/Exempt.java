package com.xiii.wave.exempt;

import com.xiii.wave.data.PlayerData;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.function.Function;

@RequiredArgsConstructor
public final class Exempt {

    private final PlayerData data;

    public boolean isExempt(final ExemptType exemptType) {
        return exemptType.getException().apply(data);
    }

    public boolean isExempt(final ExemptType... exemptTypes) { return Arrays.stream(exemptTypes).anyMatch(this::isExempt); }

    public boolean isExempt(final Function<PlayerData, Boolean> exception) {
        return exception.apply(data);
    }

}
