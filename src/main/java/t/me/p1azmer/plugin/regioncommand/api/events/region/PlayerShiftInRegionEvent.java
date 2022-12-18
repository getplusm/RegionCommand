package t.me.p1azmer.plugin.regioncommand.api.events.region;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;

public class PlayerShiftInRegionEvent extends RegionEvents{
    public PlayerShiftInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
