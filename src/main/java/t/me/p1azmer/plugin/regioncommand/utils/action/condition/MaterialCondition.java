package t.me.p1azmer.plugin.regioncommand.utils.action.condition;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.condition.AbstractConditionValidator;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterId;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterResult;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;

public class MaterialCondition extends AbstractConditionValidator {

    public MaterialCondition() {
        super("BREAK_MATERIAL");
        registerParameter(ParameterId.NAME);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) {
            return true;
        }
        Material material = CollectionsUtil.getEnum(name, Material.class);
        if (material == null)
            return true;
        Block block = player.getTargetBlock(4);
        if (block == null){
            return true;
        }
        return block.getType().equals(material);
    }
}
