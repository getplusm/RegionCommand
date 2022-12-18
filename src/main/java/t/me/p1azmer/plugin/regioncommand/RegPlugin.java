package t.me.p1azmer.plugin.regioncommand;

import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.NexPlugin;
import t.me.p1azmer.aves.engine.api.command.GeneralCommand;
import t.me.p1azmer.aves.engine.api.editor.EditorHolder;
import t.me.p1azmer.aves.engine.command.list.EditorSubCommand;
import t.me.p1azmer.aves.engine.command.list.ReloadSubCommand;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.data.Lang;
import t.me.p1azmer.plugin.regioncommand.editor.EditorHub;
import t.me.p1azmer.plugin.regioncommand.editor.EditorType;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.action.CancelledEventAction;

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


        this.getActionsManager().registerActionExecutor(new CancelledEventAction());
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
        this.langManager.setupEnum(Events.class);
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
