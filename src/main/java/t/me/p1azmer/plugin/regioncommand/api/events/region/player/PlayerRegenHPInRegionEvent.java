package t.me.p1azmer.plugin.regioncommand.api.events.region.player;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerRegenHPInRegionEvent extends RegionEvents {
    public PlayerRegenHPInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
