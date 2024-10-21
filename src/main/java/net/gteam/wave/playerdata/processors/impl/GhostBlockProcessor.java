package net.gteam.wave.playerdata.processors.impl;

import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.data.impl.MovementData;
import net.gteam.wave.playerdata.processors.Processor;
import net.gteam.wave.utils.TaskUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GhostBlockProcessor implements Processor {

    private final Profile profile;

    private Location setbackLocation;

    public GhostBlockProcessor(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() { // TODO: improve overall ghost block handler

        final MovementData movementData = profile.getMovementData();

        final Player player = profile.getPlayer();

        TaskUtils.task(() -> { // Don't ask why I have to do all the bool stuff inside of it, only Java knows why

            if (movementData.isOnGround() && movementData.isServerGround() && movementData.getNearGroundTicks() >= 2) {

                final SetbackProcessor setbackProcessor = profile.getMovementData().getSetbackProcessor();

                if (setbackProcessor.getSetbackLocation() != null && setbackProcessor.getSetbackLocation().distance(player.getLocation()) <= 5) { // Prevent abuses

                    setbackProcessor.setback(false); // TODO: Update block instead of lagback?

                } else {

                    if (setbackLocation.subtract(0, 0.5, 0).getBlock().getType().equals(Material.AIR)) {

                        setbackLocation.subtract(0, 0.5, 0);

                        player.teleport(setbackLocation);

                    } else {

                        player.teleport(player.getLocation());

                    }

                }

            } else {

                setbackLocation = player.getLocation();

            }

        });
    }
}
