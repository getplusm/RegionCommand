package t.me.p1azmer.plugin.regioncommand.api.events.region.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class EntityTakeDamageFromLavaInRegionEvent extends RegionEvents {
    public EntityTakeDamageFromLavaInRegionEvent(LivingEntity player, Region region) {
        super(player, region);
    }
}