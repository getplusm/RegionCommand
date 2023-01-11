package t.me.p1azmer.plugin.regioncommand.utils.action.parameter;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.actions.parameter.AbstractParameter;
import t.me.p1azmer.aves.engine.actions.parameter.parser.IParameterValueParser;
import t.me.p1azmer.aves.engine.utils.CollectionsUtil;

public class MaterialParameter extends AbstractParameter<Boolean> {

    private final IParameterValueParser<Boolean> parser;

    public MaterialParameter() {
        super("MATERIAL", "material");
        this.parser = s -> {
            return CollectionsUtil.getEnum(s, Material.class) != null;
        };
    }

    @Override
    public @NotNull IParameterValueParser<Boolean> getParser() {
        return parser;
    }
}
