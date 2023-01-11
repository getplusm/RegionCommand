package t.me.p1azmer.plugin.regioncommand.task;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.PlazmerCore;
import t.me.p1azmer.aves.engine.api.task.AbstractTask;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

import java.util.Map;

public class ShowRegionTask extends AbstractTask<RegPlugin> {
    public ShowRegionTask(@NotNull RegPlugin plugin) {
        super(plugin, 1, true);
    }

    @Override
    public void action() {
        for (Map.Entry<Player, Region> entry : plugin.getManager().getRegionShown().entrySet()) {
            Player player = entry.getKey();
            Region region = entry.getValue();
            if (player == null || !player.isOnline()) {
                plugin.getManager().getRegionShown().remove(player);
                return;
            }
            if (region == null) {
                plugin.getManager().getRegionShown().remove(player);
                return;
            }

            CuboidRegion cuboidRegion = region.getTerritory();
            player.sendMessage("POint size: " + cuboidRegion.getParticlePoints().size());
            for (CuboidRegion.ParticlePoint point : cuboidRegion.getParticlePoints()) {
                player.sendMessage("Vector size: " + cuboidRegion.traverse(point.getOrigin(), point.getDirection()).size());
                for (Vector vector : cuboidRegion.traverse(point.getOrigin(), point.getDirection())) {
                    Location location = vector.toLocation(player.getWorld());
                    PlazmerCore.getParticle().sendPacket(player.getLocation(), 30,
                            PlazmerCore.getParticle().DUST()
                                    .color(Color.RED, 2)
                                    .packet(true, location, 1)
                            //, player1 -> LocationUtil.distance(player1.getLocation(), location) <= 100);
                    );
                }
            }
            PlazmerCore.getParticle().sendPacket(player.getLocation(), 30,
                    PlazmerCore.getParticle().DUST_COLOR_TRANSITION()
                            .color(Color.BLUE, Color.BLACK, 1)
                            .packet(true, cuboidRegion.getCenter(), 1)
                    //, player1 -> LocationUtil.distance(player1.getLocation(), location) <= 100);
            );

        }
    }
}
