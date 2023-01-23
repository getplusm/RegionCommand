package t.me.p1azmer.plugin.regioncommand.utils.action.executors;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.util.Cooldown;
import t.me.p1azmer.aves.engine.actions.action.AbstractActionExecutor;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterId;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterResult;
import t.me.p1azmer.aves.engine.actions.parameter.value.ParameterValueNumber;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;

public class TimerEventAction extends AbstractActionExecutor {

    public static String COOLDOWN_KEY = "REGION_COMMAND_TIMER";

    public TimerEventAction() {
        super("REGION_COMMAND_TIMER");
        registerParameter(ParameterId.NAME);
        registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) {
            return;
        }

        ParameterValueNumber time = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (time == null) return;

        long timeValue = (long) time.getValue(0D);
        if (timeValue <= 0L) return;

        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
        if (eventHandler == null) {
            return;
        }
        Region region = RegionAPI.PLUGIN.getManager().getRegion(player.getLocation(), 1);
        if (region == null) {
            return;
        }
        if (!Cooldown.hasCooldown(player, COOLDOWN_KEY + "_" + name.toUpperCase())) {
            Cooldown.hasOrAddCooldown(player, COOLDOWN_KEY + "_" + name.toUpperCase(), timeValue * 20L);
        }
    }
}
