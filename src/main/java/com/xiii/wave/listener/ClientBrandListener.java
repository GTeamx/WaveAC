package com.xiii.wave.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.xiii.wave.Wave;

public class ClientBrandListener extends SimplePacketListenerAbstract {

    private final Wave plugin;

    public ClientBrandListener(Wave plugin) {
        super(PacketListenerPriority.LOWEST);

        this.plugin = plugin;

        PacketEvents.getAPI().getEventManager().registerListener(this);

    }

    // TODO: Do ClientBraNDlISTENER

}
