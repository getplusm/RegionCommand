package t.me.p1azmer.plugin.regioncommand.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.config.JYML;
import t.me.p1azmer.aves.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.listener.PlayerListener;

import java.util.HashMap;
import java.util.Map;

public class RegionManager extends AbstractManager<RegPlugin> {

    public Map<String, Region> regions;

    private PlayerListener playerListener;

    public RegionManager(@NotNull RegPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.playerListener = new PlayerListener(this);
        this.playerListener.registerListeners();

        this.regions = new HashMap<>();

        plugin.getScheduler().runTask(plugin, this::loadRegions);
    }

    private void loadRegions() {
        for (JYML cfg : JYML.loadAll(plugin.getDataFolder() + "/regions/", true)) {
            try {
                Region region = new Region(this, cfg);
                this.regions.put(region.getId(), region);
            } catch (Exception var4) {
                this.plugin.error("Unable to load region " + cfg.getFile().getName());
                var4.printStackTrace();
            }
        }
        plugin.info("Loaded " + this.regions.size() + " regions!");
    }

    @Override
    protected void onShutdown() {
        if (this.regions != null) {
            this.regions.values().forEach(Region::clear);
            this.regions.clear();
            this.regions = null;
        }
        if (this.playerListener != null) {
            this.playerListener.unregisterListeners();
            this.playerListener = null;
        }
    }

    public void removeRegion(Region region) {
        if (region.getFile().delete()) {
            region.clear();
            this.regions.remove(region.getId());
        }
    }

    public Region getRegion(String id) {
        return this.regions.get(id);
    }

    public Region getRegion(Location location) {
        if (location == null) return null;
        return this.regions.values().stream().filter(f -> {
            if (f.getActiveRegion().getRadius() > 0)
                return f.getCuboid().isInWithMarge(location, f.getActiveRegion().getRadius());
            return f.getCuboid().isIn(location);
        }).findFirst().orElse(null);
    }

    public Region getRegion(Location location, double marge) {
        if (location == null || marge < 0) return null;
        return this.regions.values().stream().filter(f -> f.getCuboid().isInWithMarge(location, marge)).findFirst().orElse(null);
    }

    public Region getRegion(Player player) {
        return this.regions.values().stream().filter(f -> {
            if (f.getActiveRegion().getRadius() > 0)
                return f.getCuboid().isInWithMarge(player.getLocation(), f.getActiveRegion().getRadius());
            return f.getCuboid().isIn(player.getLocation());
        }).findFirst().orElse(null);
    }

    public boolean inRegion(Location location) {
        return getRegion(location) != null;
    }

    public boolean inRegion(Player player) {
        return getRegion(player) != null;
    }
}
