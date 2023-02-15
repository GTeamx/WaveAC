package com.xiii.wave.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.SimplePacketListenerAbstract;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlayReceiveEvent;
import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPlayerFlying;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientPluginMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityVelocity;
import com.xiii.wave.Wave;
import com.xiii.wave.OLD.Check;
import com.xiii.wave.OLD.Packets;
import com.xiii.wave.data.Data;
import com.xiii.wave.data.PlayerData;
import com.xiii.wave.utils.BoundingBox;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.material.Ladder;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.Vine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public final class PacketListener extends SimplePacketListenerAbstract {

    public PacketListener() { super(PacketListenerPriority.NORMAL); }

    @Override
    public synchronized void onPacketPlayReceive(PacketPlayReceiveEvent event) {

        if(event.getUser() != null && Data.getPlayerData(Bukkit.getPlayer(event.getUser().getName())) != null) {

            final PlayerData data = Data.getPlayerData(Bukkit.getPlayer(event.getUser().getName()));

            for(final Check check : data.getCheckManager().checkList) {

                check.data = data;

                for(final Method method : check.getClass().getMethods()) {

                    if(method.isAnnotationPresent(Packets.class)) {

                        final PacketType.Play.Client[] packets = method.getAnnotation(Packets.class).playClient();
                        final Type[] parameters = method.getGenericParameterTypes();

                        for(final PacketType.Play.Client packetByte : packets) {

                            if(packetByte == event.getPacketType()) {

                                try {

                                    for(final Type type : parameters) {

                                        if(type.getTypeName().equals(PacketPlayReceiveEvent.class.getTypeName())) method.invoke(check, event);

                                    }

                                } catch(final IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }

                            }

                        }

                    }

                }

            }

            if (event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION_AND_ROTATION || event.getPacketType() == PacketType.Play.Client.PLAYER_POSITION || event.getPacketType() == PacketType.Play.Client.PLAYER_FLYING) {

                assert event.getUser() != null;
                WrapperPlayClientPlayerFlying wrappedPacketInFlying = new WrapperPlayClientPlayerFlying(event);
                Location from = new Location((Bukkit.getPlayer(event.getUser().getName())).getWorld(), wrappedPacketInFlying.getLocation().getX(), wrappedPacketInFlying.getLocation().getY(), wrappedPacketInFlying.getLocation().getZ());

                if (wrappedPacketInFlying.hasRotationChanged()) {

                    from.setYaw(wrappedPacketInFlying.getLocation().getYaw());
                    from.setPitch(wrappedPacketInFlying.getLocation().getPitch());

                } else {

                    from.setYaw((Bukkit.getPlayer(event.getUser().getName())).getLocation().getYaw());
                    from.setPitch((Bukkit.getPlayer(event.getUser().getName())).getLocation().getPitch());

                }

                // TODO: Do teleport handler

                final BoundingBox boundingBox = new BoundingBox((Bukkit.getPlayer(event.getUser().getName())).getLocation().getX(), (Bukkit.getPlayer(event.getUser().getName())).getLocation().getY(), (Bukkit.getPlayer(event.getUser().getName())).getLocation().getZ(), (Bukkit.getPlayer(event.getUser().getName())).getLocation().getWorld());

                data.boundingBox.set(boundingBox);
                data.boundingBoxes.add(boundingBox);

                handleCollisions(boundingBox, Data.getPlayerData(Bukkit.getPlayer(event.getUser().getName())));

                data.playerGround = wrappedPacketInFlying.isOnGround();
                data.serverGround = from.clone().getY() % 0.015625 == 0.0;
                data.from = data.to;
                data.to = from;
                data.sFrom = data.sTo;
                data.sTo = from;

                if(data.from == null) data.from = data.to;

                data.lastMotionX = data.motionX;
                data.lastMotionY = data.motionY;
                data.lastMotionZ = data.motionZ;
                data.lastDeltaPitch = data.deltaPitch;
                data.lastDeltaYaw = data.deltaYaw;
                data.deltaPitch = data.to.getPitch() - data.from.getPitch();
                float to180 = data.to.getYaw() - data.from.getYaw();

                if ((to180 %= 360.0f) >= 180.0f) to180 -= 360.0f;
                if (to180 < -180.0f) to180 += 360.0f;

                data.deltaYaw = to180;
                data.motionX = data.to.getX() - data.from.getX();
                data.motionY = data.to.getY() - data.from.getY();
                data.motionZ = data.to.getZ() - data.from.getZ();

                // TODO: Do cinematic handler
                // TODO: Handle slime, teleport

            }

            // ClientBrandListener
            if(event.getPacketType() == PacketType.Play.Client.PLUGIN_MESSAGE) {

                final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '&' + "[0-9A-FK-OR]");
                final WrapperPlayClientPluginMessage wrappedPacketInCustomPayload = new WrapperPlayClientPluginMessage(event);
                final String brand = ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(new String(wrappedPacketInCustomPayload.getData(), StandardCharsets.UTF_8).substring(1)).replaceAll(""));

                data.clientBrand = brand;
                data.clientVersion = PacketEvents.getAPI().getPlayerManager().getClientVersion(event.getUser()).name().replaceAll("_", ".").substring(2);

                for (Player p : Bukkit.getOnlinePlayers()) {

                    if (p.hasPermission(Wave.INSTANCE.configUtils.getStringConverted("config", Bukkit.getPlayer(event.getUser().getName()), "permissions.brand-alerts", "Wave.alerts.brand"))) p.sendMessage(Wave.INSTANCE.configUtils.getStringConverted("config", data.getPlayer(), "prefix", "§f[§b§lWave§f]") + " §b" + event.getUser().getName() + " §fhas joined using §b" + data.clientBrand + " §fin §b" + data.clientVersion);

                }
            }

        }

    }

    @Override
    public synchronized void onPacketPlaySend(PacketPlaySendEvent event) {

        if(event.getUser() != null) {

            if(event.getPacketType() == PacketType.Play.Server.JOIN_GAME) {

                Bukkit.getScheduler().runTask(Wave.INSTANCE, () -> Bukkit.broadcastMessage("CONNECTION: " + event.getUser().getName() + " " + event.getUser().getUUID()));

                Bukkit.getScheduler().runTaskLater(Wave.INSTANCE, () -> {
                    Player player = Bukkit.getServer().getPlayer(event.getUser().getName());
                    Bukkit.getScheduler().runTask(Wave.INSTANCE, () -> Bukkit.broadcastMessage("PLAYER: " + player));

                    Data.registerPlayerData(player);
                }, 1);

            }

            PlayerData data = Data.getPlayerData(Bukkit.getPlayer(event.getUser().getName()));

            if(data != null) {

                for(final Check check : data.getCheckManager().checkList) {

                    check.data = data;

                    for(final Method method : check.getClass().getMethods()) {

                        if(method.isAnnotationPresent(Packets.class)) {

                            final PacketType.Play.Server[] packets = method.getAnnotation(Packets.class).playServer();
                            final Type[] parameters = method.getGenericParameterTypes();

                            for(final PacketType.Play.Server packetByte : packets) {

                                if(packetByte == event.getPacketType()) {

                                    try {

                                        for(final Type type : parameters) {

                                            if(type.getTypeName().equals(PacketPlaySendEvent.class.getTypeName())) method.invoke(check, event);

                                        }

                                    } catch(final IllegalAccessException | InvocationTargetException e) {
                                        e.printStackTrace();
                                    }

                                }

                            }

                        }

                    }

                }

            }

            // onRespawn
            if(event.getPacketType() == PacketType.Play.Server.RESPAWN) {

                data.lastRespawn = System.currentTimeMillis();

            }

            // onDamage
            if(event.getPacketType() == PacketType.Play.Server.ENTITY_VELOCITY) {

                WrapperPlayServerEntityVelocity wrappedPacketOutEntityVelocity = new WrapperPlayServerEntityVelocity(event);

                if(wrappedPacketOutEntityVelocity.getEntityId() == event.getUser().getEntityId()) data.lastDamageTaken = System.currentTimeMillis();

            }

        }

    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {

        Bukkit.getScheduler().runTask(Wave.INSTANCE, () -> Bukkit.broadcastMessage("DISCONNECT: " + event.getUser().getUUID()));

        Data.clearPlayerData(Bukkit.getPlayer(event.getUser().getUUID()));
    }

    /*
     * Credits to Frequency, taken from their PositionManager class, including their Observable and BoundingBox classes.
     */

    private synchronized void handleCollisions(final BoundingBox boundingBox, final PlayerData data) {
        
        // Blocks under
        boundingBox.expand(0.5, 0.07, 0.5).move(0.0, -0.55, 0.0);

        final boolean touchingAir = boundingBox.checkBlocks(material -> material == Material.AIR, true);
        data.touchingLiquidUnder = boundingBox.checkBlocks(material -> material.toString().contains("WATER") || material.toString().contains("LAVA"), false);
        data.touchingLowBlock = boundingBox.checkBlocks(material -> material.getData() == Stairs.class || material.getData() == Step.class, false);
        data.touchingClimbableUnder = boundingBox.checkBlocks(material -> material.getData() == Ladder.class || material.getData() == Vine.class, false);
        final boolean touchingIllegalBlock = boundingBox.checkBlocks(material -> material == Material.BREWING_STAND, false);

        data.touchingAir = (touchingAir && !touchingIllegalBlock);
        
        // Blocks at
        boundingBox.expand(-0.5, 0.0, -0.5).move(0.0, 1, 0.0);
        
        data.touchingLiquidAt = boundingBox.checkBlocks(material -> material.toString().contains("WATER") || material.toString().contains("LAVA"), false);
        data.touchingClimbableAt = boundingBox.checkBlocks(material -> material.getData() == Ladder.class || material.getData() == Vine.class, false);
        
        

    }

}
