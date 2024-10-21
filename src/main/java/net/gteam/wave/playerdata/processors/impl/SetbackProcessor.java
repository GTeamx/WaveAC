package net.gteam.wave.playerdata.processors.impl;

import net.gteam.wave.managers.profile.Profile;
import net.gteam.wave.playerdata.processors.Processor;
import net.gteam.wave.tasks.TickTask;
import net.gteam.wave.utils.MathUtils;
import net.gteam.wave.utils.SampleList;
import net.gteam.wave.utils.TaskUtils;
import net.gteam.wave.utils.custom.CustomLocation;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SetbackProcessor implements Processor {

    private final SampleList<CustomLocation> locations = new SampleList<>(10, true);

    private final Profile profile;
    private int lastSetbackTicks, lastStoredLocationTicks;

    public SetbackProcessor(final Profile profile) {
        this.profile = profile;
    }

    @Override
    public void process() {
        if (MathUtils.elapsedTicks(this.lastStoredLocationTicks) < 20
                || MathUtils.elapsedTicks(this.lastSetbackTicks) < 40) return;

        this.locations.add(profile.getMovementData().getLocation());

        this.lastStoredLocationTicks = TickTask.getCurrentTick();
    }

    public void setback(final boolean exemptTime) {
        if (exemptTime && MathUtils.elapsedTicks(this.lastSetbackTicks) < 5) return;

        this.lastSetbackTicks = TickTask.getCurrentTick();

        final Player player = profile.getPlayer();

        if (player == null) return;

        if (this.locations.isEmpty()) {

            final CustomLocation cloned = profile.getMovementData().getLastLocation().clone();

            int count = 0;

            while (cloned.getBlock().getRelative(BlockFace.DOWN).isEmpty()) {

                cloned.subtract(0D, 1D, 0D);

                //Prevents crashes
                if (count++ > 5) break;
            }

            TaskUtils.task(() -> player.teleport(cloned.toBukkit(), PlayerTeleportEvent.TeleportCause.PLUGIN));

            return;
        }

        final Location setbackLocation = locations.getLast().toBukkit();

        if (setbackLocation.getWorld() != player.getWorld()) return;

        TaskUtils.task(() -> player.teleport(setbackLocation, PlayerTeleportEvent.TeleportCause.PLUGIN));
    }

    public Location getSetbackLocation() {
        return locations.isEmpty() ? null : locations.getLast().toBukkit();
    }
}
