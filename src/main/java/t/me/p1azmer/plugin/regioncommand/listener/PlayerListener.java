package t.me.p1azmer.plugin.regioncommand.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.util.Cooldown;
import t.me.p1azmer.api.util.TimeUtil;
import t.me.p1azmer.aves.engine.api.manager.AbstractListener;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.plugin.regioncommand.Perm;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockBreakInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.block.PlayerBlockPlaceInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.movement.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.player.*;
import t.me.p1azmer.plugin.regioncommand.api.events.region.use.*;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;

import static t.me.p1azmer.plugin.regioncommand.api.type.EventHandler.*;

public class PlayerListener extends AbstractListener<RegPlugin> { // TODO: RECODE FOR NEW COOLDOWNER AND REPLACE PLAYER TO LIVING ENTITY
    //FIXME - Изменить отмену ивентов. Пример брать из @onRegenHp

    private final RegionManager manager;

    public PlayerListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(@NotNull PlayerMoveEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                PlayerMoveInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }

                ActiveRegion activeRegion = region.getActiveRegion();
                EventAction eventAction = activeRegion.getEventActionByEvent(MOVE);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Move).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + MOVE.name(), time)) {
                            if (eventAction.getLangMessage() != null) {
                                eventAction.getLangMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + MOVE.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (MOVE.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                int time = activeRegion.getCooldowns().getOrDefault(MOVE, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + MOVE.name(), time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Move)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Move).send(player); // message for event
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(@NotNull BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                PlayerBlockPlaceInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerBlockPlaceInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }

                ActiveRegion activeRegion = region.getActiveRegion();
                EventAction eventAction = activeRegion.getEventActionByEvent(BLOCK_PLACE);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Place).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + BLOCK_PLACE.name(), time)) {
                            if (eventAction.getLangMessage() != null) {
                                eventAction.getLangMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + BLOCK_PLACE.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (BLOCK_PLACE.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(BLOCK_PLACE, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(BLOCK_PLACE, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + BLOCK_PLACE.name(), time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Place)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Place).send(player); // message for event
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(@NotNull BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(BLOCK_BREAK);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Break).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + BLOCK_BREAK.name(), time)) {
                            if (eventAction.getLangMessage() != null) {
                                eventAction.getLangMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + BLOCK_BREAK.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (BLOCK_BREAK.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(BLOCK_BREAK, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(BLOCK_BREAK, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + BLOCK_BREAK.name(), time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Break)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                PlayerBlockBreakInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerBlockBreakInRegionEvent(player, region, event.getBlock()));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                }
                plugin.getMessage(Lang.Events_Break).send(player); // message for event
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnter(@NotNull PlayerEnterRegionEvent event) {
        if (event.isCancelled()) return;
        Player player = (Player) event.getEntity();

        Region region = event.getRegion();
        ActiveRegion activeRegion = region.getActiveRegion();

        EventAction eventAction = activeRegion.getEventActionByEvent(ENTER);
        if (eventAction != null) {
            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                JPermission permission = eventAction.getPermission();
                if (permission != null && !player.hasPermission(permission)) {
                    plugin.getMessage(Lang.Permission_Event_Enter).send(player);
                    event.setCancelled(true);
                    return;
                }
            }
            int time = eventAction.getCooldown();
            if (time > 0)
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + ENTER.name(), time)) {
                    if (eventAction.getLangMessage() != null) {
                        eventAction.getLangMessage()
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                    }
                    event.setCancelled(true);
                    return;
                }
            eventAction.getManipulator().processAll(player);

            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + ENTER.name();
            eventAction.getManipulator().replace(s -> s
                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
            );

            if (ENTER.cancelledEvents.contains(player)) {
                event.setCancelled(true);
                return;
            }
            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                event.setCancelled(true);
                return;
            }
        }

        boolean cancelled = activeRegion.getCancelled().getOrDefault(ENTER, false);
        event.setCancelled(cancelled);

        int time = activeRegion.getCooldowns().getOrDefault(ENTER, -1);
        if (time > 0) {
            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + ENTER.name(), time)) {
                plugin.getMessage(Lang.Cooldown_Event_Enter)
                        .replace("%time%", time)
                        .replace("%time_correct%", TimeUtil.leftTime(time))
                        .send(player);
                event.setCancelled(true);
                return;
            }
        }

        plugin.getMessage(Lang.Events_Enter).send(player); // message for event
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(@NotNull PlayerLeaveRegionEvent event) {
        if (event.isCancelled()) return;
        Player player = (Player) event.getEntity();

        Region region = event.getRegion();
        ActiveRegion activeRegion = region.getActiveRegion();

        EventAction eventAction = activeRegion.getEventActionByEvent(LEAVE);
        if (eventAction != null) {
            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                JPermission permission = eventAction.getPermission();
                if (permission != null && !player.hasPermission(permission)) {
                    plugin.getMessage(Lang.Permission_Event_Leave).send(player);
                    event.setCancelled(true);
                    return;
                }
            }

            int time = eventAction.getCooldown();
            if (time > 0)
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + LEAVE.name(), time)) {
                    if (eventAction.getLangMessage() != null) {
                        eventAction.getLangMessage()
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                    }
                    event.setCancelled(true);
                    return;
                }
            eventAction.getManipulator().processAll(player);

            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + LEAVE.name();
            eventAction.getManipulator().replace(s -> s
                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
            );

            if (LEAVE.cancelledEvents.contains(player)) {
                event.setCancelled(true);
                return;
            }
            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                event.setCancelled(true);
                return;
            }
        }

        boolean cancelled = activeRegion.getCancelled().getOrDefault(LEAVE, false);
        event.setCancelled(cancelled);

        int time = activeRegion.getCooldowns().getOrDefault(LEAVE, -1);
        if (time > 0) {
            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + LEAVE.name(), time)) {
                plugin.getMessage(Lang.Cooldown_Event_Leave)
                        .replace("%time%", time)
                        .replace("%time_correct%", TimeUtil.leftTime(time))
                        .send(player);
                event.setCancelled(true);
                return;
            }
        }

        plugin.getMessage(Lang.Events_Leave).send(player); // message for event
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onClick(@NotNull PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (this.manager.inRegion(player) && this.manager.inRegion(event.getInteractionPoint())) {
            switch (action) {
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                    Region region = this.manager.getRegion(player);
                    if (region != null) {

                        if (LMB.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }

                        PlayerLMBInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLMBInRegionEvent(player, region));
                        if (customEventCaller.isCancelled()) {
                            return;
                        }
                        ActiveRegion activeRegion = region.getActiveRegion();

                        EventAction eventAction = activeRegion.getEventActionByEvent(LMB);
                        if (eventAction != null) {
                            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                JPermission permission = eventAction.getPermission();
                                if (permission != null && !player.hasPermission(permission)) {
                                    plugin.getMessage(Lang.Permission_Event_LMB).send(player);
                                    return;
                                }
                            }
                            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                return;
                            }
                            int time = eventAction.getCooldown();
                            if (time > 0)
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + LMB.name(), time)) {
                                    if (eventAction.getLangMessage() != null) {
                                        eventAction.getLangMessage()
                                                .replace("%time%", time)
                                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                                .send(player);
                                    }
                                    event.setCancelled(true);
                                    return;
                                }
                            eventAction.getManipulator().processAll(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + LMB.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (LMB.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                                return;
                            }
                            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        boolean cancelled = activeRegion.getCancelled().getOrDefault(LMB, false);
                        event.setCancelled(cancelled);

                        int time = activeRegion.getCooldowns().getOrDefault(LMB, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + LMB.name(), time)) {
                                plugin.getMessage(Lang.Cooldown_Event_LMB)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        plugin.getMessage(Lang.Events_LMB).send(player); // message for event
                    }
                }
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                    Region region = this.manager.getRegion(player);
                    if (region != null) {

                        if (RMB.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }

                        PlayerRMBInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRMBInRegionEvent(player, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                        ActiveRegion activeRegion = region.getActiveRegion();

                        EventAction eventAction = activeRegion.getEventActionByEvent(RMB);
                        if (eventAction != null) {
                            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                JPermission permission = eventAction.getPermission();
                                if (permission != null && !player.hasPermission(permission)) {
                                    plugin.getMessage(Lang.Permission_Event_RMB).send(player);
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                                return;
                            }

                            int time = eventAction.getCooldown();
                            if (time > 0)
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + RMB.name(), time)) {
                                    if (eventAction.getLangMessage() != null) {
                                        eventAction.getLangMessage()
                                                .replace("%time%", time)
                                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                                .send(player);
                                    }
                                    event.setCancelled(true);
                                    return;
                                }
                            eventAction.getManipulator().processAll(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + RMB.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (RMB.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                                return;
                            }
                            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        boolean cancelled = activeRegion.getCancelled().getOrDefault(RMB, false);
                        event.setCancelled(cancelled);

                        int time = activeRegion.getCooldowns().getOrDefault(RMB, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + RMB.name(), time)) {
                                plugin.getMessage(Lang.Cooldown_Event_RMB)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        plugin.getMessage(Lang.Events_RMB).send(player); // message for event
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onShift(@NotNull PlayerToggleSneakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            if (this.manager.inRegion(player)) {
                Region region = this.manager.getRegion(player);
                if (region != null) {

                    PlayerShiftUPInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerShiftUPInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    ActiveRegion activeRegion = region.getActiveRegion();

                    EventAction eventAction = activeRegion.getEventActionByEvent(SHIFT_UP);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                plugin.getMessage(Lang.Permission_Event_Shift_Up).send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }
                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + SHIFT_UP.name(), time)) {
                                if (eventAction.getLangMessage() != null) {
                                    eventAction.getLangMessage()
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                }
                                event.setCancelled(true);
                                return;
                            }
                        eventAction.getManipulator().processAll(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + SHIFT_UP.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (SHIFT_UP.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                        }

                        if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(SHIFT_UP, false);
                    event.setCancelled(cancelled);

                    int time = activeRegion.getCooldowns().getOrDefault(SHIFT_UP, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + SHIFT_UP.name(), time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Shift_Up)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    plugin.getMessage(Lang.Events_Shift_Up).send(player); // message for event
                }
            }
        } else {
            if (this.manager.inRegion(player)) {
                Region region = this.manager.getRegion(player);
                if (region != null) {

                    PlayerShiftDOWNInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerShiftDOWNInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    ActiveRegion activeRegion = region.getActiveRegion();

                    EventAction eventAction = activeRegion.getEventActionByEvent(SHIFT_DOWN);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                plugin.getMessage(Lang.Permission_Event_Shift_Down).send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }
                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + SHIFT_DOWN.name(), time)) {
                                if (eventAction.getLangMessage() != null) {
                                    eventAction.getLangMessage()
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                }
                                event.setCancelled(true);
                                return;
                            }
                        eventAction.getManipulator().processAll(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + SHIFT_DOWN.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (SHIFT_DOWN.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                        }
                        if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(SHIFT_DOWN, false);
                    event.setCancelled(cancelled);

                    int time = activeRegion.getCooldowns().getOrDefault(SHIFT_DOWN, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + SHIFT_DOWN.name(), time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Shift_Down)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    plugin.getMessage(Lang.Events_Shift_Down).send(player); // message for event
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJump(@NotNull PlayerJumpEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {

            Region region = this.manager.getRegion(player);
            if (region != null) {
                PlayerJumpInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerJumpInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(JUMP);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Jump).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + JUMP.name(), time)) {
                            if (eventAction.getLangMessage() != null) {
                                eventAction.getLangMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + JUMP.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (JUMP.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(JUMP, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(JUMP, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + JUMP.name(), time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Jump)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Jump).send(player); // message for event
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {


            Region region = this.manager.getRegion(player);
            if (region != null) {
                PlayerCommandInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerCommandInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(COMMANDS);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Commands).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + COMMANDS.name(), time)) {
                            if (eventAction.getLangMessage() != null) {
                                eventAction.getLangMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + COMMANDS.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (COMMANDS.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(COMMANDS, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(COMMANDS, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + COMMANDS.name(), time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Commands)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Commands).send(player); // message for event
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player player) {
            if (this.manager.inRegion(player)) {

                Region region = this.manager.getRegion(player);
                if (region != null) {
                    PlayerHungerInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerHungerInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }
                    ActiveRegion activeRegion = region.getActiveRegion();

                    EventAction eventAction = activeRegion.getEventActionByEvent(HUNGER);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                //plugin.getMessage(Lang.Permission_Event_Hunger).send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }
                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + HUNGER.name(), time)) {
                                if (eventAction.getLangMessage() != null) {
                                    eventAction.getLangMessage()
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                }
                                event.setCancelled(true);
                                return;
                            }
                        eventAction.getManipulator().processAll(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + HUNGER.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (HUNGER.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(HUNGER, false);
                    event.setCancelled(cancelled);

                    int time = activeRegion.getCooldowns().getOrDefault(HUNGER, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + HUNGER.name(), time)) {
//                            plugin.getMessage(Lang.Cooldown_Event_Hunger)
//                                    .replace("%time%", time)
//                                    .replace("%time_correct%", TimeUtil.leftTime(time))
//                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    //plugin.getMessage(Lang.Events_Hunger).send(player); // message for event
                }
            }
        }
    }

    @EventHandler
    public void onSprint(PlayerToggleSprintEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(SPRINT);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            if (eventAction.getPermissionDenyMessage() != null)
                                eventAction.getPermissionDenyMessage().send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + SPRINT.name(), time)) {
                            if (eventAction.getCooldownMessage() != null) {
                                eventAction.getCooldownMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + SPRINT.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (SPRINT.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(SPRINT, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(SPRINT, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + SPRINT.name(), time)) {
                        // TODO message
                        event.setCancelled(true);
                        return;
                    }
                }

                PlayerSprintInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerSprintInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                }
                if (eventAction != null && eventAction.getActionMessage() != null)
                    eventAction.getActionMessage().send(player);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(DEATH);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            if (eventAction.getPermissionDenyMessage() != null)
                                eventAction.getPermissionDenyMessage().send(player);
                            event.setCancelled(true); // no permission for die? ;c
                            return;
                        }
                    }

                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + DEATH.name(), time)) {
                            if (eventAction.getCooldownMessage() != null) {
                                eventAction.getCooldownMessage()
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                            }
                            event.setCancelled(true);
                            return;
                        }
                    eventAction.getManipulator().processAll(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + DEATH.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (DEATH.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(DEATH, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(DEATH, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + DEATH.name(), time)) {
                        // TODO message
                        event.setCancelled(true);
                        return;
                    }
                }

                PlayerDeathInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerDeathInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                }
                if (eventAction != null && eventAction.getActionMessage() != null)
                    eventAction.getActionMessage().send(player);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(RESPAWN);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            if (eventAction.getPermissionDenyMessage() != null)
                                eventAction.getPermissionDenyMessage().send(player);
                            return;
                        }
                    }
                    eventAction.getManipulator().processAll(player);
                }

                t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRespawnInRegionEvent(player, region));
                if (eventAction != null && eventAction.getActionMessage() != null)
                    eventAction.getActionMessage().send(player);
            }
        }
    }

    @EventHandler
    public void onRegenHp(EntityRegainHealthEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player player) {
            if (this.manager.inRegion(player)) {
                Region region = this.manager.getRegion(player);
                if (region != null) {
                    ActiveRegion activeRegion = region.getActiveRegion();

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(REGEN_HP, false);

                    EventAction eventAction = activeRegion.getEventActionByEvent(REGEN_HP);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                if (eventAction.getPermissionDenyMessage() != null)
                                    eventAction.getPermissionDenyMessage().send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + REGEN_HP.name(), time)) {
                                if (eventAction.getCooldownMessage() != null) {
                                    eventAction.getCooldownMessage()
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                }
                                event.setCancelled(true);
                                return;
                            }
                        eventAction.getManipulator().processAll(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + REGEN_HP.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        cancelled = REGEN_HP.cancelledEvents.contains(player);

                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))
                            if (eventAction.isCancelled()) {
                                cancelled = true;
                            }
                    }

                    int time = activeRegion.getCooldowns().getOrDefault(REGEN_HP, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + REGEN_HP.name(), time)) {
                            // TODO message
                            event.setCancelled(true);
                            return;
                        }
                    }

                    PlayerRegenHPInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRegenHPInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        cancelled = true;
                    }
                    if (eventAction != null && eventAction.getActionMessage() != null)
                        eventAction.getActionMessage().send(player);
                    event.setCancelled(cancelled);
                }
            }
        }
    }

    @EventHandler
    public void onRegenHunger(FoodLevelChangeEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity() instanceof Player player) {
            if (this.manager.inRegion(player)) {
                Region region = this.manager.getRegion(player);
                if (region != null) {
                    ActiveRegion activeRegion = region.getActiveRegion();

                    EventAction eventAction = activeRegion.getEventActionByEvent(REGEN_HUNGER);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                if (eventAction.getPermissionDenyMessage() != null)
                                    eventAction.getPermissionDenyMessage().send(player);
                                event.setCancelled(true); // no permission for die? ;c
                                return;
                            }
                        }

                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + REGEN_HUNGER.name(), time)) {
                                if (eventAction.getCooldownMessage() != null) {
                                    eventAction.getCooldownMessage()
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                }
                                event.setCancelled(true);
                                return;
                            }
                        eventAction.getManipulator().processAll(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + REGEN_HUNGER.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (REGEN_HUNGER.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                        }
                        if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                            event.setCancelled(true);
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(REGEN_HUNGER, false);
                    event.setCancelled(cancelled);


                    int time = activeRegion.getCooldowns().getOrDefault(REGEN_HUNGER, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + REGEN_HUNGER.name(), time)) {
                            // TODO message
                            event.setCancelled(true);
                            return;
                        }
                    }

                    PlayerRegenHungerInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRegenHungerInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        event.setCancelled(true);
                    }
                    if (eventAction != null && eventAction.getActionMessage() != null)
                        eventAction.getActionMessage().send(player);
                }
            }
        }
    }

    /**
     * cast for @EventHandler.USE, EventHandler.OPEN_CHEST, EventHandler.OPEN_ENDER_CHEST
     */
    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getClickedBlock() != null) {
            Block block = event.getClickedBlock();
            if (block.getType().isInteractable()) {
                if (this.manager.inRegion(player)) {
                    Region region = this.manager.getRegion(player);
                    if (region != null) {

                        ActiveRegion activeRegion = region.getActiveRegion();

                        if (block.getType().equals(Material.CHEST) || block.getType().equals(Material.CHEST_MINECART) || block.getType().equals(Material.TRAPPED_CHEST)) {

                            EventAction eventAction = activeRegion.getEventActionByEvent(OPEN_CHEST);
                            if (eventAction != null) {
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        if (eventAction.getPermissionDenyMessage() != null)
                                            eventAction.getPermissionDenyMessage().send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }

                                int time = eventAction.getCooldown();
                                if (time > 0)
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + OPEN_CHEST.name(), time)) {
                                        if (eventAction.getCooldownMessage() != null) {
                                            eventAction.getCooldownMessage()
                                                    .replace("%time%", time)
                                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                                    .send(player);
                                        }
                                        event.setCancelled(true);
                                        return;
                                    }
                                eventAction.getManipulator().processAll(player);

                                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + OPEN_CHEST.name();
                                eventAction.getManipulator().replace(s -> s
                                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                );

                                if (OPEN_CHEST.cancelledEvents.contains(player)) {
                                    event.setCancelled(true);
                                }
                                if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                    event.setCancelled(true);
                                }
                            }

                            boolean cancelled = activeRegion.getCancelled().getOrDefault(OPEN_CHEST, false);
                            event.setCancelled(cancelled);

                            int time = activeRegion.getCooldowns().getOrDefault(OPEN_CHEST, -1);
                            if (time > 0) {
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + OPEN_CHEST.name(), time)) {
                                    // TODO message
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            PlayerOpenChestInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerOpenChestInRegionEvent(player, region));
                            if (customEventCaller.isCancelled()) {
                                event.setCancelled(true);
                            }
                            if (eventAction != null && eventAction.getActionMessage() != null)
                                eventAction.getActionMessage().send(player);
                            if (customEventCaller.isCancelled() || event.useInteractedBlock().equals(Event.Result.DENY) || event.useItemInHand().equals(Event.Result.DENY))
                                return;
                        }
                        if (block.getType().equals(Material.ENDER_CHEST)) {

                            EventAction eventAction = activeRegion.getEventActionByEvent(OPEN_ENDER_CHEST);
                            if (eventAction != null) {
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        if (eventAction.getPermissionDenyMessage() != null)
                                            eventAction.getPermissionDenyMessage().send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }

                                int time = eventAction.getCooldown();
                                if (time > 0)
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + OPEN_ENDER_CHEST.name(), time)) {
                                        if (eventAction.getCooldownMessage() != null) {
                                            eventAction.getCooldownMessage()
                                                    .replace("%time%", time)
                                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                                    .send(player);
                                        }
                                        event.setCancelled(true);
                                        return;
                                    }
                                eventAction.getManipulator().processAll(player);

                                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + OPEN_ENDER_CHEST.name();
                                eventAction.getManipulator().replace(s -> s
                                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                );

                                if (OPEN_ENDER_CHEST.cancelledEvents.contains(player)) {
                                    event.setCancelled(true);
                                }
                                if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                    event.setCancelled(true);
                                }
                            }

                            boolean cancelled = activeRegion.getCancelled().getOrDefault(OPEN_ENDER_CHEST, false);
                            event.setCancelled(cancelled);
                            if (cancelled) {
                                event.setCancelled(true);
                            }

                            int time = activeRegion.getCooldowns().getOrDefault(OPEN_ENDER_CHEST, -1);
                            if (time > 0) {
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + OPEN_ENDER_CHEST.name(), time)) {
                                    // TODO message
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            PlayerOpenEnderChestInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerOpenEnderChestInRegionEvent(player, region));
                            if (customEventCaller.isCancelled()) {
                                event.setCancelled(true);
                            }
                            if (eventAction != null && eventAction.getActionMessage() != null)
                                eventAction.getActionMessage().send(player);
                            if (customEventCaller.isCancelled() || event.useInteractedBlock().equals(Event.Result.DENY) || event.useItemInHand().equals(Event.Result.DENY))
                                return;
                        }
                        if (player.isSneaking()) {
                            if (event.getAction().isLeftClick()) {

                                EventAction eventAction = activeRegion.getEventActionByEvent(LEFT_USE_ON_SHIFT);
                                if (eventAction != null) {
                                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                        JPermission permission = eventAction.getPermission();
                                        if (permission != null && !player.hasPermission(permission)) {
                                            if (eventAction.getPermissionDenyMessage() != null)
                                                eventAction.getPermissionDenyMessage().send(player);
                                            event.setUseInteractedBlock(Event.Result.DENY);
                                            event.setUseItemInHand(Event.Result.DENY);
                                            return;
                                        }
                                    }

                                    int time = eventAction.getCooldown();
                                    if (time > 0)
                                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + LEFT_USE_ON_SHIFT.name(), time)) {
                                            if (eventAction.getCooldownMessage() != null) {
                                                eventAction.getCooldownMessage()
                                                        .replace("%time%", time)
                                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                                        .send(player);
                                            }
                                            event.setUseInteractedBlock(Event.Result.DENY);
                                            event.setUseItemInHand(Event.Result.DENY);
                                            return;
                                        }
                                    eventAction.getManipulator().processAll(player);

                                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + LEFT_USE_ON_SHIFT.name();
                                    eventAction.getManipulator().replace(s -> s
                                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                    );

                                    if (LEFT_USE_ON_SHIFT.cancelledEvents.contains(player)) {
                                        event.setCancelled(true);
                                    }
                                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                        event.setCancelled(true);
                                    }
                                }

                                boolean cancelled = activeRegion.getCancelled().getOrDefault(LEFT_USE_ON_SHIFT, false);
                                event.setCancelled(cancelled);
                                if (cancelled) {
                                    event.setCancelled(true);
                                }

                                int time = activeRegion.getCooldowns().getOrDefault(LEFT_USE_ON_SHIFT, -1);
                                if (time > 0) {
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + LEFT_USE_ON_SHIFT.name(), time)) {
                                        // TODO message
                                        event.setCancelled(true);
                                        return;
                                    }
                                }

                                PlayerLeftUseOnShiftInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeftUseOnShiftInRegionEvent(player, region));
                                if (customEventCaller.isCancelled()) {
                                    event.setCancelled(true);
                                }
                                if (eventAction != null && eventAction.getActionMessage() != null)
                                    eventAction.getActionMessage().send(player);
                                if (customEventCaller.isCancelled() || event.useInteractedBlock().equals(Event.Result.DENY) || event.useItemInHand().equals(Event.Result.DENY))
                                    return;
                            } else {
                                EventAction eventAction = activeRegion.getEventActionByEvent(RIGHT_USE_ON_SHIFT);
                                if (eventAction != null) {
                                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                        JPermission permission = eventAction.getPermission();
                                        if (permission != null && !player.hasPermission(permission)) {
                                            if (eventAction.getPermissionDenyMessage() != null)
                                                eventAction.getPermissionDenyMessage().send(player);
                                            event.setUseInteractedBlock(Event.Result.DENY);
                                            event.setUseItemInHand(Event.Result.DENY);
                                            return;
                                        }
                                    }

                                    int time = eventAction.getCooldown();
                                    if (time > 0)
                                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + RIGHT_USE_ON_SHIFT.name(), time)) {
                                            if (eventAction.getCooldownMessage() != null) {
                                                eventAction.getCooldownMessage()
                                                        .replace("%time%", time)
                                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                                        .send(player);
                                            }
                                            event.setUseInteractedBlock(Event.Result.DENY);
                                            event.setUseItemInHand(Event.Result.DENY);
                                            return;
                                        }
                                    eventAction.getManipulator().processAll(player);

                                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + RIGHT_USE_ON_SHIFT.name();
                                    eventAction.getManipulator().replace(s -> s
                                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                    );

                                    if (RIGHT_USE_ON_SHIFT.cancelledEvents.contains(player)) {
                                        event.setCancelled(true);
                                    }
                                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                        event.setCancelled(true);
                                    }
                                }

                                boolean cancelled = activeRegion.getCancelled().getOrDefault(RIGHT_USE_ON_SHIFT, false);
                                event.setCancelled(cancelled);
                                if (cancelled) {
                                    event.setCancelled(true);
                                }

                                int time = activeRegion.getCooldowns().getOrDefault(RIGHT_USE_ON_SHIFT, -1);
                                if (time > 0) {
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + RIGHT_USE_ON_SHIFT.name(), time)) {
                                        // TODO message
                                        event.setCancelled(true);
                                        return;
                                    }
                                }

                                PlayerRightUseOnShiftInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRightUseOnShiftInRegionEvent(player, region));
                                if (customEventCaller.isCancelled()) {
                                    event.setCancelled(true);
                                }
                                if (eventAction != null && eventAction.getActionMessage() != null)
                                    eventAction.getActionMessage().send(player);
                                if (customEventCaller.isCancelled() || event.useInteractedBlock().equals(Event.Result.DENY) || event.useItemInHand().equals(Event.Result.DENY))
                                    return;
                            }
                        }
                /*
                   ------------------------------------------------------------------------------------------------------------
                    Для начала, мы проверяем все другие ивенты взаимодействия, а только потом обычный ивент взаимодействия
                   ------------------------------------------------------------------------------------------------------------
                 */
                        if (event.getAction().isLeftClick()) {
                            EventAction eventAction = activeRegion.getEventActionByEvent(LEFT_USE);
                            if (eventAction != null) {
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        if (eventAction.getPermissionDenyMessage() != null)
                                            eventAction.getPermissionDenyMessage().send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }

                                int time = eventAction.getCooldown();
                                if (time > 0)
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + LEFT_USE.name(), time)) {
                                        if (eventAction.getCooldownMessage() != null) {
                                            eventAction.getCooldownMessage()
                                                    .replace("%time%", time)
                                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                                    .send(player);
                                        }
                                        event.setCancelled(true);
                                        return;
                                    }
                                eventAction.getManipulator().processAll(player);

                                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + LEFT_USE.name();
                                eventAction.getManipulator().replace(s -> s
                                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                );

                                if (LEFT_USE.cancelledEvents.contains(player)) {
                                    event.setCancelled(true);
                                }
                                if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                    event.setCancelled(true);
                                }
                            }

                            boolean cancelled = activeRegion.getCancelled().getOrDefault(LEFT_USE, false);
                            event.setCancelled(cancelled);
                            if (cancelled) {
                                event.setCancelled(true);
                            }

                            int time = activeRegion.getCooldowns().getOrDefault(LEFT_USE, -1);
                            if (time > 0) {
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + LEFT_USE.name(), time)) {
                                    // TODO message
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            PlayerLeftUseInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeftUseInRegionEvent(player, region));
                            if (customEventCaller.isCancelled()) {
                                event.setCancelled(true);
                            }
                            if (eventAction != null && eventAction.getActionMessage() != null)
                                eventAction.getActionMessage().send(player);
                            if (customEventCaller.isCancelled() || event.useInteractedBlock().equals(Event.Result.DENY) || event.useItemInHand().equals(Event.Result.DENY))
                                return;
                        }
                        EventAction eventAction = activeRegion.getEventActionByEvent(RIGHT_USE);
                        if (eventAction != null) {
                            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                JPermission permission = eventAction.getPermission();
                                if (permission != null && !player.hasPermission(permission)) {
                                    if (eventAction.getPermissionDenyMessage() != null)
                                        eventAction.getPermissionDenyMessage().send(player);
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            int time = eventAction.getCooldown();
                            if (time > 0)
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + RIGHT_USE.name(), time)) {
                                    if (eventAction.getCooldownMessage() != null) {
                                        eventAction.getCooldownMessage()
                                                .replace("%time%", time)
                                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                                .send(player);
                                    }
                                    event.setCancelled(true);
                                    return;
                                }
                            eventAction.getManipulator().processAll(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + RIGHT_USE.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (RIGHT_USE.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                            }
                            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                            }
                        }

                        boolean cancelled = activeRegion.getCancelled().getOrDefault(RIGHT_USE, false);
                        event.setCancelled(cancelled);

                        int time = activeRegion.getCooldowns().getOrDefault(RIGHT_USE, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_" + RIGHT_USE.name(), time)) {
                                // TODO message
                                event.setCancelled(true);
                                return;
                            }
                        }

                        PlayerRightUseInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRightUseInRegionEvent(player, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                        }
                        if (eventAction != null && eventAction.getActionMessage() != null)
                            eventAction.getActionMessage().send(player);
                    }
                }
            }
        }
    }
}