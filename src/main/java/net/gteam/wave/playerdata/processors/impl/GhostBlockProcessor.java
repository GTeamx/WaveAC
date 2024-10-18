package net.gteam.wave.playerdata.processors.impl;

import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.playerdata.processors.Processor;
import net.gteam.wave.utils.TaskUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class GhostBlockProcessor implements Processor {

    private final Profile profile;

    public GhostBlockProcessor(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() {

        final MovementData movementData = profile.getMovementData();

        if (movementData.isOnGround() && movementData.isServerGround()) {

            if (movementData.getNearGroundTicks() >= 2) {

                final Player player = profile.getPlayer();

                TaskUtils.task(() -> {

                player.getLocation().subtract(0, 1, 0).getBlock().setType(Material.STONE);

                player.sendBlockChange(player.getLocation().subtract(0, 1, 0), player.getLocation().subtract(0, 1, 0).getBlock().getBlockData());

                Bukkit.broadcastMessage("Ghost block processed for " + player.getName());

                });
            }
        }
    }
}
