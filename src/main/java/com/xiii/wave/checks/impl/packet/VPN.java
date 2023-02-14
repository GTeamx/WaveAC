package com.xiii.wave.checks.impl.packet;

import com.github.retrooper.packetevents.event.simple.PacketPlaySendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.xiii.wave.Wave;
import com.xiii.wave.checks.*;
import com.xiii.wave.utils.HTTPUtils;

@CheckInfo(checkName = "VPN", checkDescription = "VPN/Proxy", checkCategory = CheckCategory.PACKET, checkState = CheckState.STABLE, addBuffer = 0, removeBuffer = 0, maxBuffer = 0, canKick = true, isSilent = true)
public final class VPN extends Check {

    @Packets(playServer = {PacketType.Play.Server.SPAWN_PLAYER}, playClient = {})
    public synchronized void handle(PacketPlaySendEvent packet) {

        final String vpnKey = Wave.INSTANCE.configUtils.getString("config", "vpn-checker-key", "DISABLED");
        if(!vpnKey.equalsIgnoreCase("DISABLED")) {

            final String httpResponse =  HTTPUtils.readUrl("https://proxycheck.io/v2/" + packet.getUser().getAddress().getHostName() + "?key=" + vpnKey + "&risk=1&vpn=1");
            //final String riskLevel = httpResponse.substring(httpResponse.indexOf("\"risk\":"));
            final String riskLevel = null;
            // TODO: Fix riskLevel

            if(httpResponse.contains("\"proxy\": \"yes\"") || httpResponse.contains("vpn")) {

                flag(null, "risk §b" + riskLevel);

            }

        }

    }

}
