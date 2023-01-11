package t.me.p1azmer.plugin.regioncommand.api.events.region.movement;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerMoveInRegionEvent extends RegionEvents {
    public PlayerMoveInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
