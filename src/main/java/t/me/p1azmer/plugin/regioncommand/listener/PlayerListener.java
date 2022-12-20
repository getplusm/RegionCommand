package t.me.p1azmer.plugin.regioncommand.listener;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
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
import t.me.p1azmer.plugin.regioncommand.api.events.region.*;
import t.me.p1azmer.plugin.regioncommand.data.Lang;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;

import static t.me.p1azmer.plugin.regioncommand.api.type.Events.*;

public class PlayerListener extends AbstractListener<RegPlugin> {

    private final RegionManager manager;

    public PlayerListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler
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
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_MOVE", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + MOVE.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (MOVE.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                }

                int time = activeRegion.getCooldowns().getOrDefault(MOVE, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_MOVE", time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Move)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Move).send(player); // message for event
            }
        }
    }

    @EventHandler
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
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_PLACE", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + BLOCK_PLACE.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (BLOCK_PLACE.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(BLOCK_PLACE, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(BLOCK_PLACE, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_PLACE", time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Place)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Place).send(player); // message for event
            }
        }
    }

    @EventHandler
    public void onBreak(@NotNull BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {

                PlayerBlockBreakInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerBlockBreakInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }

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
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_BREAK", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + BLOCK_BREAK.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (BLOCK_BREAK.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(BLOCK_BREAK, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(BLOCK_BREAK, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_BREAK", time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Break)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Break).send(player); // message for event
            }
        }
    }

    @EventHandler
    public void onEnter(@NotNull PlayerEnterRegionEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (event.getRegion() != null) {

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
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_ENTER", time)) {
                    if (eventAction.getLangKey() != null)
                        plugin.getMessage(eventAction.getLangKey())
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                    return;
                }
                eventAction.getManipulator().process(player);

                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + ENTER.name();
                eventAction.getManipulator().replace(s -> s
                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                );

                if (ENTER.cancelledEvents.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
            }

            boolean cancelled = activeRegion.getCancelled().getOrDefault(ENTER, false);
            event.setCancelled(cancelled);

            int time = activeRegion.getCooldowns().getOrDefault(ENTER, -1);
            if (time > 0) {
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ENTER", time)) {
                    plugin.getMessage(Lang.Cooldown_Event_Enter)
                            .replace("%time%", time)
                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                            .send(player);
                    event.setCancelled(true);
                    return;
                }
            }

            plugin.getMessage(Lang.Events_Enter).send(player); // message for event
        }
    }

    @EventHandler
    public void onLeave(@NotNull PlayerLeaveRegionEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        if (event.getRegion() != null) {

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
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_LEAVE", time)) {
                    if (eventAction.getLangKey() != null)
                        plugin.getMessage(eventAction.getLangKey())
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                    return;
                }
                eventAction.getManipulator().process(player);

                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + LEAVE.name();
                eventAction.getManipulator().replace(s -> s
                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                );

                if (LEAVE.cancelledEvents.contains(player)) {
                    event.setCancelled(true);
                    return;
                }
            }

            boolean cancelled = activeRegion.getCancelled().getOrDefault(LEAVE, false);
            event.setCancelled(cancelled);

            int time = activeRegion.getCooldowns().getOrDefault(LEAVE, -1);
            if (time > 0) {
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_LEAVE", time)) {
                    plugin.getMessage(Lang.Cooldown_Event_Leave)
                            .replace("%time%", time)
                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                            .send(player);
                    event.setCancelled(true);
                    return;
                }
            }

            plugin.getMessage(Lang.Events_Leave).send(player); // message for event
        }
    }

    @EventHandler
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
                            event.setCancelled(true);
                            return;
                        }
                        ActiveRegion activeRegion = region.getActiveRegion();

                        EventAction eventAction = activeRegion.getEventActionByEvent(LMB);
                        if (eventAction != null) {
                            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                JPermission permission = eventAction.getPermission();
                                if (permission != null && !player.hasPermission(permission)) {
                                    plugin.getMessage(Lang.Permission_Event_LMB).send(player);
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                            int time = eventAction.getCooldown();
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_LMB", time)) {
                                if (eventAction.getLangKey() != null)
                                    plugin.getMessage(eventAction.getLangKey())
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                            .send(player);
                                return;
                            }
                            eventAction.getManipulator().process(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + LMB.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (LMB.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        boolean cancelled = activeRegion.getCancelled().getOrDefault(LMB, false);
                        event.setCancelled(cancelled);

                        int time = activeRegion.getCooldowns().getOrDefault(LMB, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_LMB", time)) {
                                plugin.getMessage(Lang.Cooldown_Event_LMB)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
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
                            int time = eventAction.getCooldown();
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_RMB", time)) {
                                if (eventAction.getLangKey() != null)
                                    plugin.getMessage(eventAction.getLangKey())
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                            .send(player);
                                return;
                            }
                            eventAction.getManipulator().process(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + RMB.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (RMB.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        boolean cancelled = activeRegion.getCancelled().getOrDefault(RMB, false);
                        event.setCancelled(cancelled);

                        int time = activeRegion.getCooldowns().getOrDefault(RMB, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_RMB", time)) {
                                plugin.getMessage(Lang.Cooldown_Event_RMB)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
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

    @EventHandler
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
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_SHIFT_UP", time)) {
                            if (eventAction.getLangKey() != null)
                                plugin.getMessage(eventAction.getLangKey())
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                        .send(player);
                            return;
                        }
                        eventAction.getManipulator().process(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + SHIFT_UP.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (SHIFT_UP.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(SHIFT_UP, false);
                    event.setCancelled(cancelled);

                    int time = activeRegion.getCooldowns().getOrDefault(SHIFT_UP, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_SHIFT_UP", time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Shift_Up)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    plugin.getMessage(Lang.Events_Shift_Up).send(player); // message for event
                }
            }
        }else{
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
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_SHIFT_DOWN", time)) {
                            if (eventAction.getLangKey() != null)
                                plugin.getMessage(eventAction.getLangKey())
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                        .send(player);
                            return;
                        }
                        eventAction.getManipulator().process(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + SHIFT_DOWN.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (SHIFT_DOWN.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(SHIFT_DOWN, false);
                    event.setCancelled(cancelled);

                    int time = activeRegion.getCooldowns().getOrDefault(SHIFT_DOWN, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_SHIFT_DOWN", time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Shift_Down)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
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

        @EventHandler
        public void onJump (@NotNull PlayerJumpEvent event){
            if (event.isCancelled()) return;
            if (event.getFrom().getY() + 1 == event.getTo().getY() || event.getFrom().getY() + 2 == event.getTo().getY()) {
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
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_JUMP", time)) {
                                if (eventAction.getLangKey() != null)
                                    plugin.getMessage(eventAction.getLangKey())
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                            .send(player);
                                return;
                            }
                            eventAction.getManipulator().process(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + JUMP.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (JUMP.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        boolean cancelled = activeRegion.getCancelled().getOrDefault(JUMP, false);
                        event.setCancelled(cancelled);

                        int time = activeRegion.getCooldowns().getOrDefault(JUMP, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_JUMP", time)) {
                                plugin.getMessage(Lang.Cooldown_Event_Jump)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                        .send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        plugin.getMessage(Lang.Events_Jump).send(player); // message for event
                    }
                }
            }
        }

        @EventHandler
        public void onCommand (@NotNull PlayerCommandPreprocessEvent event){
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
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_COMMANDS", time)) {
                            if (eventAction.getLangKey() != null)
                                plugin.getMessage(eventAction.getLangKey())
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                        .send(player);
                            return;
                        }
                        eventAction.getManipulator().process(player);

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + COMMANDS.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (COMMANDS.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    boolean cancelled = activeRegion.getCancelled().getOrDefault(COMMANDS, false);
                    event.setCancelled(cancelled);

                    int time = activeRegion.getCooldowns().getOrDefault(COMMANDS, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_COMMANDS", time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Commands)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    plugin.getMessage(Lang.Events_Commands).send(player); // message for event
                }
            }
        }
    }