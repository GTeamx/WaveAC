package net.gteam.wave.checks.impl.ground;

import net.gteam.wave.checks.annotations.Development;
import net.gteam.wave.checks.enums.CheckType;
import net.gteam.wave.checks.types.Check;
import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.processors.ClientPlayPacket;
import net.gteam.wave.processors.ServerPlayPacket;
import net.gteam.wave.utils.BetterStream;
import org.bukkit.Material;

@Development
public class Ground10A extends Check {


    public Ground10A(final Profile profile) {
        super(profile, CheckType.GROUND, "GR10A", "Checks for ground modifications");
    }

    @Override
    public void handle(ClientPlayPacket clientPlayPacket) {
        if (!clientPlayPacket.isFlying()) return;

        final MovementData data = this.profile.getMovementData();

        final boolean testBoolean = !BetterStream.allMatch(data.getNearbyBlocksUnder(), material -> material.toString().equalsIgnoreCase("AIR"));

        final boolean serverGroundFix = !BetterStream.anyMatch(data.getNearbyBlocksUnder(), material -> material.toString().equalsIgnoreCase("AIR"));

        final boolean serverGround = data.isServerGround();

        final boolean clientGround = data.isOnGround();

        //debug(serverGround + " " + clientGround + " " + serverGroundFix + " " + BetterStream.allMatch(data.getNearbyBlocksUnder(), material -> material.toString().equalsIgnoreCase("AIR")));

        if (serverGround != clientGround && serverGroundFix && BetterStream.allMatch(data.getNearbyBlocksUnder(), material -> material.toString().equalsIgnoreCase("AIR"))) {

            if (increaseBufferBy(1) > 1) fail(serverGround + " " + clientGround + " " + serverGroundFix + " " + BetterStream.allMatch(data.getNearbyBlocksUnder(), material -> material.toString().equalsIgnoreCase("AIR")));

        } else decreaseBufferBy(0.02);

    }

    @Override
    public void handle(ServerPlayPacket clientPlayPacket) {}
}
