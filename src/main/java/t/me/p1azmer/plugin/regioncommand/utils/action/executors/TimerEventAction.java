package t.me.p1azmer.plugin.regioncommand.utils.action.executors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.util.Cooldown;
import t.me.p1azmer.aves.engine.actions.actions.IActionExecutor;
import t.me.p1azmer.aves.engine.actions.params.IParamResult;
import t.me.p1azmer.aves.engine.actions.params.IParamType;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;

import java.util.Set;

public class TimerEventAction extends IActionExecutor {

    public static String COOLDOWN_KEY = "REGION_COMMAND_TIMER";

    public TimerEventAction(@NotNull RegPlugin plugin) {
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
    protected void execute(@NotNull Entity entity, @NotNull Set<Entity> set, @NotNull IParamResult result) {
        String name = result.getParamValue(IParamType.NAME).getString(null);
        if (name == null) return;
        long timeValue = result.getParamValue(IParamType.AMOUNT).getInt(-1);
        if (timeValue <= 0L) return;

        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
        if (eventHandler == null) {
            return;
        }
        Region region = RegionAPI.PLUGIN.getManager().getRegion(entity.getLocation(), 1);
        if (region == null) {
            return;
        }
        if (entity instanceof Player player) {
            if (!Cooldown.hasCooldown(player, COOLDOWN_KEY + "_" + name.toUpperCase())) {
                Cooldown.hasOrAddCooldown(player, COOLDOWN_KEY + "_" + name.toUpperCase(), timeValue * 20L);
            }
        }
    }

//    @Override
//    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
//        String name = (String) result.getValue(ParameterId.NAME);
//        if (name == null) {
//            return;
//        }
//
//        ParameterValueNumber time = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
//        if (time == null) return;
//
//        long timeValue = (long) time.getValue(0D);
//        if (timeValue <= 0L) return;
//
//        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
//        if (eventHandler == null) {
//            return;
//        }
//        Region region = RegionAPI.PLUGIN.getManager().getRegion(player.getLocation(), 1);
//        if (region == null) {
//            return;
//        }
//        if (!Cooldown.hasCooldown(player, COOLDOWN_KEY + "_" + name.toUpperCase())) {
//            Cooldown.hasOrAddCooldown(player, COOLDOWN_KEY + "_" + name.toUpperCase(), timeValue * 20L);
//        }
//    }
}
