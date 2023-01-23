package t.me.p1azmer.plugin.regioncommand.api;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.ActionManipulator;
import t.me.p1azmer.aves.engine.actions.ActionSection;
import t.me.p1azmer.aves.engine.api.config.JYML;
import t.me.p1azmer.aves.engine.api.lang.LangMessage;
import t.me.p1azmer.aves.engine.api.manager.AbstractConfigHolder;
import t.me.p1azmer.aves.engine.api.manager.ICleanable;
import t.me.p1azmer.aves.engine.api.manager.IEditable;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.aves.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;
import t.me.p1azmer.plugin.regioncommand.editor.action.ActiveRegionEditor;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.EventActiveListEditor;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.EventHandlerListMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ActiveRegion extends AbstractConfigHolder<RegPlugin> implements IPlaceholder, IEditable, ICleanable {

    private final Region region;
    private double radius;
    private final Map<EventHandler, Integer> cooldowns;
    private final Map<EventHandler, Boolean> cancelled;
    private final List<EventAction> eventActions;

    private final JYML config;

    private ActiveRegionEditor editor;
    private EventActiveListEditor eventActiveListEditor;

    private EventHandlerListMenu eventHandlerListMenu; // menu for eventhandler selector

    public ActiveRegion(Region region) {
        super(region.plugin(), region.getConfig());
        this.region = region;
        this.config = region.getConfig();
        this.radius = 0;
        this.cooldowns = new HashMap<>();
        this.eventActions = new ArrayList<>();
        this.cancelled = new HashMap<>();

        this.eventHandlerListMenu = new EventHandlerListMenu(this);
    }

    public ActiveRegion(Region region, JYML cfg) {
        super(region.plugin(), cfg);
        this.config = cfg;
        this.region = region;

        this.cooldowns = new HashMap<>();
        this.cancelled = new HashMap<>();
        this.eventActions = new ArrayList<>();

        String path = "Active.";

        this.radius = cfg.getDouble(path + "Radius");

        path = path + "Cooldown";

        String finalPath1 = path + ".";
        cfg.getSection(path).forEach(id -> {
            String finalPath = finalPath1 + id + ".";
            EventHandler event = CollectionsUtil.getEnum(id, EventHandler.class);
            if (event == null) {
                throw new IllegalArgumentException("Invalid event enum at " + region.getId() + " region!");
            }
            int time = cfg.getInt(finalPath + id);
            this.cooldowns.put(event, time);
        });

        path = "Active.Events";
        String finalPath2 = path + ".";

        cfg.getSection(path).forEach(id -> {
            String finalPath = finalPath2 + id + ".";
            EventHandler event = CollectionsUtil.getEnum(id, EventHandler.class);
            if (event == null) {
                throw new IllegalArgumentException("Invalid event enum at " + region.getId() + " region!");
            }
            JPermission permission = null;
            String perm = cfg.getString(finalPath + "Permission", null);
            if (!perm.isEmpty())
                permission = new JPermission(perm, "Доступ к действию в регионе");
            int time = cfg.getInt(finalPath + "Cooldown");
            boolean canceled = cfg.getBoolean(finalPath + "Cancelled", false);
            this.cancelled.put(event, canceled);

            ActionManipulator manipulator = new ActionManipulator(cfg, finalPath + "Action");

            LangMessage langMessage = new LangMessage(region.plugin(), cfg.getString(finalPath + "Cooldown.Message", "&cДействие невозможно выполнять так часто, ожидайте"));
            EventAction eventAction = new EventAction(this, event, permission, langMessage, time, manipulator, canceled);
            this.eventActions.add(eventAction);
        });

        this.eventHandlerListMenu = new EventHandlerListMenu(this);
    }

    public double getRadius() {
        return radius;
    }

    public Map<EventHandler, Integer> getCooldowns() {
        return cooldowns;
    }

    public List<EventAction> getEventActions() {
        return eventActions;
    }

    public EventAction getEventActionByEvent(EventHandler eventHandler) {
        return eventActions.stream().filter(f -> f.getEventHandler().equals(eventHandler)).findAny().orElse(null);
    }

    public Map<EventHandler, Boolean> getCancelled() {
        return cancelled;
    }

    public Region getRegion() {
        return region;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public boolean load() {
        return true;
    }

    @Override
    public void onSave() {
        String path = "Active.";
        this.config.set(path + "Radius", this.getRadius());
        this.config.set(path + "Cooldown", this.getCooldowns());
        String finalPath1 = path + "Events.";
        if (!this.config.contains(finalPath1))
            this.config.createSection(path + "Events");
        this.eventActions.forEach(eventAction -> {
            String id = eventAction.getEventHandler().name();
            String finalPath = finalPath1 + id + ".";
            this.config.set(finalPath + "Cancelled", eventAction.isCancelled());
            if (eventAction.getPermission() != null) {
                this.config.set(finalPath + "Permission", eventAction.getPermission().getName());
            } else if (this.config.contains(finalPath + "Permission"))
                this.config.remove(finalPath + "Permission");
            if (eventAction.getLangMessage() != null) {
                this.config.set(finalPath + "Cooldown.Message", eventAction.getLangMessage().getRaw());
            } else if (this.config.contains(finalPath + "Cooldown.Message"))
                this.config.remove(finalPath + "Cooldown.Message");
            if (eventAction.getManipulator() != null) {
                for (ActionSection section : eventAction.getManipulator().getActions().values()) {
                    this.config.set(finalPath + "Action." + section.getId() + ".Conditions.List", section.getConditions());
                    this.config.set(finalPath + "Action." + section.getId() + ".Conditions.Fail_Actions", section.getConditionFailActions());
                    this.config.set(finalPath + "Action." + section.getId() + ".Action_Executors", section.getActionExecutors());
                }
            }

        });
        this.config.saveChanges();
    }

    @NotNull
    @Override
    public ActiveRegionEditor getEditor() {
        if (this.editor == null)
            this.editor = new ActiveRegionEditor(this.region.plugin(), this);
        return editor;
    }

    @NotNull
    public EventActiveListEditor getEventActiveListEditor() {
        if (this.eventActiveListEditor == null)
            this.eventActiveListEditor = new EventActiveListEditor(this.region.plugin(), this);
        return this.eventActiveListEditor;
    }

    public EventHandlerListMenu getEventHandlerListMenu() {
        return eventHandlerListMenu;
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_ACTION_RADIUS, String.valueOf(this.radius))
                .replace(Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE, NumberUtil.format(this.eventActions.size()))
                ;
    }

    @Override
    public void clear() {
        if (this.eventActiveListEditor != null) {
            this.eventActiveListEditor.clear();
            this.eventActiveListEditor = null;
        }
        if (this.editor != null) {
            this.editor.clear();
            this.editor = null;
        }
        if (this.eventHandlerListMenu != null){
            this.eventHandlerListMenu.clear();
            this.eventHandlerListMenu = null;
        }
    }

    public void deleteEventAction(EventAction event){
        this.eventActions.remove(event);
        this.config.set("Active.Events."+event.getEventHandler().name().toUpperCase(), "");
        this.config.saveChanges();
    }
}
