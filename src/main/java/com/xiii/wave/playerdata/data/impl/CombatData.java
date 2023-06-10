package com.xiii.wave.playerdata.data.impl;

import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.playerdata.processors.impl.SetbackProcessor;
import com.xiii.wave.processors.Packet;
import com.xiii.wave.utils.TaskUtils;
import com.xiii.wave.utils.custom.Equipment;
import org.bukkit.Bukkit;

public class CombatData implements Data {

    private final Profile profile;

    private final Equipment equipment;

    public CombatData(Profile profile) {
        this.profile = profile;

        this.equipment = new Equipment();
    }

    @Override
    public void process(Packet packet) {

        switch (packet.getType()) {

            case ENTITY_ACTION:

                TaskUtils.task(() -> Bukkit.broadcastMessage(packet.getEntityActionWrapper() + ""));
        }
    }
}