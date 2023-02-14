package t.me.p1azmer.plugin.regioncommand.utils.action.parameter;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.params.IAutoValidated;
import t.me.p1azmer.aves.engine.actions.params.IParamValue;
import t.me.p1azmer.aves.engine.actions.params.defaults.IParamBoolean;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;

import java.util.Set;

public class MaterialParameter extends IParamBoolean implements IAutoValidated {

//    private final IParameterValueParser<Boolean> parser;

    public MaterialParameter() {
        super("MATERIAL", "material");
//        this.parser = s -> {
//            return CollectionsUtil.getEnum(s, Material.class) != null;
//        };
    }

//    @Override
//    public @NotNull IParameterValueParser<Boolean> getParser() {
//        return parser;
//    }

    @Override
    public void autoValidate(@NotNull Entity entity, @NotNull Set<Entity> set, @NotNull IParamValue result) {
        String name = result.getString(null);
        if (name == null) return;
        result.setBoolean(CollectionsUtil.getEnum(name, Material.class) != null);
    }
}
