package t.me.p1azmer.plugin.regioncommand.api.events.region.use;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerDropItemInRegionEvent extends RegionEvents {
    public PlayerDropItemInRegionEvent(LivingEntity player, Region region) {
        super(player, region);
    }
}
