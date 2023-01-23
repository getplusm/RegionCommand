package t.me.p1azmer.plugin.regioncommand.api.type;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import t.me.p1azmer.api.returns.TripleReturn;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.aves.engine.utils.collections.AutoRemovalCollection;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.RegionAPI;
import t.me.p1azmer.plugin.regioncommand.api.events.region.RegionEvents;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockBreakInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockPlaceInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityCollisionInRegion;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityTakeDamageFromBlockInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityTakeDamageFromLavaInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.movement.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.player.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.use.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public enum EventHandler {

    ENTER(new ItemStack(Material.BIRCH_DOOR),
            List.of("&7Отвечает за вход в регион")),
    LEAVE(new ItemStack(Material.IRON_DOOR),
            List.of("&7Отвечает за выход из региона")),
    MOVE(new ItemStack(Material.LEATHER_BOOTS),
            List.of("&7Отвечает за движение внутри региона")),
    JUMP(new ItemStack(Material.SLIME_BLOCK),
            List.of("&7Отвечает за прыжки в регионе")),
    SHIFT_UP(new ItemStack(Material.LEATHER_LEGGINGS),
            List.of("&7Отвечает за восстановление", "&7положения игрока из шифта")),
    SHIFT_DOWN(new ItemStack(Material.DIAMOND_LEGGINGS),
            List.of("&7Отвечает за установку положения в шифте")),
    //INSIDE(ItemUtil.returnSkullTexture(new ItemStack(Material.DIRT), // like move, "?))
    LMB(new ItemStack(Material.BIRCH_BUTTON),
            List.of("&7Отвечает за Левый клик по чему угодно")),
    RMB(new ItemStack(Material.ACACIA_BUTTON),
            List.of("&7Отвечает за Правый клик по чему угодно")),
    COMMANDS(new ItemStack(Material.COMMAND_BLOCK),
            List.of("&7Отвечает за выполнение","&7любых команд в регионе")),
    BLOCK_PLACE(new ItemStack(Material.DIRT_PATH),
            List.of("&7Отвечает за постановку блоков в регионе")),
    BLOCK_BREAK(new ItemStack(Material.WOODEN_PICKAXE),
            List.of("&7Отвечает за ломание блоков в регионе")),
    /**
     * disable damage to any player
     */
    DAMAGE_PLAYER(new ItemStack(Material.DIAMOND_SWORD),
            List.of("&7Отвечает за урон игрока в регионе")),
    /**
     * disable damage to any entity
     */
    DAMAGE_AGGRESSIVE(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOThhNjg4NDQ5N2RlMTkxNTkyZWM0MmJkZGFlNTBlYmU3NWVkYmUyNDY4Yjk2ODczODI2MDk1MzBkNDE3MWExMSJ9fX0="),
            List.of("&7Отвечает за урон по","&7агрессивным монстрам в регионе")),
    DAMAGE_ANIMAL(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBmNTAzOTRjNmQ3ZGJjMDNlYTU5ZmRmNTA0MDIwZGM1ZDY1NDhmOWQzYmM5ZGNhYzg5NmJiNWNhMDg1ODdhIn19fQ=="),
            List.of("&7Отвечает за урон по","&7мирным монстрам в регионе")),
    TAKE_DAMAGE(new ItemStack(Material.SHIELD),
            List.of("&7Отвечает за полученный урон в регионе")),
    /**
     * prevent all damage from player
     */
    TAKE_DAMAGE_PLAYER(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOTNhMzk2MGM4Nzk0NzQwMTdjMGNhM2M4MGY2ZWU3M2NmODg2ZTAwZTg5YzkwMmEzZWU4OTNkZDI4NDk1MzVjMCJ9fX0="),
            List.of("&7Отвечает за полученный","&7урон от игрока в регионе")),

    /**
     * prevent damage from all collision blocks
     */

    TAKE_DAMAGE_FROM_BLOCK(new ItemStack(Material.IRON_BLOCK),
            List.of("&7Отвечает за урон от блоков (Гравий и т.п)")),
    /**
     * prevent damage from lava
     */
    TAKE_DAMAGE_FROM_LAVA(new ItemStack(Material.LAVA_BUCKET),
            List.of("&7Отвечает за урон от лавы")),


    /**
     * disable pickup all items
     */
    PICKUP_ITEMS(new ItemStack(Material.HOPPER),
            List.of("&7Отвечает за подъем предметов")),

    /**
     * disable drop any items
     */
    DROP_ITEMS(new ItemStack(Material.DISPENSER),
            List.of("&7Отвечает за выброс предметов")),
    HUNGER(new ItemStack(Material.BEEF),
            List.of("&7Отвечает за голод (отменяет его уменьшение)")),

    /**
     * regen HP
     */
    REGEN_HP(new ItemStack(Material.POTION),
            List.of("&7Отвечает за восстановление жизней в регионе")),
    /**
     * regen Hunger
     */
    REGEN_HUNGER(new ItemStack(Material.BEETROOT_SOUP),
            List.of("&7Отвечает за восстановление голода в регионе")),

    SPRINT(new ItemStack(Material.FEATHER),
            List.of("&7Отвечает за бег в регионе (@MOVE ивент выше)")),
    /**
     * we are cant block respawn, but this for Action section methods
     */

    RESPAWN(new ItemStack(Material.RED_BED),
            List.of("&7Отвечает за респавн в регионе (пока не работает)")),

    /**
     * disable collision
     */
    COLLISION(new ItemStack(Material.OAK_FENCE),
            List.of("&7Отвечает за коллизию в регионе")),

    /**
     * open chests
     */
    OPEN_CHEST(new ItemStack(Material.CHEST),
            List.of("&7Отвечает за открытие сундуков")),
    OPEN_ENDER_CHEST(new ItemStack(Material.ENDER_CHEST),
            List.of("&7Отвечает за открытие эндер сундука")),
    LEFT_USE(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkYWI3MjcxZjRmZjA0ZDU0NDAyMTkwNjdhMTA5YjVjMGMxZDFlMDFlYzYwMmMwMDIwNDc2ZjdlYjYxMjE4MCJ9fX0="),
            List.of("&7твечает за левый клик взаимодействия", "&7с предметами, который позволяют это (двери и т.п)")),
    RIGHT_USE(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmM2EyZGZjZTBjM2RhYjdlZTEwZGIzODVlNTIyOWYxYTM5NTM0YThiYTI2NDYxNzhlMzdjNGZhOTNiIn19fQ=="),
            List.of("&7твечает за правый клик взаимодействия", "&7с предметами, который позволяют это (двери и т.п)")),
    LEFT_USE_ON_SHIFT(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0="),
            List.of("&7твечает за левый клик на шифте взаимодействия", "&7с предметами, который позволяют это (двери и т.п)")),
    RIGHT_USE_ON_SHIFT(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjgyYWQxYjljYjRkZDIxMjU5YzBkNzVhYTMxNWZmMzg5YzNjZWY3NTJiZTM5NDkzMzgxNjRiYWM4NGE5NmUifX19"),
            List.of("&7твечает за левый клик на шифте взаимодействия", "&7с предметами, который позволяют это (двери и т.п)")),
    DEATH(ItemUtil.returnSkullTexture(new ItemStack(Material.PLAYER_HEAD), "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzdhZWU5YTc1YmYwZGY3ODk3MTgzMDE1Y2NhMGIyYTdkNzU1YzYzMzg4ZmYwMTc1MmQ1ZjQ0MTlmYzY0NSJ9fX0"),
            List.of("&7Отвечает за смерить в регионе (пока не работает)")),
    ;

    private final ItemStack item;
    private final List<String> lore;

    EventHandler(ItemStack item) {
        this.item = item;
        this.lore = List.of("&7Что-то делает, пока не добавили описание");
    }

    EventHandler(ItemStack item, List<String> lore) {
        this.item = item;
        this.lore = lore;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack getItem() {
        return item;
    }


    public String getName() {
        return RegionAPI.PLUGIN.getLangManager().getEnum(this);
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
            case DAMAGE_AGGRESSIVE -> new PlayerDamageAggressiveInRegionEvent(player, region);
            case DAMAGE_ANIMAL -> new PlayerDamageAnimalsInRegionEvent(player, region);
            case DAMAGE_PLAYER -> new PlayerDamagePlayerInRegionEvent(player, region);
            case TAKE_DAMAGE_PLAYER -> new PlayerTakeByPlayerDamageInRegionEvent(player, region);
            case HUNGER -> new PlayerHungerInRegionEvent(player, region);
            case LEFT_USE -> new PlayerLeftUseInRegionEvent(player, region);
            case RIGHT_USE -> new PlayerRightUseInRegionEvent(player, region);
            case LEFT_USE_ON_SHIFT -> new PlayerLeftUseOnShiftInRegionEvent(player, region);
            case RIGHT_USE_ON_SHIFT -> new PlayerRightUseOnShiftInRegionEvent(player, region);
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
//            Event originalEvent = t.me.p1azmer.api.EventHandler.callSyncAndJoin(getOriginalEvent(player, player.getLocation(), player.getLocation()));
//            if (originalEvent instanceof Cancellable cancellable) {
//                cancellable.setCancelled(cancel);
//                return event.isCancelled();
//            }
            return event.isCancelled();
        }
        return false;
    }

    public void callBlockRestore(Player player, Region region, Material type, long time, Material... materials) {
        callBlockRestore(player, region, type, time, Arrays.stream(materials).toList());
    }

    public void callBlockRestore(Player player, Region region, @Nullable Material type, long time, List<Material> materials) {
        if (materialsToRestore == null)
            materialsToRestore = new HashMap<>();
        materialsToRestore.put(region, new TripleReturn<>(materials, type, time));
//        t.me.p1azmer.api.EventHandler.callSync(new RestoreBlockEvent(player, region, materials));
    }

}
