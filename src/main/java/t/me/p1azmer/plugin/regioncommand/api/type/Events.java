package t.me.p1azmer.plugin.regioncommand.api.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.aves.engine.utils.collections.AutoRemovalMap;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.*;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public enum Events {

    ENTER(Material.BIRCH_DOOR),
    LEAVE(Material.IRON_DOOR),
    MOVE(Material.LEATHER_BOOTS),
    JUMP(Material.FEATHER),
    SHIFT(Material.LEATHER_LEGGINGS),
    INSIDE(Material.DIRT), // like move?
    LMB(Material.BIRCH_BUTTON),
    RMB(Material.ACACIA_BUTTON),
    COMMANDS(Material.COMMAND_BLOCK),
    BLOCK_PLACE(Material.DIRT_PATH),
    BLOCK_BREAK(Material.BEDROCK),
    ;

    private final Material material;

    Events(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }


    public String getName(RegPlugin plugin) {
        return plugin.getLangManager().getEnum(this);
    }

    public Collection<Player> cancelledEvents = AutoRemovalCollection.newHashSet(20, TimeUnit.MILLISECONDS);;

    @NotNull
    public RegionEvents getCustomEvent(Player player, Region region) {
        return switch (this) {
            case MOVE -> new PlayerMoveInRegionEvent(player, region);
            case LMB -> new PlayerLMBInRegionEvent(player, region);
            case RMB -> new PlayerRMBInRegionEvent(player, region);
            case JUMP -> new PlayerJumpInRegionEvent(player, region);
            case ENTER -> new PlayerEnterRegionEvent(player, region);
            case LEAVE -> new PlayerLeaveInRegionEvent(player, region);
            case SHIFT -> new PlayerShiftInRegionEvent(player, region);
            case INSIDE -> new PlayerMoveInRegionEvent(player, region);
            case COMMANDS -> new PlayerCommandInRegionEvent(player, region);
            case BLOCK_BREAK -> new PlayerBlockBreakInRegionEvent(player, region);
            case BLOCK_PLACE -> new PlayerBlockPlaceInRegionEvent(player, region);
        };
    }

    public Event getOriginalEvent(Player player, Location from, Location to) {
        return switch (this) {
            case MOVE, JUMP, ENTER, LEAVE, INSIDE -> new PlayerMoveEvent(player, from, to);
            case LMB ->
                    new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getItemOnCursor(), from.getBlock(), BlockFace.SELF);
            case RMB ->
                    new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, player.getItemOnCursor(), from.getBlock(), BlockFace.SELF);
            case SHIFT -> new PlayerToggleSneakEvent(player, player.isSneaking());
            case COMMANDS -> new PlayerCommandPreprocessEvent(player, "");
            case BLOCK_BREAK -> new BlockBreakEvent(from.getBlock(), player);
            case BLOCK_PLACE ->
                    new BlockPlaceEvent(from.getBlock(), from.getBlock().getState(), Objects.requireNonNull(player.getTargetBlock(1)), player.getItemOnCursor(), player, true, EquipmentSlot.HAND);
        };
    }

    public boolean cancelledCustomEvent(Player player, Region region, boolean cancel) {
        if (this.cancelledEvents == null)
            this.cancelledEvents = AutoRemovalCollection.newHashSet(20, TimeUnit.MILLISECONDS);
        RegionEvents event = t.me.p1azmer.api.Events.callSyncAndJoin(getCustomEvent(player, region));
        if (this.cancelledEvents.add(player)) {
            event.setCancelled(cancel);
            Event originalEvent = t.me.p1azmer.api.Events.callSyncAndJoin(getOriginalEvent(player, player.getLocation(), player.getLocation()));
            if (originalEvent instanceof Cancellable cancellable) {
                cancellable.setCancelled(cancel);
                return event.isCancelled();
            }
            return event.isCancelled();
        }
        return false;
    }

}
