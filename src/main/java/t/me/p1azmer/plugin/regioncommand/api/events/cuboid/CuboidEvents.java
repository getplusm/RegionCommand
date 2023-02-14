package t.me.p1azmer.plugin.regioncommand.api.events.cuboid;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.plugin.regioncommand.utils.CuboidRegion;

public abstract class CuboidEvents extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    protected LivingEntity entity;
    protected CuboidRegion cuboidRegion;
    protected boolean cancelled;

    public CuboidEvents(LivingEntity entity, CuboidRegion cuboidRegion){

        this.entity = entity;
        this.cuboidRegion = cuboidRegion;
    }

    public CuboidRegion getCuboid() {
        return cuboidRegion;
    }

    public LivingEntity getEntity() {
        return entity;
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
