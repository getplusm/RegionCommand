package t.me.p1azmer.plugin.regioncommand.utils.action.condition;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.api.util.Cooldown;
import t.me.p1azmer.aves.engine.actions.conditions.IConditionValidator;
import t.me.p1azmer.aves.engine.actions.params.IParamResult;
import t.me.p1azmer.aves.engine.actions.params.IParamType;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class TimerCooldownCondition extends IConditionValidator {

    private Collection<LivingEntity> preventCache = new HashSet<>();

    public TimerCooldownCondition(@NotNull RegPlugin plugin) {
        super(plugin, "REGION_COMMAND_TIMER");
    }

    @Override
    public void registerParams() {
        this.registerParam(IParamType.NAME);
        this.registerParam(IParamType.AMOUNT);
    }

    @Override
    public boolean mustHaveTarget() {
        return false;
    }

    @Override
    protected @Nullable Predicate<Entity> validate(@NotNull Entity entity, @NotNull Set<Entity> set, @NotNull IParamResult result) {
        String name = result.getParamValue(IParamType.NAME).getString(null);
        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
        if (eventHandler == null) {
            return null;
        }
        int timeResult = result.getParamValue(IParamType.AMOUNT).getInt(-1);
        if (timeResult <= 0) return null;
        return entity1 -> {
            if (entity1 instanceof LivingEntity livingEntity) {
                Region region = RegionAPI.PLUGIN.getManager().getRegion(livingEntity.getLocation(), 1);
                if (region == null) {
                    return true;
                }

                if (!Cooldown.hasCooldown(livingEntity.getName(), TimerEventAction.COOLDOWN_KEY + "_" + name.toUpperCase()))
                    return true;
                long time = Cooldown.getSecondCooldown(livingEntity.getName(), TimerEventAction.COOLDOWN_KEY + "_" + name.toUpperCase());
                if (time > 0) {
                    eventHandler.cancelledCustomEvent(livingEntity, region, true);
                    return false;
                }
                return preventCache.add(livingEntity);
            } else return false;
        };
    }

//    @Override
//    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
//        String name = (String) result.getValue(ParameterId.NAME);
//        if (name == null) {
//            return true;
//        }
//
//        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
//        if (eventHandler == null) {
//            return true;
//        }
//
//        ParameterValueNumber amount = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
//        if (amount == null) {
//            return true;
//        }
//
//        long timeRequest = (long) amount.getValue(0L);
//        if (timeRequest <= 0L) {
//            return true;
//        }
//
//        Region region = RegionAPI.PLUGIN.getManager().getRegion(player.getLocation(), 1);
//        if (region == null) {
//            return true;
//        }
//
//        if (!Cooldown.hasCooldown(player, TimerEventAction.COOLDOWN_KEY + "_" + name.toUpperCase()))
//            return true;
//        long time = Cooldown.getSecondCooldown(player, TimerEventAction.COOLDOWN_KEY + "_" + name.toUpperCase());
//        if (time > 0) {
//            eventHandler.cancelledCustomEvent(player, region, true);
//            return false;
//        }
//        return preventCache.add(player);
//    }
}
