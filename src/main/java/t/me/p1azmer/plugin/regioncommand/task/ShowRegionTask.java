package t.me.p1azmer.plugin.regioncommand.task;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.server.AbstractTask;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

import java.util.Map;

public class ShowRegionTask extends AbstractTask<RegPlugin> {
    public ShowRegionTask(@NotNull RegPlugin plugin) {
        super(plugin, 3, true);
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
            cuboidRegion.getVisualizer().draw(player);

        }
    }
}
