package t.me.p1azmer.plugin.regioncommand.api.events.region.movement;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerSprintInRegionEvent extends RegionEvents {
    public PlayerSprintInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
