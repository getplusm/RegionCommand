package t.me.p1azmer.plugin.regioncommand.api.events.region;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;

public class PlayerDamagePlayerInRegionEvent extends RegionEvents{
    public PlayerDamagePlayerInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
