package t.me.p1azmer.plugin.regioncommand.task;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.PlazmerCore;
import t.me.p1azmer.api.returns.TripleReturn;
import t.me.p1azmer.api.util.LocationUtil;
import t.me.p1azmer.aves.engine.api.task.AbstractTask;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RestoreBlockTask extends AbstractTask<RegPlugin> {

    private final RegionManager manager;
    private final Map<Location, AtomicInteger> cache;

    public RestoreBlockTask(@NotNull RegionManager regionManager) {
        super(regionManager.plugin(), 1, false);
        this.manager = regionManager;
        this.cache = new HashMap<>();
    }

    @Override
    public void stop() {
        if (this.manager.getCustomListener().getBlockRestores() != null)
            this.manager.getCustomListener().getBlockRestores().forEach(pair -> {
                pair.second().getBlock().setType(pair.first().getType());
                pair.second().getBlock().setBlockData(pair.first().getBlockData());
            });
        super.stop();
    }

    @Override
    public void action() {
        for (TripleReturn<BlockState, Location, Long> pair : new ArrayList<>(this.manager.getCustomListener().getBlockRestores())) {
            BlockState block = pair.first();
            Location location = pair.second();
            long respawnTime = pair.third();

            if (!cache.containsKey(location))
                cache.put(location, new AtomicInteger(0));

            AtomicInteger time = this.cache.getOrDefault(location, new AtomicInteger(0));

            PlazmerCore.getParticle().sendPacket(location, 5,
                    PlazmerCore.getParticle().DUST_COLOR_TRANSITION()
                            .color(Color.RED, Color.BLUE, 1)
                            .packet(true, location.clone().add(0.5, 0.5, 0.5))
            );

            if (time.incrementAndGet() >= respawnTime) {

                location.getBlock().setType(block.getType());
                location.getBlock().setBlockData(block.getBlockData());
//                    location.getBlock().setBlockData(block.getBlockData());
                LocationUtil.getNearbyEntities(location, LivingEntity.class, 1)
                        .stream().filter(f -> f.getLocation().equals(location))
                        .forEach(player -> {
                            try {
                                Location safe = LocationUtil.getSafeDestination(location);
                                player.teleport(safe);
                            } catch (Exception ignore) {
                                Location safe = t.me.p1azmer.aves.engine.utils.LocationUtil.getFirstGroundBlock(location);
                                player.teleport(safe);
                            }
                        });
                this.manager.getCustomListener().getBlockRestores().remove(pair);
                this.cache.remove(location);
            }
        }
    }
}
