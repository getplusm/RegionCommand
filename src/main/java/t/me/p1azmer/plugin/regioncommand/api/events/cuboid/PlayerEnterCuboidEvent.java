package t.me.p1azmer.plugin.regioncommand.api.events.cuboid;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

public class PlayerEnterCuboidEvent extends CuboidEvents{

    public PlayerEnterCuboidEvent(Player player, CuboidRegion cuboidRegion) {
        super(player, cuboidRegion);
    }
}
