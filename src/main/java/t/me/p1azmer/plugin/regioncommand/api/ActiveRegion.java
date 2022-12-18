package t.me.p1azmer.plugin.regioncommand.api;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.ActionManipulator;
import t.me.p1azmer.aves.engine.actions.ActionSection;
import t.me.p1azmer.aves.engine.api.config.JYML;
import t.me.p1azmer.aves.engine.api.lang.LangKey;
import t.me.p1azmer.aves.engine.api.manager.IEditable;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.aves.engine.utils.NumberUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.editor.action.ActiveRegionEditor;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.EventActiveListEditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class ActiveRegion implements IPlaceholder, IEditable {

    private final Region region;
    private double radius;
    private final Map<Events, Integer> cooldowns;
    private final Map<Events, Boolean> cancelled;
    private final List<EventAction> eventActions;

    private final JYML config;

    private ActiveRegionEditor editor;
    private EventActiveListEditor eventActiveListEditor;

    public ActiveRegion(Region region) {
        this.region = region;
        this.config = region.getConfig();
        this.radius = 0;
        this.cooldowns = new HashMap<>();
        this.eventActions = new ArrayList<>();
        this.cancelled = new HashMap<>();
    }

    public ActiveRegion(Region region, JYML cfg) {
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
            Events event = CollectionsUtil.getEnum(id, Events.class);
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
            Events event = CollectionsUtil.getEnum(id, Events.class);
            if (event == null) {
                throw new IllegalArgumentException("Invalid event enum at " + region.getId() + " region!");
            }
            JPermission permission = null;
            String perm = cfg.getString(finalPath + "Permission", null);
            if (!perm.isEmpty())
                permission = new JPermission(perm, "Доступ к действию в регионе");
            int time = cfg.getInt(finalPath + "Cooldown");
            boolean canceled = cfg.getBoolean(finalPath + "Canceled", false);
            this.cancelled.put(event, canceled);
            ActionManipulator manipulator = new ActionManipulator(cfg, finalPath + "Action");

            LangKey langKey = LangKey.of(finalPath + "Cooldown.Message", "&cДействие невозможно выполнять так часто, ожидайте");
            EventAction eventAction = new EventAction(this, event, permission, langKey, time, manipulator, canceled);
            this.eventActions.add(eventAction);
        });
    }

    public double getRadius() {
        return radius;
    }

    public Map<Events, Integer> getCooldowns() {
        return cooldowns;
    }

    public List<EventAction> getEventActions() {
        return eventActions;
    }

    public EventAction getEventActionByEvent(Events events) {
        return eventActions.stream().filter(f -> f.getEvents().equals(events)).findFirst().orElse(null);
    }

    public Map<Events, Boolean> getCancelled() {
        return cancelled;
    }

    public Region getRegion() {
        return region;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void save() {
        StringBuilder path = new StringBuilder("Active.");
        this.config.set(path + "Radius", this.getRadius());
        this.config.set(path + "Cooldown", this.getCooldowns());
        path.append("Events.");
        this.getEventActions().forEach(eventAction -> {
            String id = eventAction.getEvents().name();
            path.append(id).append(".");
            this.config.set(path + "Permission", eventAction.getPermission() == null ? null : eventAction.getPermission().getName());
            this.config.set(path + "Cooldown.Time", eventAction.getCooldown());
            this.config.set(path + "Cooldown.Message", eventAction.getLangKey().getDefaultText());
            for (ActionSection section : eventAction.getManipulator().getActions().values()) {
                this.config.set(path + "Action.Conditions.List", section.getConditions());
                this.config.set(path + "Action.Conditions.Fail_Actions", section.getConditionFailActions());
                this.config.set(path + "Action.Action_Executors", section.getActionExecutors());
            }

        });
        this.config.save();
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

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_ACTION_RADIUS, String.valueOf(this.radius))
                .replace(Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE, NumberUtil.format(this.eventActions.size()))
                ;
    }
}
