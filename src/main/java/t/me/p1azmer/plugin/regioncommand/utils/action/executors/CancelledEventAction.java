package t.me.p1azmer.plugin.regioncommand.utils.action.executors;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.action.AbstractActionExecutor;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterId;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterResult;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;

public class CancelledEventAction extends AbstractActionExecutor {
    public CancelledEventAction() {
        super("REGION_COMMAND_BLOCK_EVENT");
        this.registerParameter(ParameterId.NAME);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) {
            return;
        }
        Events events = CollectionsUtil.getEnum(name.toUpperCase(), Events.class);
        if (events == null) {
            return;
        }
        Region region = RegionAPI.PLUGIN.getManager().getRegion(player.getLocation(), 1);
        if (region == null) {
            return;
        }
        events.cancelledCustomEvent(player, region, true);
    }
}
