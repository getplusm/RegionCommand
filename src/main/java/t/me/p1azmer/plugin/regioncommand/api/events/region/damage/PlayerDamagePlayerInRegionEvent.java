package t.me.p1azmer.plugin.regioncommand.api.events.region.damage;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerDamagePlayerInRegionEvent extends RegionEvents {
    public PlayerDamagePlayerInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
