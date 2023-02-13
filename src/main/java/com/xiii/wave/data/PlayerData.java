package com.xiii.wave.data;

import com.xiii.wave.checks.CheckManager;
import com.xiii.wave.utils.BoundingBox;
import com.xiii.wave.utils.Observable;
import com.xiii.wave.utils.SampleList;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class PlayerData {

    public Player player;

    public List<Player> alertsList = new ArrayList<>();
    public CheckManager checkManager = new CheckManager();
    public HashMap<String, HashMap<String, Integer>> flagList = new HashMap<>();
    public Location from, to, sFrom, sTo;
    public SampleList<BoundingBox> boundingBoxes = new SampleList<>(10);
    public Observable<BoundingBox> boundingBox = new Observable<>(new BoundingBox(0, 0, 0, null));

    public boolean playerGround, serverGround, touchingAir, touchingLowBlock, touchingLiquid, touchingClimable;
    public double motionX, motionY, motionZ, lastMotionX, lastMotionY, lastMotionZ;
    public float deltaPitch, deltaYaw, lastDeltaPitch, lastDeltaYaw;

    public PlayerData(Player player) {

        this.player = player;

    }

}
