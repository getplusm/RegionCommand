package t.me.p1azmer.plugin.regioncommand.api.events.region.block;

import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class PlayerBlockBreakInRegionEvent extends RegionEvents {

    private Block block;
    public PlayerBlockBreakInRegionEvent(LivingEntity player, Region region) {
        super(player, region);
    }
    public PlayerBlockBreakInRegionEvent(LivingEntity player, Region region, Block block) {
        super(player, region);
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }
}
