package t.me.p1azmer.plugin.regioncommand.utils.action.executors;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.actions.IActionExecutor;
import t.me.p1azmer.aves.engine.actions.params.IParamResult;
import t.me.p1azmer.aves.engine.actions.params.IParamType;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;

import java.util.Set;

public class CancelledEventAction extends IActionExecutor {
    public CancelledEventAction(@NotNull RegPlugin plugin) {
        super(plugin, "REGION_COMMAND_BLOCK_EVENT");
    }

    @Override
    public void registerParams() {
        this.registerParam(IParamType.NAME);
    }

    @Override
    public boolean mustHaveTarget() {
        return false;
    }

    @Override
    protected void execute(@NotNull Entity entity, @NotNull Set<Entity> set, @NotNull IParamResult result) {
        String name = result.getParamValue(IParamType.NAME).getString(null);
        if (name == null) return;
        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
        if (eventHandler == null) {
            return;
        }

        Location location = entity.getLocation();

        Region region = RegionAPI.PLUGIN.getManager().getRegion(location, 1);
        if (region == null) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity)
            eventHandler.cancelledCustomEvent(livingEntity, region, true);
    }

//    @Override
//    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
//        String name = (String) result.getValue(ParameterId.NAME);
//        if (name == null) {
//            return;
//        }
//        EventHandler eventHandler = CollectionsUtil.getEnum(name.toUpperCase(), EventHandler.class);
//        if (eventHandler == null) {
//            return;
//        }
//
//        Location location = player.getLocation();
//
//        Region region = RegionAPI.PLUGIN.getManager().getRegion(location, 1);
//        if (region == null) {
//            return;
//        }
//        eventHandler.cancelledCustomEvent(player, region, true);
//    }
}
