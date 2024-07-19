package net.gteam.wave.processors.listeners;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPosition;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerPositionAndRotation;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerRotation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import net.gteam.wave.Wave;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.ChatUtils;
import net.gteam.wave.utils.MoveUtils;
import net.gteam.wave.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class NetworkListener extends SimplePacketListenerAbstract {

    private final Wave plugin;

    public NetworkListener(final Wave plugin) {
        super(PacketListenerPriority.HIGHEST);

        this.plugin = plugin;
    }

    @Override
    public void onPacketPlayReceive(final PacketPlayReceiveEvent e) {
        if (e.isCancelled() || e.getPlayer() == null) return;

        final Player player = (Player) e.getPlayer();

        final ClientPlayPacket clientPlayPacket = new ClientPlayPacket(e.getPacketType(), e, e.getTimestamp());

        final String crashAttempt = checkCrasher(clientPlayPacket);

        if (crashAttempt != null) {

            e.setCancelled(true);

            ChatUtils.log(Level.SEVERE, "Kicking " + player.getName() + " for attempting to crash the server, Module: " + crashAttempt);

            // Kick the player on the main thread
            TaskUtils.task(() -> player.kickPlayer("Invalid Packet"));

            return;
        }

        final Profile profile = this.plugin.getProfileManager().getProfile(player);

        if (profile == null) return;

        profile.getProfileThread().execute(() -> profile.handle(clientPlayPacket));
    }

    @Override
    public void onPacketPlaySend(final PacketPlaySendEvent e) {
        if (e.isCancelled() || e.getPlayer() == null) return;

        final Player player = (Player) e.getPlayer();

        final ServerPlayPacket serverPlayPacket = new ServerPlayPacket(e.getPacketType(), e, e.getTimestamp());

        final Profile profile = this.plugin.getProfileManager().getProfile(player);

        if (profile == null) return;

        final int playerId = player.getEntityId();

        switch (e.getPacketType()) {

            case ENTITY_VELOCITY:

                final WrapperPlayServerEntityVelocity velocity = new WrapperPlayServerEntityVelocity(e);

                if (velocity.getEntityId() != playerId) return;

                break;
        }

        profile.getProfileThread().execute(() -> profile.handle(serverPlayPacket));
    }

    private String checkCrasher(final ClientPlayPacket clientPlayPacket) {

        double x = 0D, y = 0D, z = 0D;
        float yaw = 0F, pitch = 0F;

        switch (clientPlayPacket.getType()) {

            case PLAYER_POSITION:

                final WrapperPlayClientPlayerPosition pos = clientPlayPacket.getPositionWrapper();

                x = Math.abs(pos.getPosition().getX());
                y = Math.abs(pos.getPosition().getY());
                z = Math.abs(pos.getPosition().getZ());

                break;

            case PLAYER_POSITION_AND_ROTATION:

                final WrapperPlayClientPlayerPositionAndRotation posLook = clientPlayPacket.getPositionLookWrapper();

                x = Math.abs(posLook.getPosition().getX());
                y = Math.abs(posLook.getPosition().getY());
                z = Math.abs(posLook.getPosition().getZ());
                yaw = Math.abs(posLook.getYaw());
                pitch = Math.abs(posLook.getPitch());

                break;

            case PLAYER_ROTATION:

                final WrapperPlayClientPlayerRotation look = clientPlayPacket.getLookWrapper();

                yaw = Math.abs(look.getYaw());
                pitch = Math.abs(look.getPitch());

                break;
        }

        final double invalidValue = 3.0E7D;

        //This messes our threading system and potentially causes damage to the server.
        final boolean invalid = x > invalidValue || y > invalidValue || z > invalidValue
                || yaw > 3.4028235e+35F
                || pitch > MoveUtils.MAXIMUM_PITCH;

        //It's impossible for these values to be NaN or Infinite.
        final boolean impossible = !Double.isFinite(x)
                || !Double.isFinite(y)
                || !Double.isFinite(z)
                || !Float.isFinite(yaw)
                || !Float.isFinite(pitch);

        if (invalid || impossible) {

            return "Invalid Position, X: " + x + " Y: " + y + " Z: " + z + " Yaw: " + yaw + " Pitch: " + pitch;
        }

        return null;
    }
}