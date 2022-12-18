package t.me.p1azmer.plugin.regioncommand.api.events.cuboid;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.regioncommand.utils.Cuboid;

public abstract class CuboidEvents extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    protected Player player;
    protected Cuboid cuboid;
    protected boolean cancelled;

    public CuboidEvents(Player player, Cuboid cuboid){

        this.player = player;
        this.cuboid = cuboid;
    }

    public Cuboid getCuboid() {
        return cuboid;
    }

    public Player getPlayer() {
        return player;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
