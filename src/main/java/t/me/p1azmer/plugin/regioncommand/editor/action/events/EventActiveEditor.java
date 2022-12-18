package t.me.p1azmer.plugin.regioncommand.editor.action.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.menu.IMenuClick;
import t.me.p1azmer.aves.engine.api.menu.IMenuItem;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenu;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.Map;

public class EventActiveEditor extends AbstractEditorMenu<RegPlugin, EventAction> {
    public EventActiveEditor(@NotNull RegPlugin plugin, @NotNull EventAction object) {
        super(plugin, object, "Настройка ивент обработчика", 27);

        IMenuClick click = (player, type, event) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type2.equals(MenuItemType.RETURN)) {
                    object.getActiveRegion().getEventActiveListEditor().open(player, 1);
                } else {
                    this.onItemClickDefault(player, type2);
                }
            } else if (type instanceof EditorType type2) {
                if (type2.equals(EditorType.EVENTS_MANIPULATORS_OBJECT)) {
                    object.getActionListEditor().open(player, 1);
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {

        map.put(MenuItemType.RETURN, 22);

        map.put(EditorType.EVENTS_CHANGE_CANCELLED, 10);
        map.put(EditorType.EVENTS_CHANGE_LANGKEY, 12);
        map.put(EditorType.EVENTS_CHANGE_COOLDOWN, 14);
        map.put(EditorType.EVENTS_CHANGE_PERMISSION, 16);

        map.put(EditorType.EVENTS_MANIPULATORS_OBJECT, 4);
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
