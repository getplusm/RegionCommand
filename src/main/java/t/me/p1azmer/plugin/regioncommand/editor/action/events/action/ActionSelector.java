package t.me.p1azmer.plugin.regioncommand.editor.action.events.action;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.ActionSection;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.menu.IMenuClick;
import t.me.p1azmer.aves.engine.api.menu.IMenuItem;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenu;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.Map;
import java.util.stream.Collectors;

public class ActionSelector extends AbstractEditorMenu<RegPlugin, EventAction> {

    private ActionSection section;

    public ActionSelector(@NotNull RegPlugin plugin, EventAction eventAction, ActionSection section) {
        super(plugin, eventAction, "Выбор активного действия", 9);
        this.section = section;

        IMenuClick click = (player, type, inventoryClickEvent) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type2.equals(MenuItemType.RETURN)) eventAction.getActionListEditor().open(player, 1);
                else this.onItemClickDefault(player, type2);
            } else if (type instanceof EditorType type2) {
                ManipulatorActionEditor editor = null;
                if (type2.equals(EditorType.ACTION_CONDITIONS_LIST_OBJECT)) {
                    editor = new ManipulatorActionEditor(plugin, eventAction, ActionSelectType.CONDITIONS, section);
                } else if (type2.equals(EditorType.ACTION_EXECUTORS_OBJECT)) {
                    editor = new ManipulatorActionEditor(plugin, eventAction, ActionSelectType.EXECUTORS, section);

                } else if (type2.equals(EditorType.ACTION_FAIL_ACTIONS_OBJECT)) {
                    editor = new ManipulatorActionEditor(plugin, eventAction, ActionSelectType.FAIL, section);
                }
                if (editor != null) editor.open(player, 1);
            }
        };

        this.loadItems(click);
    }

    @Override
    public void onItemPrepare(@NotNull Player player, @NotNull IMenuItem menuItem, @NotNull ItemStack item) {
        super.onItemPrepare(player, menuItem, item);
        ItemUtil.replace(item, this.object.replacePlaceholders());
        ItemUtil.replace(item, this.object.getActiveRegion().replacePlaceholders());
        ItemUtil.replace(item, s -> s
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID, this.section.getConditionFailActions())
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE, String.join("\n", this.section.getConditions()))
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE, String.join("\n", this.section.getActionExecutors()))
        );
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(EditorType.ACTION_CONDITIONS_LIST_OBJECT, 2);
        map.put(EditorType.ACTION_EXECUTORS_OBJECT, 4);
        map.put(EditorType.ACTION_FAIL_ACTIONS_OBJECT, 6);

        map.put(MenuItemType.RETURN, 8);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }

    public enum ActionSelectType {
        CONDITIONS, EXECUTORS, FAIL;
    }
}
