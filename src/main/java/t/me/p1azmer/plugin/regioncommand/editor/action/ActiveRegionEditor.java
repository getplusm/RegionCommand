package t.me.p1azmer.plugin.regioncommand.editor.action;

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
import t.me.p1azmer.aves.engine.lang.CoreLang;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.data.Lang;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.Map;

public class ActiveRegionEditor extends AbstractEditorMenu<RegPlugin, ActiveRegion> {

    public ActiveRegionEditor(@NotNull RegPlugin plugin, @NotNull ActiveRegion object) {
        super(plugin, object, "Настройка активного региона", 9);

        EditorInput<ActiveRegion, EditorType> input = (player, region, type, event) -> {
            String msg = event.getMessage();
            if (type.equals(EditorType.ACTION_CHANGE_RADIUS)) {
                int value = StringUtil.getInteger(msg, -1);
                if (value < 0) {
                    EditorManager.error(player, CoreLang.EDITOR_ERROR_NUMBER_NOT_INT.getDefaultText());
                    return false;
                }
                region.setRadius(value);
            }
            region.save();
            return true;
        };

        IMenuClick click = (player, type, event) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type2.equals(MenuItemType.RETURN))
                    object.getRegion().getEditor().open(player, 1);
                else
                    this.onItemClickDefault(player, type2);
            } else if (type instanceof EditorType type2) {
                if (type2.equals(EditorType.ACTION_CHANGE_RADIUS)) {
                    EditorManager.startEdit(player, object, type2, input);
                    EditorManager.tip(player, Lang.EDITOR_REGION_CHANGE_WRITE_RADIUS.getDefaultText());
                    player.closeInventory();
                } else if (type2.equals(EditorType.REGION_ACTIVE_EVENTS_OBJECT)) {
                    this.object.getEventActiveListEditor().open(player, 1);
                }
            }
        };

        loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.RETURN, 8);
        map.put(EditorType.ACTION_CHANGE_RADIUS, 0);
        map.put(EditorType.REGION_ACTIVE_EVENTS_OBJECT, 2);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }


}
