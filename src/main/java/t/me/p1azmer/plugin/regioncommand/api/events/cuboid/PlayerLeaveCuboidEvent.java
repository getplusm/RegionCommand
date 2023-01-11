package t.me.p1azmer.plugin.regioncommand.api.events.cuboid;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

public class PlayerLeaveCuboidEvent extends CuboidEvents {
    public PlayerLeaveCuboidEvent(Player player, CuboidRegion cuboidRegion) {
        super(player, cuboidRegion);
    }
}
