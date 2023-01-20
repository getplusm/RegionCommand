package t.me.p1azmer.plugin.regioncommand.api;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.config.JYML;
import t.me.p1azmer.aves.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.aves.engine.api.manager.ICleanable;
import t.me.p1azmer.aves.engine.api.manager.IEditable;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.aves.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.type.RegionType;
import t.me.p1azmer.plugin.regioncommand.editor.EditorRegion;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

import java.util.function.UnaryOperator;

public class Region extends AbstractConfigHolder<RegPlugin> implements ICleanable, IEditable, IPlaceholder {

    private CuboidRegion territory;
    private RegionType regionType = RegionType.CUBOID;
    private String name;
    private ActiveRegion activeRegion;
    private final RegionManager manager;

    private EditorRegion editorRegion;

    public Region(@NotNull RegionManager manager, @NotNull String id, Location location) {
        super(manager.plugin(), manager.plugin().getDataFolder() + "/regions/" + id + ".yml");
        this.manager = manager;

//        switch (regionType){
//            case CUBOID -> this.setTerritory(new CuboidTerritory(id, BlockVector3.at(location.getX(), location.getY(), location.getZ()), BlockVector3.at(location.getX(), location.getY(), location.getZ())));
//            case POLYGON -> this.setTerritory(new PolygonalTerritory(id, BlockVector3.at(location.getX(), location.getY(), location.getZ()), BlockVector3.at(location.getX(), location.getY(), location.getZ())));
//        }
        this.setTerritory(CuboidRegion.empty()); // for one block action

        this.setName("&7" + id);
        this.setActiveRegion(new ActiveRegion(this));
    }

    public Region(@NotNull RegionManager manager, @NotNull JYML cfg) { // load from database
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.name = cfg.getString("Name", "&7" + getId());

        Location first = cfg.getLocation("Territory.Maximum");
        Location second = cfg.getLocation("Territory.Minimum");

        if (first == null) {
            throw new IllegalArgumentException("Invalid first location at cuboidRegion region '" + getId() + "'");
        } else if (second == null) {
            throw new IllegalArgumentException("Invalid second location at cuboidRegion region '" + getId() + "'");
        } else {

            this.setTerritory(new CuboidRegion(first, second)); // for one block action
            this.setActiveRegion(new ActiveRegion(this, cfg));
        }
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Territory.Type", this.getRegionType().getName());
        cfg.set("Territory.Maximum", this.getTerritory().getLocationMax());//this.getTerritory().getMaximumPoint().toParserString());
        cfg.set("Territory.Minimum", this.getTerritory().getLocationMin());//this.getTerritory().getMinimumPoint().toParserString());
        cfg.saveChanges();
        this.getActiveRegion().save();
    }

    @Override
    public void clear() {
        if (this.editorRegion != null) {
            this.editorRegion.clear();
            this.editorRegion = null;
        }
        this.activeRegion.clear();
    }

    public String getName() {
        return name;
    }

    public RegionType getRegionType() {
        return regionType;
    }

    public ActiveRegion getActiveRegion() {
        return activeRegion;
    }

    public CuboidRegion getTerritory() {
        return territory;
    }

    public RegionManager getManager() {
        return manager;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActiveRegion(ActiveRegion activeRegion) {
        this.activeRegion = activeRegion;
    }

    public void setTerritory(CuboidRegion territory) {
        this.territory = territory;
    }

    @Override
    public @NotNull EditorRegion getEditor() {
        if (this.editorRegion == null)
            this.editorRegion = new EditorRegion(this);
        return this.editorRegion;
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_REGION_NAME, getName())
                .replace(Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE, NumberUtil.format(this.getActiveRegion().getEventActions().size()))
                ;
    }
}
