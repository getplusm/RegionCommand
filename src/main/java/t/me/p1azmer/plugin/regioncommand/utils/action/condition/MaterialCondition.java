package t.me.p1azmer.plugin.regioncommand.utils.action.condition;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.aves.engine.actions.conditions.IConditionValidator;
import t.me.p1azmer.aves.engine.actions.params.IParamResult;
import t.me.p1azmer.aves.engine.actions.params.IParamType;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;

import java.util.Set;
import java.util.function.Predicate;

public class MaterialCondition extends IConditionValidator {

    public MaterialCondition(@NotNull RegPlugin plugin) {
        super(plugin, "BREAK_MATERIAL");
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
    protected @Nullable Predicate<Entity> validate(@NotNull Entity entity, @NotNull Set<Entity> set, @NotNull IParamResult result) {
        if (!result.hasParam(IParamType.NAME)) return null;
        String name = result.getParamValue(IParamType.NAME).getString(null);
        Material material = CollectionsUtil.getEnum(name, Material.class);
        if (material == null)
            return null;
        return entity1 -> {
            if (entity1 instanceof Player player) {
                Block block = player.getTargetBlock(4);
                if (block == null) {
                    return false;
                }
                return block.getType().equals(material);
            } else return false;
        };
    }

//    @Override
//    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
//        String name = (String) result.getValue(ParameterId.NAME);
//        if (name == null) {
//            return true;
//        }
//        Material material = CollectionsUtil.getEnum(name, Material.class);
//        if (material == null)
//            return true;
//        Block block = player.getTargetBlock(4);
//        if (block == null) {
//            return true;
//        }
//        return block.getType().equals(material);
//    }
}
