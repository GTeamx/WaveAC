package net.gteam.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerPositionAndLook;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.Data;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import org.bukkit.util.Vector;

import java.util.ArrayDeque;

public class TeleportData implements Data {


    private int teleportTicks = 0;
    private final Profile profile;
    private final ArrayDeque<Vector> teleports = new ArrayDeque<>();

    public TeleportData(Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process(final ClientPlayPacket packet) {
        if (!packet.isMovement()) return;
        final MovementData data = profile.getMovementData();

        checkTeleports(data.getLocation().getX(), data.getLocation().getY(), data.getLocation().getZ());

    }

    @Override
    public void process(final ServerPlayPacket packet) {
        if (packet.is(PacketType.Play.Server.PLAYER_POSITION_AND_LOOK)) {

            final WrapperPlayServerPlayerPositionAndLook wrapper = packet.getPlayerPositionAndLookWrapper();

            final Vector teleportVector = new Vector(
                    wrapper.getX(),
                    wrapper.getY(),
                    wrapper.getZ()
            );

            teleports.add(teleportVector);
        }
    }

    private void checkTeleports(double x, double y, double z) {
        teleportTicks = teleports.isEmpty() ? teleportTicks + 1 : 0;

        for (Vector vector : teleports) {
            final double dx = Math.abs(x - vector.getX());
            final double dy = Math.abs(y - vector.getY());
            final double dz = Math.abs(z - vector.getZ());

            if (dx == 0 && dy == 0 && dz == 0) {
                teleports.remove(vector);
            }
        }
    }

    public int getTeleportTicks() {
        return teleportTicks;
    }
}
