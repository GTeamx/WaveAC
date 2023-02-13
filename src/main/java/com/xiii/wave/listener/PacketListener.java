package com.xiii.wave.listener;

import com.xiii.wave.Wave;
import com.xiii.wave.checks.Check;
import com.xiii.wave.checks.Packets;
import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import com.xiii.wave.utils.BoundingBox;
import io.github.retrooper.packetevents.event.PacketListenerAbstract;
import io.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import io.github.retrooper.packetevents.packettype.PacketType;
import io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import io.github.retrooper.packetevents.packetwrappers.play.in.flying.WrappedPacketInFlying;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.material.Ladder;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class PacketListener extends PacketListenerAbstract {

    public PacketListener() { super(PacketListenerPriority.NORMAL); }

    @Override
    public synchronized void onPacketPlayReceive(PacketPlayReceiveEvent event) {

        if(event.getPlayer() != null && Data.getPlayerData(event.getPlayer()) != null) {

            final PlayerData data = Data.getPlayerData(event.getPlayer());

            for(final Check check : data.checkManager.checkList) {

                check.data = data;

                for(final Method method : check.getClass().getMethods()) {

                    if(method.isAnnotationPresent(Packets.class)) {

                        final byte[] packets = method.getAnnotation(Packets.class).packets();
                        final Type[] parameters = method.getGenericParameterTypes();

                        for(final byte packetByte : packets) {

                            if(packetByte == event.getPacketId()) {

                                try {

                                    for(final Type type : parameters) {

                                        if(type.getTypeName().equals(PacketPlayReceiveEvent.class.getTypeName())) method.invoke(check, event);
                                        if(type.getTypeName().equals(WrappedPacket.class.getTypeName())) method.invoke(check, new WrappedPacket(event.getNMSPacket()));

                                    }

                                } catch (final IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                    }

                }

            }

            if (event.getPacketId() == PacketType.Play.Client.POSITION || event.getPacketId() == PacketType.Play.Client.POSITION_LOOK || event.getPacketId() == PacketType.Play.Client.FLYING) {

                WrappedPacketInFlying wrappedPacketInFlying = new WrappedPacketInFlying(event.getNMSPacket());
                Location from = new Location(event.getPlayer().getWorld(), wrappedPacketInFlying.getPosition().getX(), wrappedPacketInFlying.getPosition().getY(), wrappedPacketInFlying.getPosition().getZ());

                if (wrappedPacketInFlying.isRotating()) {

                    from.setYaw(wrappedPacketInFlying.getYaw());
                    from.setPitch(wrappedPacketInFlying.getPitch());

                } else {

                    from.setYaw(event.getPlayer().getLocation().getYaw());
                    from.setPitch(event.getPlayer().getLocation().getPitch());

                }

                // TODO: Do teleport handler

                final BoundingBox boundingBox = new BoundingBox(event.getPlayer().getLocation().getX(), event.getPlayer().getLocation().getY(), event.getPlayer().getLocation().getZ(), event.getPlayer().getLocation().getWorld());

                data.boundingBox.set(boundingBox);
                data.boundingBoxes.add(boundingBox);

                handleCollisions(boundingBox, Data.getPlayerData(event.getPlayer()));

                data.playerGround = wrappedPacketInFlying.isOnGround();
                data.serverGround = from.clone().getY() % 0.015625 == 0.0;
                data.from = data.to;
                data.to = from;
                data.sFrom = data.sTo;
                data.sTo = from;

                if(from == null) from = data.to;

                data.lastMotionX = data.motionX;
                data.lastMotionY = data.motionY;
                data.lastMotionZ = data.motionZ;
                data.lastDeltaPitch = data.deltaPitch;
                data.lastDeltaYaw = data.deltaYaw;
                data.deltaPitch = data.to.getPitch() - from.getPitch();
                float to180 = data.to.getYaw() - from.getYaw();

                if ((to180 %= 360.0f) >= 180.0f) to180 -= 360.0f;
                if (to180 < -180.0f) to180 += 360.0f;

                data.deltaYaw = to180;
                data.motionX = data.to.getX() - from.getX();
                data.motionY = data.to.getY() - from.getY();
                data.motionZ = data.to.getZ() - from.getZ();

                // TODO: Do cinematic handler
                // TODO: Handle slime, teleport, animation ???, flying, death, respawn ect...

            }

        }

    }

    @Override
    public synchronized void onPacketPlaySend(PacketPlaySendEvent event) {

        if(event.getPlayer() != null) {

            // onJoin
            if(event.getPacketId() == PacketType.Play.Server.LOGIN) {

                Data.registerPlayerData(event.getPlayer());

            }

            PlayerData data = Data.getPlayerData(event.getPlayer());

            for(final Check check : data.checkManager.checkList) {

                check.data = data;

                for(final Method method : check.getClass().getMethods()) {

                    if(method.isAnnotationPresent(Packets.class)) {

                        final byte[] packets = method.getAnnotation(Packets.class).packets();
                        final Type[] parameters = method.getGenericParameterTypes();

                        for(final byte packetByte : packets) {

                            if(packetByte == event.getPacketId()) {

                                try {

                                    for(final Type type : parameters) {

                                        if(type.getTypeName().equals(PacketPlaySendEvent.class.getTypeName())) method.invoke(check, event);
                                        if(type.getTypeName().equals(WrappedPacket.class.getTypeName())) method.invoke(check, new WrappedPacket(event.getNMSPacket()));

                                    }

                                } catch (final IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                    }

                }

            }

            // onQuit
            if(event.getPacketId() == PacketType.Play.Server.KICK_DISCONNECT) {

                // TODO: Test it
                Bukkit.getScheduler().runTask(Wave.INSTANCE, () -> {
                   Bukkit.broadcastMessage("KICK_DISCONNECT: " + event.getPlayer());
                });
                Data.clearPlayerData(event.getPlayer());

            }

        }

    }

    /*
     * Credits to Frequency, taken from their PositionManager class, including their Observable and BoundingBox class.
     */

    private synchronized void handleCollisions(final BoundingBox boundingBox, final PlayerData data) {

        boundingBox.expand(0.5, 0.07, 0.5).move(0.0, -0.55, 0.0);

        final boolean touchingAir = boundingBox.checkBlocks(material -> material == Material.AIR);
        final boolean touchingLiquid = boundingBox.checkBlocks(material -> material == Material.WATER || material == Material.LAVA);
        final boolean touchingLowBlock = boundingBox.checkBlocks(material -> material.getData() == Stairs.class || material.getData() == Step.class);
        final boolean touchingClimbable = boundingBox.checkBlocks(material -> material.getData() == Ladder.class);
        final boolean touchingIllegalBlock = boundingBox.checkBlocks(material -> material == Material.LILY_PAD || material == Material.BREWING_STAND);

        data.touchingAir = (touchingAir && !touchingIllegalBlock);
        data.touchingLiquid = (touchingLiquid);
        data.touchingLowBlock = (touchingLowBlock);
        data.touchingClimable = (touchingClimbable);

    }

}
