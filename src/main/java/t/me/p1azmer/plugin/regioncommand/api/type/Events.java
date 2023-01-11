package t.me.p1azmer.plugin.regioncommand.api.type;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.api.returns.TripleReturn;
import t.me.p1azmer.aves.engine.utils.Pair;
import t.me.p1azmer.aves.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockBreakInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockPlaceInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.RestoreBlockEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerDamageEntityInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerDamagePlayerInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerTakeByPlayerDamageInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerTakeDamageInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityCollisionInRegion;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityTakeDamageFromBlockInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityTakeDamageFromLavaInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.movement.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.player.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.use.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public enum Events {

    ENTER(Material.BIRCH_DOOR),
    LEAVE(Material.IRON_DOOR),
    MOVE(Material.LEATHER_BOOTS),
    JUMP(Material.FEATHER),
    SHIFT_UP(Material.LEATHER_LEGGINGS),
    SHIFT_DOWN(Material.DIAMOND_LEGGINGS),
    //INSIDE(Material.DIRT), // like move?
    LMB(Material.BIRCH_BUTTON),
    RMB(Material.ACACIA_BUTTON),
    COMMANDS(Material.COMMAND_BLOCK),
    BLOCK_PLACE(Material.DIRT_PATH),
    BLOCK_BREAK(Material.WOODEN_PICKAXE),
    /**
     * disable damage to any player
     */
    DAMAGE_PLAYER(Material.DIAMOND_SWORD),
    /**
     * disable damage to any entity
     */
    DAMAGE_ENTITY(Material.WOODEN_SWORD),
    TAKE_DAMAGE(Material.SHIELD),
    /**
     * prevent all damage from player
     */
    TAKE_DAMAGE_PLAYER(Material.SHIELD),

    /**
     * prevent damage from all collision blocks
     */

    TAKE_DAMAGE_FROM_BLOCK(Material.IRON_BLOCK),
    /**
     * prevent damage from lava
     */
    TAKE_DAMAGE_FROM_LAVA(Material.LAVA_BUCKET),


    /**
     * disable pickup all items
     */
    PICKUP_ITEMS(Material.HOPPER),

    /**
     * disable drop any items
     */
    DROP_ITEMS(Material.WATER_BUCKET),
    HUNGER(Material.BEEF),

    /**
     * regen HP
     */
    REGEN_HP(Material.POTION),
    /**
     * regen Hunger
     */
    REGEN_HUNGER(Material.BEETROOT_SOUP),

    SPRINT(Material.FEATHER),
    /**
     * we are cant block respawn, but this for Action section methods
     */

    RESPAWN(Material.RED_BED),

    /**
     * disable collision
     */
    COLLISION(Material.OAK_FENCE),

    /**
     * open chests
     */
    OPEN_CHEST(Material.CHEST),
    OPEN_ENDER_CHEST(Material.ENDER_CHEST),

    /**
     * prevent use any items. Like button, stove etc.
     */
    USE(Material.STONE_BUTTON),

    DEATH(Material.PLAYER_HEAD),
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

    public Collection<LivingEntity> cancelledEvents = AutoRemovalCollection.newHashSet(20, TimeUnit.MILLISECONDS);
    public static Map<Region, TripleReturn<List<Material>, Material, Long>> materialsToRestore = new HashMap<>();


    public static Map<Region, TripleReturn<List<Material>, Material, Long>> getMaterialsToRestore() {
        return materialsToRestore;
    }

    public Collection<LivingEntity> getCancelledEvents() {
        return cancelledEvents;
    }

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
            //case INSIDE -> new PlayerMoveInRegionEvent(player, region); removed, but he is MOVE event
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
            case HUNGER -> new PlayerHungerInRegionEvent(player, region);
            case USE -> new PlayerUseInRegionEvent(player, region);
            case SPRINT -> new PlayerSprintInRegionEvent(player, region);
            case RESPAWN -> new PlayerRespawnInRegionEvent(player, region);
            case REGEN_HP -> new PlayerRegenHPInRegionEvent(player, region);
            case COLLISION -> new EntityCollisionInRegion(player, region);
            case OPEN_CHEST -> new PlayerOpenChestInRegionEvent(player, region);
            case REGEN_HUNGER -> new PlayerRegenHungerInRegionEvent(player, region);
            case OPEN_ENDER_CHEST -> new PlayerOpenEnderChestInRegionEvent(player, region);
            case TAKE_DAMAGE_FROM_LAVA -> new EntityTakeDamageFromLavaInRegionEvent(player, region);
            case TAKE_DAMAGE_FROM_BLOCK -> new EntityTakeDamageFromBlockInRegionEvent(player, region);
            case DEATH -> new PlayerDeathInRegionEvent(player, region);
        };
    }

//    public Event getOriginalEvent(Player player, Location from, Location to) { its working without this. yoa
//        return switch (this) {
//            case MOVE, JUMP, ENTER, LEAVE, INSIDE -> new PlayerMoveEvent(player, from, to);
//            case LMB ->
//                    new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, player.getItemOnCursor(), from.getBlock(), BlockFace.SELF);
//            case RMB ->
//                    new PlayerInteractEvent(player, Action.LEFT_CLICK_BLOCK, player.getItemOnCursor(), from.getBlock(), BlockFace.SELF);
//            case SHIFT_UP -> new PlayerToggleSneakEvent(player, player.isSneaking());
//            case COMMANDS -> new PlayerCommandPreprocessEvent(player, "");
//            case BLOCK_BREAK -> new BlockBreakEvent(from.getBlock(), player);
//            case DAMAGE_PLAYER, DAMAGE_ENTITY ->
//                    new EntityDamageByEntityEvent(player, Objects.requireNonNull(player.getTargetEntity(10) == null ? null : player.getTargetEntity(10)), EntityDamageEvent.DamageCause.CONTACT, new HashMap<>(), new HashMap<>(), false);
//            //case PICKUP_ITEMS -> new EntityPickupItemEvent(player, player.getItemOnCursor()., 5);
//            case BLOCK_PLACE, DROP_ITEMS, PICKUP_ITEMS, SHIFT_DOWN, TAKE_DAMAGE, TAKE_DAMAGE_PLAYER ->
//                    new BlockPlaceEvent(from.getBlock(), from.getBlock().getState(), Objects.requireNonNull(player.getTargetBlock(1)), player.getItemOnCursor(), player, true, EquipmentSlot.HAND);
//        };
//    }

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

    public void callBlockRestore(Player player, Region region, Material type, long time, Material... materials) {
        callBlockRestore(player, region, type, time,Arrays.stream(materials).toList());
    }

    public void callBlockRestore(Player player, Region region, @Nullable Material type, long time, List<Material> materials) {
        if (materialsToRestore == null)
            materialsToRestore = new HashMap<>();
        materialsToRestore.put(region, new TripleReturn<>(materials, type, time));
//        t.me.p1azmer.api.Events.callSync(new RestoreBlockEvent(player, region, materials));
    }

}
