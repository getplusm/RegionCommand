package t.me.p1azmer.plugin.regioncommand.api.events.region;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.regioncommand.api.Region;

public abstract class RegionEvents extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    protected Player player;
    protected Region region;
    protected boolean cancelled;

    public RegionEvents(Player player, Region region){
        this.player = player;
        this.region = region;
    }

    public Region getRegion() {
        return region;
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
