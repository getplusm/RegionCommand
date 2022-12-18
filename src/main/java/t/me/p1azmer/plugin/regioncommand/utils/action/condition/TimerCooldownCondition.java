package t.me.p1azmer.plugin.regioncommand.utils.action.condition;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.util.Cooldown;
import t.me.p1azmer.aves.engine.actions.condition.AbstractConditionValidator;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterId;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterResult;
import t.me.p1azmer.aves.engine.actions.parameter.value.ParameterValueNumber;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;

import java.util.Collection;
import java.util.HashSet;

public class TimerCooldownCondition extends AbstractConditionValidator {

    private Collection<Player> preventCache = new HashSet<>();

    public TimerCooldownCondition() {
        super("REGION_COMMAND_TIMER");
        registerParameter(ParameterId.NAME);
        registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) {
            return true;
        }

        Events events = CollectionsUtil.getEnum(name.toUpperCase(), Events.class);
        if (events == null) {
            return true;
        }

        ParameterValueNumber amount = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (amount == null) {
            return true;
        }

        long timeRequest = (long) amount.getValue(0L);
        if (timeRequest <= 0L) {
            return true;
        }

        Region region = RegionAPI.PLUGIN.getManager().getRegion(player.getLocation(), 1);
        if (region == null) {
            return true;
        }

        if (!Cooldown.hasCooldown(player, TimerEventAction.COOLDOWN_KEY + "_" + name.toUpperCase()))
            return true;
        long time = Cooldown.getSecondCooldown(player, TimerEventAction.COOLDOWN_KEY + "_" + name.toUpperCase());
        if (time > 0) {
            events.cancelledCustomEvent(player, region, true);
            return false;
        }
        return preventCache.add(player);
    }
}
