package net.gteam.wave.nms;

import net.gteam.wave.utils.VersionUtils;

public class NMSManager {

    private final NMSInstance nmsInstance;

    public NMSManager() {

        switch (VersionUtils.getServerVersion()) {

            default:
                this.nmsInstance = new InstanceDefault();
                break;

            // TODO: code versions
        }
    }

    public NMSInstance getNmsInstance() {
        return nmsInstance;
    }
}