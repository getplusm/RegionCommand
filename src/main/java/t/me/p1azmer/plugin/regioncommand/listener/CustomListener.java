package t.me.p1azmer.plugin.regioncommand.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.returns.TripleReturn;
import t.me.p1azmer.aves.engine.api.manager.AbstractListener;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockBreakInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockPlaceInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.RestoreBlockEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerDamageAggressiveInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerDamagePlayerInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.player.PlayerCommandInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomListener extends AbstractListener<RegPlugin> {

    private final RegionManager manager;
    private final List<TripleReturn<BlockState, Location, Long>> blockRestores;

    public Collection<TripleReturn<BlockState, Location, Long>> getBlockRestores() {
        return blockRestores;
    }

    public TripleReturn<BlockState, Location, Long> getBlockRestores(Location location) {
        return blockRestores.stream().filter(f -> f.second().equals(location)).findFirst().orElse(null);
    }

    public CustomListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
        this.blockRestores = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreakInRegion(PlayerBlockBreakInRegionEvent event) {
        if (event.getBlock() != null) {
            Block block = event.getBlock();
            Region region = event.getRegion();
            if (Events.getMaterialsToRestore().containsKey(region)) {
                TripleReturn<List<Material>, Material, Long> pair = Events.getMaterialsToRestore().get(region);
                if (pair.first().contains(block.getType())) {
                    RestoreBlockEvent calledEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new RestoreBlockEvent(event, event.getEntity(), region, block.getState(), pair.second(), pair.third()));
                    if (calledEvent.isCancelled()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(PlayerBlockPlaceInRegionEvent event) {
        if (event.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandInRegionEvent event) {
        if (event.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamageEntity(PlayerDamageAggressiveInRegionEvent event) {
        if (event.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamagePlayer(PlayerDamagePlayerInRegionEvent event) {
        if (event.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRestoreBlock(RestoreBlockEvent event) {
        Location location = event.getBlockState().getLocation();

        TripleReturn<BlockState, Location, Long> pair = new TripleReturn<>(event.getBlockState(), location, event.getRespawnTime());

        blockRestores.add(pair);

        location.getBlock().breakNaturally();

        location.getBlock().setType(event.getPlacedMaterial());
    }
}
