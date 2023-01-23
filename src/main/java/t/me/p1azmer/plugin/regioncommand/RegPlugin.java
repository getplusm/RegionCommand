package t.me.p1azmer.plugin.regioncommand;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.NexPlugin;
import t.me.p1azmer.aves.engine.api.command.GeneralCommand;
import t.me.p1azmer.aves.engine.api.editor.EditorHolder;
import t.me.p1azmer.aves.engine.command.list.EditorSubCommand;
import t.me.p1azmer.aves.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.editor.EditorHub;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.action.condition.MaterialCondition;
import t.me.p1azmer.plugin.regioncommand.utils.action.condition.TimerCooldownCondition;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.BlockRestoreAction;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.CancelledEventAction;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;
import t.me.p1azmer.plugin.regioncommand.utils.action.parameter.MaterialParameter;

public final class RegPlugin extends NexPlugin<RegPlugin> implements EditorHolder<RegPlugin, EditorType> {

    private RegionManager manager;
    private EditorHub editorHub;

    @Override
    protected @NotNull RegPlugin getSelf() {
        return this;
    }


    @Override
    public void enable() {
        this.manager = new RegionManager(this);
        this.manager.setup();

        this.getActionsManager().registerParameter(new MaterialParameter());

        this.getActionsManager().registerActionExecutor(new CancelledEventAction());
        this.getActionsManager().registerActionExecutor(new TimerEventAction());
        /**
         * Automatically restore block when destroyed
         * Example: name: STONE,COBBLESTONE; ~type: BEDROCK (replaced)
         */
        this.getActionsManager().registerActionExecutor(new BlockRestoreAction());

        this.getActionsManager().registerConditionValidator(new TimerCooldownCondition());
        this.getActionsManager().registerConditionValidator(new MaterialCondition());

    }

    @Override
    public void disable() {
        if (this.manager != null) {
            this.manager.shutdown();
            this.manager = null;
        }
    }

    @Override
    public void loadConfig() {

    }

    @Override
    public void loadLang() {
        this.langManager.loadMissing(Lang.class);
        this.langManager.setupEnum(EventHandler.class);
    }

    @Override
    public void registerHooks() {

    }

    @Override
    public void registerCommands(@NotNull GeneralCommand<RegPlugin> generalCommand) {
        generalCommand.addChildren(new ReloadSubCommand<>(this, "aves.admin"));
        generalCommand.addChildren(new EditorSubCommand<>(this, this, "aves.amdin"));
    }

    @Override
    public void registerPermissions() {

    }

    public RegionManager getManager() {
        return manager;
    }

    @Override
    public @NotNull EditorHub getEditor() {
        if (this.editorHub == null)
            this.editorHub = new EditorHub(this);
        return this.editorHub;
    }
}
