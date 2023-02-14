package t.me.p1azmer.plugin.regioncommand.editor.action.events.action;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.ActionManipulator;
import t.me.p1azmer.aves.engine.actions.ActionSection;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.editor.EditorInput;
import t.me.p1azmer.aves.engine.api.menu.MenuClick;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenuAuto;
import t.me.p1azmer.aves.engine.editor.EditorManager;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

public class ManipulatorActionEditor extends AbstractEditorMenuAuto<RegPlugin, EventAction, String> {

    private final ActionSelector.ActionSelectType selectType;
    private final EditorInput<EventAction, EditorType> actionManipulatorInput;
    private final ActionSection actionSection;

    public ManipulatorActionEditor(@NotNull RegPlugin plugin, @NotNull EventAction parent, ActionSelector.ActionSelectType selectType, ActionSection actionSection) {
        super(plugin, parent, "Настройка активного действия", 54);
        this.selectType = selectType;
        this.actionSection = actionSection;

        this.actionManipulatorInput = (player, eventAction, editorType, event) -> {
            String msg = event.getMessage();
            ActionManipulator manipulator = eventAction.getManipulator();
            ActionSection section = manipulator.getActions().values().stream().filter(Objects::nonNull).findFirst().orElse(null);
            if (section == null) {
                EditorManager.error(player, Lang.EDITOR_ERROR_ACTION_SECTION_NOT_FOUND.getDefaultText());
                return false;
            }
            if (editorType.equals(EditorType.ACTION_SELECT_CONDITIONS)) {
                section.getConditions().add(msg);
            }
            if (editorType.equals(EditorType.ACTION_SELECT_EXECUTORS)) {
                section.getActionExecutors().add(msg);
            }
            if (editorType.equals(EditorType.ACTION_SELECT_FAIL)) {
                section.setConditionActionOnFail(msg);
            }
            eventAction.setManipulator(manipulator);
            parent.getActiveRegion().save();
            return true;
        };

        EditorInput<EventAction, EditorType> actionInput = (player, eventAction, type, event) -> {
            String msg = event.getMessage();
            EventHandler eventHandler = eventAction.getEventHandler();
            ActiveRegion activeRegion = eventAction.getActiveRegion();
            if (type.equals(EditorType.ACTION_CREATE_CONDITION)) {
                ActionManipulator manipulator = new ActionManipulator(plugin);
                if (activeRegion.getEventActionByEvent(eventHandler) != null && activeRegion.getEventActionByEvent(eventHandler).getManipulator() != null) {
                    manipulator = activeRegion.getEventActionByEvent(eventHandler).getManipulator();
                }
                manipulator.getActions().put("default", new ActionSection("default", List.of(msg), new ArrayList<>(), "", new ArrayList<>()));
            } else if (type.equals(EditorType.ACTION_CREATE_EXECUTOR)) {
                ActionManipulator manipulator = new ActionManipulator(plugin);
                if (activeRegion.getEventActionByEvent(eventHandler) != null && activeRegion.getEventActionByEvent(eventHandler).getManipulator() != null) {
                    manipulator = activeRegion.getEventActionByEvent(eventHandler).getManipulator();
                }
                manipulator.getActions().put("default", new ActionSection("default", new ArrayList<>(), List.of(msg), "", new ArrayList<>()));
            } else if (type.equals(EditorType.ACTION_CREATE_FAIL)) {
                ActionManipulator manipulator = new ActionManipulator(plugin);
                if (activeRegion.getEventActionByEvent(eventHandler) != null && activeRegion.getEventActionByEvent(eventHandler).getManipulator() != null) {
                    manipulator = activeRegion.getEventActionByEvent(eventHandler).getManipulator();
                }
                manipulator.getActions().put("default", new ActionSection("default", new ArrayList<>(), new ArrayList<>(), msg, new ArrayList<>()));
            }
            eventAction.getActiveRegion().save();
            return true;
        };

//        EditorInput<EventAction, EditorType> eventsInput = (player, eventAction, type, event) -> {
//            String msg = event.getMessage();
//            if (type.equals(EditorType.ACTION_CREATE_CONDITION) || type.equals(EditorType.ACTION_CREATE_EXECUTOR) || type.equals(EditorType.ACTION_CREATE_FAIL)) {
//                EventHandler events = CollectionsUtil.getEnum(msg, EventHandler.class);
//                if (events == null) {
//                    EditorManager.error(player, "Ивент обработчик не найден!");
//                    return false;
//                }
//                EditorManager.endEdit(player);
//                EditorManager.startEdit(player, eventAction, type, actionInput);
//                EditorManager.tip(player, Lang.EDITOR_WRITE_ACTION.getDefaultText());
//                eventAction.getActiveRegion().save();
//            }
//            return true;
//        };

        MenuClick click = (player, type, inventoryClickEvent) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type2.equals(MenuItemType.RETURN))
                    this.parent.getActionListEditor().open(player, 1);
            } else if (type instanceof EditorType type2) {
                if (type2.equals(EditorType.ACTION_CREATE_CONDITION)) {
                    EditorManager.startEdit(player, parent, type2, actionInput);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_EVENTS.getDefaultText());
                    EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(EventHandler.class), false);
                    player.closeInventory();
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.EXECUTORS)) {
                    EditorManager.startEdit(player, parent, type2, actionInput);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_EVENTS.getDefaultText());
                    EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(EventHandler.class), false);
                    player.closeInventory();
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.FAIL)) {
                    EditorManager.startEdit(player, parent, type2, actionInput);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_EVENTS.getDefaultText());
                    EditorManager.suggestValues(player, CollectionsUtil.getEnumsList(EventHandler.class), false);
                    player.closeInventory();
                } else if (type2.equals(EditorType.EVENTS_CHANGE_CANCELLED)) {
                    EditorManager.startEdit(player, parent, type2, actionInput);
                }
            }
        };
        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        if (selectType.equals(ActionSelector.ActionSelectType.CONDITIONS)) {
            map.put(EditorType.ACTION_CREATE_CONDITION, 51);
        } else if (selectType.equals(ActionSelector.ActionSelectType.EXECUTORS)) {
            map.put(EditorType.ACTION_CREATE_EXECUTOR, 51);
        } else {
            map.put(EditorType.ACTION_CREATE_FAIL, 51);
        }
        map.put(MenuItemType.RETURN, 49);
    }

    @Override
    protected int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    protected @NotNull List<String> getObjects(@NotNull Player player) {
        if (this.selectType.equals(ActionSelector.ActionSelectType.CONDITIONS))
            return new ArrayList<>(this.actionSection.getConditions());
        if (this.selectType.equals(ActionSelector.ActionSelectType.EXECUTORS))
            return new ArrayList<>(this.actionSection.getActionExecutors());
        else if (this.selectType.equals(ActionSelector.ActionSelectType.FAIL))
            return List.of(this.actionSection.getConditionFailActions());
        return new ArrayList<>();
    }

    @Override
    protected @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull String eventAction) {
        ItemStack item = new ItemStack(Material.STONE);
        if (selectType.equals(ActionSelector.ActionSelectType.CONDITIONS))
            item = EditorType.ACTION_SELECT_CONDITIONS.getItem();
        if (selectType.equals(ActionSelector.ActionSelectType.EXECUTORS))
            item = EditorType.ACTION_SELECT_EXECUTORS.getItem();
        else if (selectType.equals(ActionSelector.ActionSelectType.FAIL))
            item = EditorType.ACTION_SELECT_FAIL.getItem();
        ItemUtil.replace(item, this.parent.replacePlaceholders());
        ItemUtil.replace(item, s -> s
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID, this.actionSection.getConditionFailActions())
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE, String.join("\n", this.actionSection.getConditions()))
                .replace(Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE, String.join("\n", this.actionSection.getActionExecutors()))
        );
        return item;
    }

    @Override
    protected @NotNull MenuClick getObjectClick(@NotNull Player player, @NotNull String eventAction) {
        return (player1, type, event) -> {
            ActionManipulator manipulator = this.parent.getManipulator();
            if (event.isShiftClick()) {
                if (this.selectType.equals(ActionSelector.ActionSelectType.CONDITIONS)) {
                    manipulator.getActions().forEach((key, value) -> value.getConditions().clear());
                    this.parent.setManipulator(manipulator);
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.EXECUTORS)) {
                    manipulator.getActions().forEach((key, value) -> value.getActionExecutors().clear());
                    this.parent.setManipulator(manipulator);
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.FAIL)) {
                    manipulator.getActions().forEach((key, value) -> value.setConditionActionOnFail(""));
                    this.parent.setManipulator(manipulator);
                }
            } else if (event.isRightClick()) {
                if (this.selectType.equals(ActionSelector.ActionSelectType.CONDITIONS)) {
                    manipulator.getActions().forEach((key, value) -> {
                        if (value.getConditions().size() == 1)
                            value.getConditions().clear();
                        else
                            value.getConditions().remove(value.getConditions().size() - 1);
                    });
                    this.parent.setManipulator(manipulator);
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.EXECUTORS)) {
                    manipulator.getActions().forEach((key, value) -> {
                        if (value.getActionExecutors().size() == 1)
                            value.getActionExecutors().clear();
                        else
                            value.getActionExecutors().remove(value.getActionExecutors().size() - 1);
                    });
                    this.parent.setManipulator(manipulator);
                }
            } else {
                if (this.selectType.equals(ActionSelector.ActionSelectType.CONDITIONS)) {
                    EditorManager.startEdit(player, this.parent, EditorType.ACTION_SELECT_CONDITIONS, this.actionManipulatorInput);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_ACTION.getDefaultText());
                    player.closeInventory();
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.EXECUTORS)) {
                    EditorManager.startEdit(player, this.parent, EditorType.ACTION_SELECT_EXECUTORS, this.actionManipulatorInput);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_ACTION.getDefaultText());
                    player.closeInventory();
                } else if (this.selectType.equals(ActionSelector.ActionSelectType.FAIL)) {
                    EditorManager.startEdit(player, this.parent, EditorType.ACTION_SELECT_FAIL, this.actionManipulatorInput);
                    EditorManager.tip(player, Lang.EDITOR_WRITE_ACTION.getDefaultText());
                    player.closeInventory();
                }
            }
        };
    }


    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
