package t.me.p1azmer.plugin.regioncommand.editor.action.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.editor.EditorInput;
import t.me.p1azmer.aves.engine.api.lang.LangMessage;
import t.me.p1azmer.aves.engine.api.menu.MenuClick;
import t.me.p1azmer.aves.engine.api.menu.MenuItem;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenu;
import t.me.p1azmer.aves.engine.editor.EditorManager;
import t.me.p1azmer.aves.engine.lang.CoreLang;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.Map;

public class EventActiveEditor extends AbstractEditorMenu<RegPlugin, EventAction> {
    public EventActiveEditor(@NotNull RegPlugin plugin, @NotNull EventAction object) {
        super(plugin, object, "Настройка ивент обработчика", 27);

        EditorInput<EventAction, EditorType> input = (player, eventAction, type, event) -> {
            String msg = event.getMessage();
            if (type.equals(EditorType.EVENTS_CHANGE_LANGKEY)) {
                eventAction.setLangMessage(new LangMessage(plugin, msg));
            } else if (type.equals(EditorType.EVENTS_CHANGE_COOLDOWN)) {
                int value = StringUtil.getInteger(msg, -1);
                if (value < -1) {
                    EditorManager.error(player, CoreLang.EDITOR_ERROR_NUMBER_NOT_INT.getDefaultText());
                    return false;
                }
                eventAction.setCooldown(value);
            } else if (type.equals(EditorType.EVENTS_CHANGE_PERMISSION)) {
                eventAction.setPermission(new JPermission(msg));
            }
            eventAction.getActiveRegion().save();
            return true;
        };

        MenuClick click = (player, type, event) -> {
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
                    return;
                }
                if (type2.equals(EditorType.EVENTS_CHANGE_CANCELLED)) {
                    object.setCancelled(!object.isCancelled());
                    this.open(player, getPage(player));
                    object.getActiveRegion().save();
                } else if (type2.equals(EditorType.EVENTS_CHANGE_LANGKEY)) {
                    EditorManager.startEdit(player, object, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_WRITE_RAW_MESSAGE).getLocalized());
                    player.closeInventory();
                } else if (type2.equals(EditorType.EVENTS_CHANGE_COOLDOWN)) {
                    EditorManager.startEdit(player, object, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_WRITE_INTEGER).getLocalized());
                    player.closeInventory();
                } else if (type2.equals(EditorType.EVENTS_CHANGE_PERMISSION)) {
                    EditorManager.startEdit(player, object, type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_WRITE_PERMISSION).getLocalized());
                    player.closeInventory();
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
    public void onItemPrepare(@NotNull Player player, @NotNull MenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
        ItemUtil.replace(item, this.object.getActiveRegion().replacePlaceholders());
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
