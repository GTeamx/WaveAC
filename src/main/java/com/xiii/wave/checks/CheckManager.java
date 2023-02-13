package com.xiii.wave.checks;

import com.xiii.wave.checks.impl.movement.fly.FlyA;

import java.util.ArrayList;

public class CheckManager {

    public ArrayList<Check> checkList = new ArrayList<>();

    public CheckManager() {

        registerCheck(new FlyA());

    }

    private void registerCheck(Check check) {

        CheckInfo checkInfo = check.getClass().getAnnotation(CheckInfo.class);

        check.checkName = checkInfo.checkName();
        check.checkDescription = checkInfo.checkDescription();
        check.checkCategory = checkInfo.checkCategory();
        check.checkState = checkInfo.checkState();

        check.canBan = checkInfo.canBan();
        check.canKick = checkInfo.canKick();
        check.isSilent = checkInfo.isSilent();

        check.punishVL = checkInfo.punishVL();

        check.maxBuffer = checkInfo.maxBuffer();
        check.removeBuffer = checkInfo.removeBuffer();
        check.addBuffer = checkInfo.addBuffer();
        check.currentBuffer = checkInfo.currentBuffer();

        if(!checkList.contains(check)) checkList.add(check);

    }

}
