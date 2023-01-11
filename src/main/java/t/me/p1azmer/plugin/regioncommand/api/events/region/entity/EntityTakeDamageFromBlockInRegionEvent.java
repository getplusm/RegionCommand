package t.me.p1azmer.plugin.regioncommand.api.events.region.entity;

import org.bukkit.entity.LivingEntity;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class EntityTakeDamageFromBlockInRegionEvent extends RegionEvents {
    public EntityTakeDamageFromBlockInRegionEvent(LivingEntity player, Region region) {
        super(player, region);
    }
}
