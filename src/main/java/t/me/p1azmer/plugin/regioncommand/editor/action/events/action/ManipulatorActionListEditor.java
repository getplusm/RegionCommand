package t.me.p1azmer.plugin.regioncommand.editor.action.events.action;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.ActionSection;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.menu.MenuClick;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenuAuto;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class ManipulatorActionListEditor extends AbstractEditorMenuAuto<RegPlugin, EventAction, ActionSection> {
    public ManipulatorActionListEditor(@NotNull RegPlugin plugin, @NotNull EventAction parent) {
        super(plugin, parent, "Список действий обработчика", 54);


        MenuClick click = (player, type, inventoryClickEvent) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type2.equals(MenuItemType.RETURN)) parent.getEditor().open(player, 1);
                else this.onItemClickDefault(player, type2);
            } else if (type instanceof EditorType type2) {
                if (type2.equals(EditorType.MANIPULATOR_CREATE_ACTION)) {
                    this.parent.getActionSelector(new ActionSection("default", new ArrayList<>(), new ArrayList<>(), "", new ArrayList<>())).open(player, 1);
                }
            }
        };
        loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(MenuItemType.RETURN, 49);
        map.put(EditorType.MANIPULATOR_CREATE_ACTION, 51);
    }

    @Override
    protected int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    protected @NotNull List<ActionSection> getObjects(@NotNull Player player) {
        if (this.parent.getManipulator() == null) return new ArrayList<>();
        else return new ArrayList<>(this.parent.getManipulator().getActions().values());
    }

    @Override
    protected @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull ActionSection section) {
        ItemStack item = EditorType.EVENTS_MANIPULATOR_ACTION_OBJECT.getItem();
        String actionId = section.getId();
        ItemUtil.replace(item, s -> s.replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTIN_ID, actionId == null ? "Нет" : actionId)
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE, String.valueOf(section.getConditions().size()))
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE, String.valueOf(section.getActionExecutors().size()))
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID, section.getConditionFailActions()));

        return item;
    }

    @Override
    protected @NotNull MenuClick getObjectClick(@NotNull Player player, @NotNull ActionSection section) {
        return (player1, type, click) -> this.parent.getActionSelector(section).open(player, 1);
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
