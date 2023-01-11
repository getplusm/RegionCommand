package t.me.p1azmer.plugin.regioncommand.api.events.region.player;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerRespawnInRegionEvent extends RegionEvents {
    public PlayerRespawnInRegionEvent(Player player, Region region) {
        super(player, region);
    }
}
