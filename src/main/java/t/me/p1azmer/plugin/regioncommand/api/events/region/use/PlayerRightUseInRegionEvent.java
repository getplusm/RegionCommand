package t.me.p1azmer.plugin.regioncommand.api.events.region.use;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerRightUseInRegionEvent extends RegionEvents {
    public PlayerRightUseInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
