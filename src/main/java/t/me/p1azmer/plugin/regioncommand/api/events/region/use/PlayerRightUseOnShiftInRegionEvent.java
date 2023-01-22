package t.me.p1azmer.plugin.regioncommand.api.events.region.use;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerRightUseOnShiftInRegionEvent extends RegionEvents {
    public PlayerRightUseOnShiftInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
