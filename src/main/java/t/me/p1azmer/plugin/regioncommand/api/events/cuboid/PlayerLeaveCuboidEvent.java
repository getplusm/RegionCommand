package t.me.p1azmer.plugin.regioncommand.api.events.cuboid;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

public class PlayerLeaveCuboidEvent extends CuboidEvents {
    public PlayerLeaveCuboidEvent(LivingEntity player, CuboidRegion cuboidRegion) {
        super(player, cuboidRegion);
    }
}
