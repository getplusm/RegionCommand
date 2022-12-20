package t.me.p1azmer.plugin.regioncommand.api.events.region;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;

public class PlayerShiftUPInRegionEvent extends RegionEvents{
    public PlayerShiftUPInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
