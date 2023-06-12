package com.xiii.wave.playerdata.data.impl;

import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientEntityAction;
import com.xiii.wave.Wave;
import com.xiii.wave.managers.profile.Profile;
import com.xiii.wave.playerdata.data.Data;
import com.xiii.wave.processors.packet.client.ClientPlayPacket;
import com.xiii.wave.processors.packet.server.ServerPlayPacket;
import com.xiii.wave.utils.MiscUtils;
import com.xiii.wave.utils.custom.PlacedBlock;
import com.xiii.wave.utils.custom.desync.Desync;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ActionData implements Data {

    private GameMode gameMode;

    private boolean allowFlight, sneaking, sprinting;

    private final Desync desync;

    private PlacedBlock placedBlock;

    private ItemStack itemInMainHand = MiscUtils.EMPTY_ITEM, itemInOffHand = MiscUtils.EMPTY_ITEM;

    private int lastAllowFlightTicks, lastSleepingTicks, lastRidingTicks;

    /*
     * 1.9+
     */
    private int lastDuplicateOnePointSeventeenPacketTicks = 100;

    public ActionData(final Profile profile) {

        this.desync = new Desync(profile);

        //Initialize

        Player player = profile.getPlayer();

        this.gameMode = player.getGameMode();

        this.allowFlight = Wave.getInstance().getNmsManager().getNmsInstance().getAllowFlight(player);

        this.lastAllowFlightTicks = this.allowFlight ? 0 : 100;
    }

    @Override
    public void process(final ClientPlayPacket clientPlayPacket) {
        if (clientPlayPacket.is(PacketType.Play.Client.ENTITY_ACTION)) {
            final WrapperPlayClientEntityAction action = clientPlayPacket.getEntityActionWrapper();
            if(action.getAction() == WrapperPlayClientEntityAction.Action.START_SPRINTING) {
                this.sprinting = true;
            }
            if(action.getAction() == WrapperPlayClientEntityAction.Action.STOP_SPRINTING) {
                this.sprinting = false;
            }
            if(action.getAction() == WrapperPlayClientEntityAction.Action.START_SNEAKING) {
                this.sneaking = true;
            }
            if(action.getAction() == WrapperPlayClientEntityAction.Action.STOP_SNEAKING) {
                this.sneaking = false;
            }
        }
    }

    @Override
    public void process(final ServerPlayPacket serverPlayPacket) {

    }

    public int getLastRidingTicks() {
        return lastRidingTicks;
    }

    public PlacedBlock getPlacedBlock() {
        return placedBlock;
    }

    public boolean isSneaking() {
        return sneaking;
    }

    public boolean isSprinting() {
        return sprinting;
    }

    public ItemStack getItemInMainHand() {
        return itemInMainHand;
    }

    public ItemStack getItemInOffHand() {
        return itemInOffHand;
    }

    public Desync getDesync() {
        return desync;
    }

    public int getLastSleepingTicks() {
        return lastSleepingTicks;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getLastDuplicateOnePointSeventeenPacketTicks() {
        return lastDuplicateOnePointSeventeenPacketTicks;
    }
}