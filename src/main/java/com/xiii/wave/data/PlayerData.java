package com.xiii.wave.data;

import com.github.retrooper.packetevents.protocol.player.User;
import com.xiii.wave.checks.CheckManager;
import com.xiii.wave.checks.PredictionEngine;
import com.xiii.wave.exempt.Exempt;
import com.xiii.wave.utils.BoundingBox;
import com.xiii.wave.utils.Observable;
import com.xiii.wave.utils.SampleList;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@Setter
@Getter
public final class PlayerData {

    public Player player;
    public UUID uuid;

    public CheckManager checkManager = new CheckManager();
    public HashMap<String, HashMap<String, Integer>> flagList = new HashMap<>();
    public Location from, to, sFrom, sTo;
    public SampleList<BoundingBox> boundingBoxes = new SampleList<>(10);
    public Observable<BoundingBox> boundingBox = new Observable<>(new BoundingBox(0, 0, 0, null));
    public Exempt exempt = new Exempt(this);
    public PredictionEngine predictionEngine = new PredictionEngine(this);
    public String clientBrand, clientVersion;

    public boolean playerGround, serverGround, touchingAir, touchingLowBlock, touchingLiquidUnder, touchingClimbableUnder, touchingLiquidAt, touchingClimbableAt, alertsState;
    public double motionX, motionY, motionZ, lastMotionX, lastMotionY, lastMotionZ;
    public float deltaPitch, deltaYaw, lastDeltaPitch, lastDeltaYaw;
    public long lastRespawn = 10000L, lastDamageTaken = 10000L;

    public PlayerData(Player player) {

        this.player = player;
        this.uuid = player.getUniqueId();

    }

}
