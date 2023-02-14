package com.xiii.wave.checks;

import com.xiii.wave.Wave;
import com.xiii.wave.checks.impl.movement.fly.FlyA;
import com.xiii.wave.checks.impl.packet.VPN;

import java.util.ArrayList;

public final class CheckManager {

    public ArrayList<Check> checkList = new ArrayList<>();

    public CheckManager() {

        registerCheck(new FlyA());

        registerCheck(new VPN());

    }

    private void registerCheck(Check check) {

        CheckInfo checkInfo = check.getClass().getAnnotation(CheckInfo.class);

        check.checkEnabled = Wave.INSTANCE.configUtils.getBoolean("checks", check.checkName + "." + "check-enabled", check.checkEnabled);

        check.checkName = Wave.INSTANCE.configUtils.getString("checks", check.checkName + "." + "check-name", check.checkName);
        check.checkDescription = Wave.INSTANCE.configUtils.getString("checks", check.checkName + "." + "check-description", check.checkDescription);
        check.checkCategory = checkInfo.checkCategory();
        check.checkState = checkInfo.checkState();

        check.canBan =  Wave.INSTANCE.configUtils.getBoolean("checks", check.checkName + "." + "check-can-ban", check.canBan);
        check.canKick = Wave.INSTANCE.configUtils.getBoolean("checks", check.checkName + "." + "check-can-kick", check.canKick);
        check.isSilent = Wave.INSTANCE.configUtils.getBoolean("checks", check.checkName + "." + "check-is-silent", check.isSilent);

        check.punishVL = Wave.INSTANCE.configUtils.getInt("checks", check.checkName + "." + "check-punish-vl", check.punishVL);

        check.maxBuffer = Wave.INSTANCE.configUtils.getDouble("checks", check.checkName + "." + "check-buffer-max", check.maxBuffer);
        check.removeBuffer = Wave.INSTANCE.configUtils.getDouble("checks", check.checkName + "." + "check-buffer-remove", check.removeBuffer);
        check.addBuffer = Wave.INSTANCE.configUtils.getDouble("checks", check.checkName + "." + "check-buffer-add", check.addBuffer);
        check.currentBuffer = checkInfo.currentBuffer();

        if(!checkList.contains(check)) checkList.add(check);

    }

}
