package net.gteam.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.Data;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;

public class VelocityData implements Data {
    private final Profile profile;

    private Vector3d velocity = new Vector3d(0, 0, 0);
    private long timestamp;

    public VelocityData(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process(final ClientPlayPacket packet) {
    }

    @Override
    public void process(final ServerPlayPacket packet) {

        if (packet.is(PacketType.Play.Server.ENTITY_VELOCITY)) {
            WrapperPlayServerEntityVelocity velocity = packet.getEntityVelocityWrapper();
            if (velocity.getEntityId() == profile.getPlayer().getEntityId()) {
                this.velocity = velocity.getVelocity();
                timestamp = System.currentTimeMillis();
            }
        }
    }

    public Vector3d getVelocity() {
        return velocity;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
