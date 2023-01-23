package t.me.p1azmer.plugin.regioncommand.utils.action.executors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.action.AbstractActionExecutor;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterId;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterResult;
import t.me.p1azmer.aves.engine.actions.parameter.value.ParameterValueNumber;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class BlockRestoreAction extends AbstractActionExecutor {
    public BlockRestoreAction() {
        super("REGION_RESTORE_BLOCK");
        this.registerParameter(ParameterId.NAME);
        this.registerParameter(ParameterId.TYPE);
        this.registerParameter(ParameterId.AMOUNT);
    }

    @Override
    protected void execute(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) {
            return;
        }

        List<Material> materials = new ArrayList<>();

        for (String materialName : name.split(",")) {
            Material material = CollectionsUtil.getEnum(materialName, Material.class);
            if (material == null) {
                continue;
            }
            materials.add(material);
        }

        Material type = Material.AIR;

        if (result.hasValue(ParameterId.TYPE)) {
            String typeName = (String) result.getValue(ParameterId.TYPE);
            if (typeName != null) {
                Material material = CollectionsUtil.getEnum(typeName, Material.class);
                if (material != null) {
                    type = material;

                }
            }
        }

        ParameterValueNumber time = (ParameterValueNumber) result.getValue(ParameterId.AMOUNT);
        if (time == null) {
            return;
        }

        long timeValue = (long) time.getValue(0L);
        if (timeValue <= 0L) {
            return;
        }

        EventHandler eventHandler = EventHandler.BLOCK_BREAK;
        Location location = player.getLocation();

        Region region = RegionAPI.PLUGIN.getManager().getRegion(location, 1);
        if (region == null) {
            return;
        }

        eventHandler.callBlockRestore(player, region, type, timeValue, materials);

    }
}
