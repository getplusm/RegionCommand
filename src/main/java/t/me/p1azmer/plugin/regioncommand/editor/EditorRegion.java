package t.me.p1azmer.plugin.regioncommand.editor;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.editor.EditorInput;
import t.me.p1azmer.aves.engine.api.menu.IMenuClick;
import t.me.p1azmer.aves.engine.api.menu.IMenuItem;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenu;
import t.me.p1azmer.aves.engine.editor.EditorManager;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.data.Lang;

import java.util.Map;

public class EditorRegion extends AbstractEditorMenu<RegPlugin, Region> {
    public EditorRegion(@NotNull Region region) {
        super(region.getManager().plugin(), region, "Настройка региона " + region.getId(), 9);
        EditorInput<Region, EditorType> input = (player, reg, type, e) -> {
            String msg = StringUtil.color(e.getMessage());
            switch (type) {
                case REGION_CHANGE_NAME -> region.setName(msg);
                case REGION_CHANGE_CUBOID_FIRST -> {
                    if (msg.equalsIgnoreCase("now")) {
                        reg.getCuboid().setFirst(player.getLocation());
                        plugin.getMessage(Lang.EDITOR_REGION_CHANGE_CUBOID_FIRST).send(player);
                    }
                }
                case REGION_CHANGE_CUBOID_SECOND -> {
                    if (msg.equalsIgnoreCase("now")) {
                        reg.getCuboid().setSecond(player.getLocation());
                        plugin.getMessage(Lang.EDITOR_REGION_CHANGE_CUBOID_SECOND).send(player);
                    }
                }
            }
            reg.save();
            return true;
        };

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType) {
                if (type == MenuItemType.RETURN) {
                    plugin.getEditor().open(player, 1);
                }
            } else if (type instanceof EditorType type2) {
                switch (type2) {
                    case REGION_ACTIVE_OBJECT -> {
                        this.object.getActiveRegion().getEditor().open(player, 1);
                    }
                    case REGION_CHANGE_CUBOID_FIRST, REGION_CHANGE_CUBOID_SECOND -> {
                        EditorManager.startEdit(player, region, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_REGION_CHANGE_CHANGE_LOCATION).getLocalized());
                        player.closeInventory();
                    }
                    case REGION_CHANGE_NAME -> {
                        EditorManager.startEdit(player, region, type2, input);
                        EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_REGION_CHANGE_WRITE_NAME).getLocalized());
                        player.closeInventory();
                    }
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(EditorType.REGION_CHANGE_CUBOID_FIRST, 0);
        map.put(EditorType.REGION_CHANGE_CUBOID_SECOND, 1);
        map.put(EditorType.REGION_CHANGE_NAME, 3);
        map.put(EditorType.REGION_ACTIVE_OBJECT, 6);
        map.put(MenuItemType.RETURN, 8);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
        ItemUtil.replace(item, this.object.getActiveRegion().replacePlaceholders());
    }
}
