package t.me.p1azmer.plugin.regioncommand.utils.action.executors;

import org.bukkit.Location;
import org.bukkit.Material;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockRestoreAction extends IActionExecutor {
    public BlockRestoreAction(RegPlugin plugin) {
        super(plugin, "REGION_RESTORE_BLOCK");
    }

    @Override
    public void registerParams() {
        this.registerParam(IParamType.NAME);
        this.registerParam(IParamType.TYPE);
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

        List<Material> materials = new ArrayList<>();

        for (String materialName : name.split(",")) {
            Material material = CollectionsUtil.getEnum(materialName, Material.class);
            if (material == null) {
                continue;
            }
            materials.add(material);
        }

        Material type = Material.AIR;

        if (result.hasParam(IParamType.TYPE)) {
            String typeName = result.getParamValue(IParamType.TYPE).getString(null);
            if (typeName != null) {
                Material material = CollectionsUtil.getEnum(typeName, Material.class);
                if (material != null) {
                    type = material;

                }
            }
        }

        int time = result.getParamValue(IParamType.AMOUNT).getInt(-1);
        if (time <= 0) {
            return;
        }

        EventHandler eventHandler = EventHandler.BLOCK_BREAK;
        Location location = entity.getLocation();

        Region region = RegionAPI.PLUGIN.getManager().getRegion(location, 1);
        if (region == null) {
            return;
        }
        if (entity instanceof LivingEntity livingEntity)
            eventHandler.callBlockRestore(livingEntity, region, type, time, materials);
    }

//    @Override
//    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
//        String name = (String) result.getValue(ParameterId.NAME);
//        if (name == null) {
//            return;
//        }
//
//        List<Material> materials = new ArrayList<>();
//
//        for (String materialName : name.split(",")) {
//            Material material = CollectionsUtil.getEnum(materialName, Material.class);
//            if (material == null) {
//                continue;
//            }
//            materials.add(material);
//        }
//
//        Material type = Material.AIR;
//
//        if (result.hasValue(ParameterId.TYPE)) {
//            String typeName = (String) result.getValue(ParameterId.TYPE);
//            if (typeName != null) {
//                Material material = CollectionsUtil.getEnum(typeName, Material.class);
//                if (material != null) {
//                    type = material;
//
//                }
//            }
//        }
//
//        ParameterValueNumber time = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
//        if (time == null) {
//            return;
//        }
//
//        long timeValue = (long) time.getValue(0L);
//        if (timeValue <= 0L) {
//            return;
//        }
//
//        EventHandler eventHandler = EventHandler.BLOCK_BREAK;
//        Location location = player.getLocation();
//
//        Region region = RegionAPI.PLUGIN.getManager().getRegion(location, 1);
//        if (region == null) {
//            return;
//        }
//
//        eventHandler.callBlockRestore(player, region, type, timeValue, materials);
//
//    }
}
