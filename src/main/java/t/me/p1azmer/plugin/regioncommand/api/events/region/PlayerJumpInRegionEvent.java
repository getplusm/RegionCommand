package t.me.p1azmer.plugin.regioncommand.api.events.region;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;

public class PlayerJumpInRegionEvent extends RegionEvents{
    public PlayerJumpInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
