package t.me.p1azmer.plugin.regioncommand.api.events.region.movement;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerJumpInRegionEvent extends RegionEvents {
    public PlayerJumpInRegionEvent(LivingEntity player, Region region) {
        super(player, region);
    }
}
