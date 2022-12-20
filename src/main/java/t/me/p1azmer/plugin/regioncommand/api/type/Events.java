package t.me.p1azmer.plugin.regioncommand.api.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public enum Events {

    ENTER(Material.BIRCH_DOOR),
    LEAVE(Material.IRON_DOOR),
    MOVE(Material.LEATHER_BOOTS),
    JUMP(Material.FEATHER),
    SHIFT_UP(Material.LEATHER_LEGGINGS),
    SHIFT_DOWN(Material.DIAMOND_LEGGINGS),
    INSIDE(Material.DIRT), // like move?
    LMB(Material.BIRCH_BUTTON),
    RMB(Material.ACACIA_BUTTON),
    COMMANDS(Material.COMMAND_BLOCK),
    BLOCK_PLACE(Material.DIRT_PATH),
    BLOCK_BREAK(Material.BEDROCK),
    DAMAGE_PLAYER(Material.DIAMOND_SWORD),
    DAMAGE_ENTITY(Material.WOODEN_SWORD),
    TAKE_DAMAGE(Material.SHIELD),
    TAKE_DAMAGE_PLAYER(Material.SHIELD),
    PICKUP_ITEMS(Material.HOPPER),
    DROP_ITEMS(Material.WATER_BUCKET),
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

    public Collection<Player> cancelledEvents = AutoRemovalCollection.newHashSet(20, TimeUnit.MILLISECONDS);
    ;

    @NotNull
    public RegionEvents getCustomEvent(Player player, Region region) {
        return switch (this) {
            case MOVE -> new PlayerMoveInRegionEvent(player, region);
            case LMB -> new PlayerLMBInRegionEvent(player, region);
            case RMB -> new PlayerRMBInRegionEvent(player, region);
            case JUMP -> new PlayerJumpInRegionEvent(player, region);
            case ENTER -> new PlayerEnterRegionEvent(player, region);
            case LEAVE -> new PlayerLeaveInRegionEvent(player, region);
            case SHIFT_UP -> new PlayerShiftUPInRegionEvent(player, region);
            case INSIDE -> new PlayerMoveInRegionEvent(player, region);
            case COMMANDS -> new PlayerCommandInRegionEvent(player, region);
            case BLOCK_BREAK -> new PlayerBlockBreakInRegionEvent(player, region);
            case BLOCK_PLACE -> new PlayerBlockPlaceInRegionEvent(player, region);
            case DROP_ITEMS -> new PlayerDropItemInRegionEvent(player, region);
            case SHIFT_DOWN -> new PlayerShiftDOWNInRegionEvent(player, region);
            case TAKE_DAMAGE -> new PlayerTakeDamageInRegionEvent(player, region);
            case PICKUP_ITEMS -> new PlayerPickUpItemInRegionEvent(player, region);
            case DAMAGE_ENTITY -> new PlayerDamageEntityInRegionEvent(player, region);
            case DAMAGE_PLAYER -> new PlayerDamagePlayerInRegionEvent(player, region);
            case TAKE_DAMAGE_PLAYER -> new PlayerTakeByPlayerDamageInRegionEvent(player, region);
        };
    }

    public Event getOriginalEvent(Player player, Location from, Location to) {
        return switch (this) {
            case MOVE, JUMP, ENTER, LEAVE, INSIDE -> new PlayerMoveEvent(player, from, to);
            case LMB ->
                    new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getItemOnCursor(), from.getBlock(), BlockFace.SELF);
            case RMB ->
                    new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, player.getItemOnCursor(), from.getBlock(), BlockFace.SELF);
            case SHIFT_UP -> new PlayerToggleSneakEvent(player, player.isSneaking());
            case COMMANDS -> new PlayerCommandPreprocessEvent(player, "");
            case BLOCK_BREAK -> new BlockBreakEvent(from.getBlock(), player);
            case DAMAGE_PLAYER, DAMAGE_ENTITY ->
                    new EntityDamageByEntityEvent(player, Objects.requireNonNull(player.getTargetEntity(10) == null ? null : player.getTargetEntity(10)), EntityDamageEvent.DamageCause.CONTACT, new HashMap<>(), new HashMap<>(), false);
            //case PICKUP_ITEMS -> new EntityPickupItemEvent(player, player.getItemOnCursor()., 5);
            case BLOCK_PLACE, DROP_ITEMS, PICKUP_ITEMS, SHIFT_DOWN, TAKE_DAMAGE, TAKE_DAMAGE_PLAYER ->
                    new BlockPlaceEvent(from.getBlock(), from.getBlock().getState(), Objects.requireNonNull(player.getTargetBlock(1)), player.getItemOnCursor(), player, true, EquipmentSlot.HAND);
        };
    }

    public boolean cancelledCustomEvent(Player player, Region region, boolean cancel) {
        if (this.cancelledEvents == null)
            this.cancelledEvents = AutoRemovalCollection.newHashSet(20, TimeUnit.MILLISECONDS);
        RegionEvents event = t.me.p1azmer.api.Events.callSyncAndJoin(getCustomEvent(player, region));
        if (this.cancelledEvents.add(player)) {
            event.setCancelled(cancel);
//            Event originalEvent = t.me.p1azmer.api.Events.callSyncAndJoin(getOriginalEvent(player, player.getLocation(), player.getLocation()));
//            if (originalEvent instanceof Cancellable cancellable) {
//                cancellable.setCancelled(cancel);
//                return event.isCancelled();
//            }
            return event.isCancelled();
        }
        return false;
    }

}
