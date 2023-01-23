package t.me.p1azmer.plugin.regioncommand.manager;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.config.JYML;
import t.me.p1azmer.aves.engine.api.manager.AbstractManager;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.EventHandlerListMenu;
import t.me.p1azmer.plugin.regioncommand.listener.*;
import t.me.p1azmer.plugin.regioncommand.task.RestoreBlockTask;
import t.me.p1azmer.plugin.regioncommand.task.ShowRegionTask;

import java.util.HashMap;
import java.util.Map;

public class RegionManager extends AbstractManager<RegPlugin> {

    public Map<Player, Region> regionShown;

    public Map<String, Region> regions;

    private PlayerListener playerListener;
    private DetectListener detectListener;

    private PhysicalListener physicalListener;

    private CustomListener customListener;
    private EntityListener entityListener;
    private ShowRegionTask showRegionTask;

    private RestoreBlockTask restoreBlockTask;
    public RegionManager(@NotNull RegPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.detectListener = new DetectListener(this);
        this.detectListener.registerListeners();

        this.playerListener = new PlayerListener(this);
        this.playerListener.registerListeners();

        this.physicalListener = new PhysicalListener(this);
        this.physicalListener.registerListeners();

        this.entityListener = new EntityListener(this);
        this.entityListener.registerListeners();

        this.customListener = new CustomListener(this);
        this.customListener.registerListeners();

        this.regions = new HashMap<>();
        this.regionShown = new HashMap<>();

        this.showRegionTask = new ShowRegionTask(this.plugin);
        this.showRegionTask.start();

        if (this.plugin.getActionsManager().getActionExecutor("REGION_RESTORE_BLOCK") != null) {
            this.restoreBlockTask = new RestoreBlockTask(this);
            this.restoreBlockTask.start();
        }

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
            this.regions.values().forEach(rg->{
                rg.save();
                rg.clear();
            });
            this.regions.clear();
            this.regions = null;
        }
        if (this.playerListener != null) {
            this.playerListener.unregisterListeners();
            this.playerListener = null;
        }
        if (this.detectListener != null) {
            this.detectListener.unregisterListeners();
            this.detectListener = null;
        }
        if (this.physicalListener != null){
            this.physicalListener.unregisterListeners();
            this.physicalListener = null;
        }
        if (this.entityListener != null){
            this.entityListener.unregisterListeners();
            this.entityListener = null;
        }
        if (this.regionShown != null){
            this.regionShown.clear();
            this.regionShown = null;
        }
        if (this.showRegionTask != null){
            this.showRegionTask.stop();
            this.showRegionTask = null;
        }
        if (this.restoreBlockTask != null){
            this.restoreBlockTask.stop();
            this.restoreBlockTask = null;
        }
        if (this.customListener != null){
            this.customListener.unregisterListeners();
            this.customListener = null;
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
        return this.regions
                .values()
                .stream()
                .filter(f -> {
            if (f.getActiveRegion().getRadius() > 0)
                return f.getTerritory().isInWithMarge(location, f.getActiveRegion().getRadius());
            return f.getTerritory().isIn(location);
        })
                .findFirst()
                .orElse(null);
    }

    public Region getRegion(Location location, double marge) {
        if (location == null || marge < 0) return null;
        return this.regions
                .values()
                .stream()
                .filter(f ->
                        this.getRegion(location) != null
                                || f.getTerritory().isInWithMarge(location, marge))
                .findFirst()
                .orElse(null);
    }

    public Region getRegion(Player player) {
        return getRegion(player.getLocation());
    }

    public Region getRegion(LivingEntity entity){
        return this.getRegion(entity.getLocation());
    }

    public Region getRegion(Player player, double merge) {
        return getRegion(player.getLocation(), merge);
    }

    public Region getRegion(LivingEntity entity, double merge){
        return this.getRegion(entity.getLocation(), merge);
    }

    public boolean inRegion(Location location) {
        return getRegion(location) != null;
    }

    public boolean inRegion(Player player) {
        return getRegion(player) != null;
    }

    public boolean inRegion(LivingEntity entity){
        return inRegion(entity.getLocation());
    }

    public Map<Player, Region> getRegionShown() {
        return regionShown;
    }

//    /**
//     * Create a {@link ProtectedRegion} from the actor's selection.
//     *
//     * @param actor the actor
//     * @param id the ID of the new region
//     * @return a new region
//     * @throws CommandException thrown on an error
//     */
//    protected static RegionTerritory checkRegionFromSelection(Actor actor, String id) {
//        Territory selection = checkSelection(actor);
//
//        // Detect the type of region from WorldEdit
//        if (selection instanceof Polygonal2DRegion) {
//            Polygonal2DRegion polySel = (Polygonal2DRegion) selection;
//            int minY = polySel.getMinimumPoint().getBlockY();
//            int maxY = polySel.getMaximumPoint().getBlockY();
//            return new ProtectedPolygonalRegion(id, polySel.getPoints(), minY, maxY);
//        } else if (selection instanceof CuboidRegion) {
//            BlockVector3 min = selection.getMinimumPoint();
//            BlockVector3 max = selection.getMaximumPoint();
//            return new ProtectedCuboidRegion(id, min, max);
//        } else {
//            throw new CommandException("Sorry, you can only use cuboids and polygons for WorldGuard regions.");
//        }
//    }
//    /**
//     * Get a WorldEdit selection for an actor, or emit an exception if there is none
//     * available.
//     *
//     * @param actor the actor
//     * @return the selection
//     * @throws CommandException thrown on an error
//     */
//    protected static Territory checkSelection(Actor actor) throws CommandException {
//        LocalSession localSession = WorldEdit.getInstance().getSessionManager().get(actor);
//        try {
//            if (localSession == null || localSession.getSelectionWorld() == null) {
//                throw new IncompleteRegionException();
//            }
//            return localSession.getRegionSelector(localSession.getSelectionWorld()).getRegion();
//        } catch (IncompleteRegionException e) {
//            throw new CommandException("Please select an area first. " +
//                    "Use WorldEdit to make a selection! " +
//                    "(see: https://worldedit.enginehub.org/en/latest/usage/regions/selections/).");
//        }
//    }


    public CustomListener getCustomListener() {
        return customListener;
    }

}
