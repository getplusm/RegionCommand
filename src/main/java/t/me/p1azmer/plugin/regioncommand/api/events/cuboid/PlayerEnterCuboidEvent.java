package t.me.p1azmer.plugin.regioncommand.api.events.cuboid;

import org.bukkit.entity.Player;
import t.me.p1azmer.plugin.regioncommand.utils.Cuboid;

public class PlayerEnterCuboidEvent extends CuboidEvents{

    public PlayerEnterCuboidEvent(Player player, Cuboid cuboid) {
        super(player, cuboid);
    }
}
