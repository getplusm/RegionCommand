package t.me.p1azmer.plugin.regioncommand.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.aves.engine.actions.ActionManipulator;
import t.me.p1azmer.aves.engine.actions.ActionSection;
import t.me.p1azmer.aves.engine.api.lang.LangMessage;
import t.me.p1azmer.aves.engine.api.manager.IEditable;
import t.me.p1azmer.aves.engine.api.manager.IPlaceholder;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.aves.engine.lang.LangManager;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.EventActiveEditor;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.action.ActionSelector;
import t.me.p1azmer.plugin.regioncommand.editor.action.events.action.ManipulatorActionListEditor;

import java.util.function.UnaryOperator;

public class EventAction implements IPlaceholder, IEditable {

    private final ActiveRegion activeRegion;
    private final Events events;
    private LangMessage langMessage;
    private JPermission permission;
    private int cooldown;
    private boolean cancelled;
    private ActionManipulator manipulator;

    private EventActiveEditor editor;
    private ManipulatorActionListEditor actionListEditor;
    private ActionSelector actionSelector;

    public EventAction(@NotNull ActiveRegion activeRegion, @NotNull Events events, JPermission permission, LangMessage langMessage, int cooldown, ActionManipulator manipulator) {
        this.activeRegion = activeRegion;
        this.events = events;
        this.permission = permission;
        this.langMessage = langMessage;
        this.cooldown = cooldown;
        this.manipulator = manipulator;
        this.cancelled = false;
    }

    public EventAction(@NotNull ActiveRegion activeRegion, @NotNull Events events, JPermission permission, LangMessage langMessage, int cooldown, ActionManipulator manipulator, boolean cancelled) {
        this(activeRegion, events, permission, langMessage, cooldown, manipulator);
        this.setCancelled(cancelled);
    }

    public EventAction(@NotNull ActiveRegion activeRegion, @NotNull Events events) {
        this(activeRegion, events, null, null, -1, new ActionManipulator(), false);
    }

    public EventAction(@NotNull ActiveRegion activeRegion, @NotNull Events events, @NotNull ActionManipulator manipulator) {
        this(activeRegion, events, null, null, -1, manipulator, false);
    }

    public ActiveRegion getActiveRegion() {
        return activeRegion;
    }

    public Events getEvents() {
        return events;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    @Nullable
    public JPermission getPermission() {
        return permission;
    }

    public int getCooldown() {
        return cooldown;
    }

    public LangMessage getLangMessage() {
        return langMessage;
    }

    public ActionManipulator getManipulator() {
        return manipulator;
    }

    public void setManipulator(ActionManipulator manipulator) {
        this.manipulator = manipulator;
    }

    public void setPermission(JPermission permission) {
        this.permission = permission;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public void setLangMessage(LangMessage langMessage) {
        this.langMessage = langMessage;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public EventActiveEditor getEditor() {
        if (this.editor == null)
            this.editor = new EventActiveEditor(this.activeRegion.getRegion().plugin(), this);
        return editor;
    }

    @NotNull
    public ActionSelector getActionSelector(ActionSection section) {
        if (this.actionSelector == null)
            this.actionSelector = new ActionSelector(this.activeRegion.getRegion().plugin(), this, section);
        return actionSelector;
    }

    @NotNull
    public ManipulatorActionListEditor getActionListEditor() {
        if (this.actionListEditor == null)
            this.actionListEditor = new ManipulatorActionListEditor(this.activeRegion.getRegion().plugin(), this);
        return this.actionListEditor;
    }

    @Override
    public @NotNull UnaryOperator<String> replacePlaceholders() {
        return s -> s
                .replace(Placeholders.PLACEHOLDER_EVENTS_COOLDOWN, this.cooldown + " сек")
                .replace(Placeholders.PLACEHOLDER_EVENTS_CANCELLED, LangManager.getBoolean(this.cancelled))
                .replace(Placeholders.PLACEHOLDER_EVENTS_LANGKEY, this.langMessage == null ? "Нет" : this.langMessage.getRaw())
                .replace(Placeholders.PLACEHOLDER_EVENTS_PERMISSION, this.permission == null ? "Нет" : this.permission.getName())
                .replace(Placeholders.PLACEHOLDER_EVENTS_NAME, this.events.getName(getActiveRegion().getRegion().plugin()))
                ;
    }
}
