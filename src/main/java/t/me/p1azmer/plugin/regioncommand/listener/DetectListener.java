package t.me.p1azmer.plugin.regioncommand.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.manager.AbstractListener;
import t.me.p1azmer.aves.engine.utils.PlayerUtil;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.plugin.regioncommand.Perm;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.cuboid.PlayerEnterCuboidEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.cuboid.PlayerLeaveCuboidEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.movement.PlayerEnterRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.movement.PlayerLeaveRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.movement.PlayerMoveInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;

public class DetectListener extends AbstractListener<RegPlugin> {

    private final RegionManager manager;

    public DetectListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }


    // register custom events (like enter cuboidRegion)

    @EventHandler
    public void detectCuboidEvent(@NotNull PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        Player player = event.getPlayer();

        Region toRegion = manager.getRegion(to);
        Region fromRegion = manager.getRegion(from);

        if (fromRegion == null && toRegion != null) {
            PlayerEnterCuboidEvent playerEnterCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterCuboidEvent(player, toRegion.getTerritory()));
            PlayerEnterRegionEvent playerEnterRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterRegionEvent(player, toRegion));
            if (playerEnterCuboidEvent.isCancelled() || playerEnterRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))
                    event.setCancelled(true);
                return;
            }
        } else if (fromRegion != null && toRegion == null) {
            PlayerLeaveCuboidEvent playerLeaveCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveCuboidEvent(player, fromRegion.getTerritory()));
            PlayerLeaveRegionEvent playerLeaveRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveRegionEvent(player, fromRegion));
            if (playerLeaveCuboidEvent.isCancelled() || playerLeaveRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))
                    event.setCancelled(true);
                return;
            }
        }
        if (fromRegion != null && toRegion != null) {
            PlayerMoveInRegionEvent toMove = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, fromRegion));
            PlayerMoveInRegionEvent fromMove = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, toRegion));
            if (toMove.isCancelled() || fromMove.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRegionEnter(@NotNull PlayerEnterRegionEvent event) {
        if (event.isCancelled()) return;

        Player player = (Player) event.getEntity();
        Region region = event.getRegion();
        ActiveRegion activeRegion = region.getActiveRegion();
        EventAction eventAction = activeRegion.getEventActionByEvent(Events.ENTER);
        if (eventAction != null) {
            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))
                if (eventAction.getPermission() != null && !player.hasPermission(eventAction.getPermission())) {
                    event.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void detectCuboidEventTP(@NotNull PlayerTeleportEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        Player player = event.getPlayer();

        Region toRegion = manager.getRegion(to);
        Region fromRegion = manager.getRegion(from);

        if (fromRegion == null && toRegion != null) {
            event.setCancelled(true);
                PlayerEnterCuboidEvent playerEnterCuboidEvent = t.me.p1azmer.api.Events.callAndReturn(new PlayerEnterCuboidEvent(player, toRegion.getTerritory()));
                PlayerEnterRegionEvent playerEnterRegionEvent = t.me.p1azmer.api.Events.callAndReturn(new PlayerEnterRegionEvent(player, toRegion));
                if (playerEnterCuboidEvent.isCancelled() || playerEnterRegionEvent.isCancelled()) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        player.teleport(from);
                    }
                }else
                    event.setCancelled(false);
        } else if (fromRegion != null && toRegion == null) {
            event.setCancelled(true);
            PlayerLeaveCuboidEvent playerLeaveCuboidEvent = t.me.p1azmer.api.Events.callAndReturn(new PlayerLeaveCuboidEvent(player, fromRegion.getTerritory()));
            PlayerLeaveRegionEvent playerLeaveRegionEvent = t.me.p1azmer.api.Events.callAndReturn(new PlayerLeaveRegionEvent(player, fromRegion));
            if (playerLeaveCuboidEvent.isCancelled() || playerLeaveRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.teleport(from);
                }
            }else
                event.setCancelled(false);
        }
    }


    // detect and cancelled spigot events

    @EventHandler
    public void onInteractWithoutRegion(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location from = player.getLocation();
        Location to = event.getInteractionPoint();

        Region fromRegion = manager.getRegion(from);
        Region toRegion = manager.getRegion(to);
        if (toRegion != null && fromRegion == null || toRegion != null && !fromRegion.getId().equals(toRegion.getId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLoginInRegion(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();
        Region region = manager.getRegion(location);
        if (region != null) {
            PlayerEnterRegionEvent enterRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterRegionEvent(player, region));
            if (enterRegionEvent.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                PlayerUtil.dispatchCommand(player, "spawn");
                player.sendMessage(StringUtil.color("&cВозможно, вы могли застрять в этом регионе. Мы принудительно телепортировали вас на спавн, чтобы избежать этого.\nЕсли это ошибка, сообщите администрации код: #PZ-RGCMD-LOGIN_EVENT_SAFE_REGION-" + region.getId()));
            }
        }
    }

    @EventHandler
    public void onMoveInCancelledRegion(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        Region fromRegion = manager.getRegion(from);
        Region toRegion = manager.getRegion(to);

        if (fromRegion != null && toRegion != null) {
            PlayerMoveInRegionEvent moveFrom = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, fromRegion));
            PlayerMoveInRegionEvent moveTo = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, toRegion));
            if (moveFrom.isCancelled() && moveTo.isCancelled() || event.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                PlayerUtil.dispatchCommand(player, "spawn");
                player.sendMessage(StringUtil.color("&cВозможно, вы могли застрять в этом регионе. Мы принудительно телепортировали вас на спавн, чтобы избежать этого.\nЕсли это ошибка, сообщите администрации код: #PZ-RGCMD-MOVE_EVENT_SAFE_REGION-" + toRegion.getId()));
            }
        }
    }


}

