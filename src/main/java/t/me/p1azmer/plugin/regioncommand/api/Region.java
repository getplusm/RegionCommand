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
import t.me.p1azmer.plugin.regioncommand.editor.EditorRegion;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.Cuboid;

import java.util.function.UnaryOperator;

public class Region extends AbstractConfigHolder<RegPlugin> implements ICleanable, IEditable, IPlaceholder {

    private Cuboid cuboid;
    private String name;
    private ActiveRegion activeRegion;
    private final RegionManager manager;

    private EditorRegion editorRegion;

    public Region(@NotNull RegionManager manager, @NotNull String id, Location location) {
        super(manager.plugin(), manager.plugin().getDataFolder() + "/regions/" + id + ".yml");
        this.manager = manager;
        this.setCuboid(new Cuboid(location, location)); // for one block action
        this.setName("&7" + id);
        this.setActiveRegion(new ActiveRegion(this));
    }

    public Region(@NotNull RegionManager manager, @NotNull JYML cfg) { // load from database
        super(manager.plugin(), cfg);
        this.manager = manager;

        this.name = cfg.getString("Name", "&7" + getId());

        Location first = cfg.getLocation("Cuboid.First");
        Location second = cfg.getLocation("Cuboid.Second");

        if (first == null) {
            throw new IllegalArgumentException("Invalid first location at cuboid region '" + getId() + "'");
        } else if (second == null) {
            throw new IllegalArgumentException("Invalid second location at cuboid region '" + getId() + "'");
        } else {

            this.setCuboid(new Cuboid(first, second)); // for one block action
            this.setActiveRegion(new ActiveRegion(this, cfg));
        }
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public void onSave() {
        cfg.set("Name", this.getName());
        cfg.set("Cuboid.First", this.getCuboid().getPoint1());
        cfg.set("Cuboid.Second", this.getCuboid().getPoint2());
        this.getActiveRegion().save();
    }

    @Override
    public void clear() {

    }

    public String getName() {
        return name;
    }

    public ActiveRegion getActiveRegion() {
        return activeRegion;
    }

    public Cuboid getCuboid() {
        return cuboid;
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

    public void setCuboid(Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    @Override
    public @NotNull EditorRegion getEditor() {
        if (this.editorRegion == null)
            this.editorRegion = new EditorRegion(this);
        return this.editorRegion;
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> getCuboid().replacePlaceholders().apply(s)
                .replace(Placeholders.PLACEHOLDER_REGION_NAME, getName())
                .replace(Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE, NumberUtil.format(this.getActiveRegion().getEventActions().size()))
                ;
    }
}
