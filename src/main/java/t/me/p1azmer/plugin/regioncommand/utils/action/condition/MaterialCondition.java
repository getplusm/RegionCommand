package t.me.p1azmer.plugin.regioncommand.utils.action.condition;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.condition.AbstractConditionValidator;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterId;
import t.me.p1azmer.aves.engine.actions.parameter.ParameterResult;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;

public class MaterialCondition extends AbstractConditionValidator {

    public MaterialCondition() {
        super("IS_MATERIAL");
        registerParameter(ParameterId.NAME);
    }

    @Override
    protected boolean validate(@NotNull Player player, @NotNull ParameterResult result) {
        String name = (String) result.getValue(ParameterId.NAME);
        if (name == null) {
            player.sendMessage("IS_MATERIAL: name is null");
            return true;
        }
        boolean bol = CollectionsUtil.getEnum(name, Material.class) != null;
        player.sendMessage("IS_MATERIAL: check: " + (bol ? "not null" : "null"));
        return CollectionsUtil.getEnum(name, Material.class) != null;
    }
}
