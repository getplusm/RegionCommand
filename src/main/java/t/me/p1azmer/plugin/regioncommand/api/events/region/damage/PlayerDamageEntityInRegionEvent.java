package t.me.p1azmer.plugin.regioncommand.api.events.region.damage;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerDamageEntityInRegionEvent extends RegionEvents {
    public PlayerDamageEntityInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
