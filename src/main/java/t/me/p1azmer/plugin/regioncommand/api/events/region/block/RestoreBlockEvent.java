package t.me.p1azmer.plugin.regioncommand.api.events.region.block;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;

public class RestoreBlockEvent extends RegionEvents {

    private final BlockState block;
    private Material placedMaterial;
    private final PlayerBlockBreakInRegionEvent originalEvent;

    private final long respawnTime;

    public RestoreBlockEvent(@NotNull PlayerBlockBreakInRegionEvent originalEvent, @NotNull LivingEntity player, @NotNull Region region, @NotNull BlockState block) {
        super(player, region);
        this.block = block;
        this.originalEvent = originalEvent;
        this.respawnTime = 20*5;
    }

    public RestoreBlockEvent(@NotNull PlayerBlockBreakInRegionEvent originalEvent, @NotNull LivingEntity player,
                             @NotNull Region region, @NotNull BlockState block, @NotNull Material placedMaterial, long respawnTime) {
        super(player, region);
        this.block = block;
        this.originalEvent = originalEvent;
        this.placedMaterial = placedMaterial;
        this.respawnTime = respawnTime;
    }

    public BlockState getBlockState() {
        return block;
    }

    public PlayerBlockBreakInRegionEvent getOriginalEvent() {
        return originalEvent;
    }

    public Material getPlacedMaterial() {
        return placedMaterial;
    }

    public long getRespawnTime() {
        return respawnTime;
    }
}
