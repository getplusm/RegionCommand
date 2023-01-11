package t.me.p1azmer.plugin.regioncommand.editor.action.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.editor.EditorInput;
import t.me.p1azmer.aves.engine.api.menu.MenuClick;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenuAuto;
import t.me.p1azmer.aves.engine.editor.EditorManager;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EventActiveListEditor extends AbstractEditorMenuAuto<RegPlugin, ActiveRegion, EventAction> {
    public EventActiveListEditor(@NotNull RegPlugin plugin, @NotNull ActiveRegion parent) {
        super(plugin, parent, "Список ивент обработчиков", 54);

        EditorInput<ActiveRegion, EditorType> eventCreate = (player, activeRegion, type, event) -> {
            String msg = event.getMessage();
            if (type.equals(EditorType.EVENTS_CREATE)) {
                Events events = CollectionsUtil.getEnum(msg, Events.class);
                if (events == null) {
                    EditorManager.error(player, "Ивент обработчик не найден!");
                    return false;
                }
                EditorManager.tip(player, "Ивент обработчик создан. Настрой его!");
                EventAction eventAction = new EventAction(activeRegion, events);
                activeRegion.getEventActions().add(eventAction);
                activeRegion.save();
                return true;
            }
            return false;
        };

        MenuClick click = (player, type, event) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type2.equals(MenuItemType.RETURN))
                    parent.getEditor().open(player, 1);
                else
                    this.onItemClickDefault(player, type2);
            } else if (type instanceof EditorType type2) {
                if (type2.equals(EditorType.EVENTS_CREATE)) {
                    EditorManager.startEdit(player, parent, type2, eventCreate);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_EVENTS.getDefaultText());
                    EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(Events.class), false);
                    player.closeInventory();
                }
            }
        };
        loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.RETURN, 49);
        map.put(EditorType.EVENTS_CREATE, 51);
    }

    @Override
    protected int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    protected @NotNull List<EventAction> getObjects(@NotNull Player player) {
        return new ArrayList<>(this.parent.getEventActions());
    }

    @Override
    protected @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull EventAction eventAction) {
        ItemStack item = EditorType.EVENTS_OBJECT.getItem();
        item.setType(eventAction.getEvents().getMaterial());
        ItemUtil.replace(item, eventAction.replacePlaceholders());
        return item;
    }

    @Override
    protected @NotNull MenuClick getObjectClick(@NotNull Player player, @NotNull EventAction eventAction) {
        return (player1, anEnum, inventoryClickEvent) -> {
            eventAction.getEditor().open(player, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
