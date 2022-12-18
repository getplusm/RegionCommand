package t.me.p1azmer.plugin.regioncommand.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.util.Cooldown;
import t.me.p1azmer.api.util.TimeUtil;
import t.me.p1azmer.aves.engine.api.manager.AbstractListener;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.aves.engine.utils.PlayerUtil;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.plugin.regioncommand.Perm;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.cuboid.PlayerEnterCuboidEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.cuboid.PlayerLeaveCuboidEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.*;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
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
                    if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
                    if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
                    if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
        Player player = event.getPlayer();

        if (event.getRegion() != null) {

            Region region = event.getRegion();
            ActiveRegion activeRegion = region.getActiveRegion();

            EventAction eventAction = activeRegion.getEventActionByEvent(ENTER);
            if (eventAction != null) {
                if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
        Player player = event.getPlayer();
        if (event.getRegion() != null) {

            Region region = event.getRegion();
            ActiveRegion activeRegion = region.getActiveRegion();

            EventAction eventAction = activeRegion.getEventActionByEvent(LEAVE);
            if (eventAction != null) {
                if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
                            if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
                            if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
        Player player = event.getPlayer();
        if (player.isSneaking()) return;
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {

                PlayerShiftInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerShiftInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ActiveRegion activeRegion = region.getActiveRegion();

                EventAction eventAction = activeRegion.getEventActionByEvent(SHIFT);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Shift).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    int time = eventAction.getCooldown();
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_SHIFT", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + SHIFT.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (SHIFT.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                    }
                }

                boolean cancelled = activeRegion.getCancelled().getOrDefault(SHIFT, false);
                event.setCancelled(cancelled);

                int time = activeRegion.getCooldowns().getOrDefault(SHIFT, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_SHIFT", time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Shift)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Shift).send(player); // message for event
            }
        }
    }

    @EventHandler
    public void onJump(@NotNull PlayerMoveEvent event) {
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
                        if (!player.hasPermission(Perm.REGION_BYPASS)) {
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
    public void onCommand(@NotNull PlayerCommandPreprocessEvent event) {
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
                    if (!player.hasPermission(Perm.REGION_BYPASS)) {
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


    // register custom events (like enter cuboid)

    @EventHandler
    public void detectCuboidEvent(@NotNull PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        Player player = event.getPlayer();

        Region toRegion = plugin.getManager().getRegion(to);
        Region fromRegion = plugin.getManager().getRegion(from);
        if (fromRegion == null && toRegion != null) {
            PlayerEnterCuboidEvent playerEnterCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterCuboidEvent(player, toRegion.getCuboid()));
            PlayerEnterRegionEvent playerEnterRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterRegionEvent(player, toRegion));
            if (playerEnterCuboidEvent.isCancelled() || playerEnterRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS))
                    event.setCancelled(true);
                return;
            }
        } else if (fromRegion != null && toRegion == null) {
            PlayerLeaveCuboidEvent playerLeaveCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveCuboidEvent(player, fromRegion.getCuboid()));
            PlayerLeaveRegionEvent playerLeaveRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveRegionEvent(player, fromRegion));
            if (playerLeaveCuboidEvent.isCancelled() || playerLeaveRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS))
                    event.setCancelled(true);
                return;
            }
        }
        if (fromRegion != null && toRegion != null) {
            PlayerMoveInRegionEvent toMove = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, fromRegion));
            PlayerMoveInRegionEvent fromMove = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, toRegion));
            if (toMove.isCancelled() || fromMove.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS))
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onRegionEnter(@NotNull PlayerEnterRegionEvent event) {
        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        Region region = event.getRegion();
        ActiveRegion activeRegion = region.getActiveRegion();
        EventAction eventAction = activeRegion.getEventActionByEvent(Events.ENTER);
        if (eventAction != null) {
            if (!player.hasPermission(Perm.REGION_BYPASS))
                if (eventAction.getPermission() != null && !player.hasPermission(eventAction.getPermission())) {
                    event.setCancelled(true);
                }
        }
    }

    @EventHandler
    public void detectCuboidEventTP(@NotNull PlayerTeleportEvent event) {
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE))
            return;
        Location from = event.getFrom();
        Location to = event.getTo();

        Player player = event.getPlayer();

        Region toRegion = plugin.getManager().getRegion(to);
        Region fromRegion = plugin.getManager().getRegion(from);
        if (fromRegion == null && toRegion != null) {
            PlayerEnterCuboidEvent playerEnterCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterCuboidEvent(player, toRegion.getCuboid()));
            PlayerEnterRegionEvent playerEnterRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterRegionEvent(player, toRegion));
            if (playerEnterCuboidEvent.isCancelled() || playerEnterRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS))
                    event.setCancelled(true);
                player.sendMessage("player teleport cancelled 1");
                return;
            }
        } else if (fromRegion != null && toRegion == null) {
            PlayerLeaveCuboidEvent playerLeaveCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveCuboidEvent(player, fromRegion.getCuboid()));
            PlayerLeaveRegionEvent playerLeaveRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveRegionEvent(player, fromRegion));
            if (playerLeaveCuboidEvent.isCancelled() || playerLeaveRegionEvent.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS))
                    event.setCancelled(true);
                player.sendMessage("player teleport cancelled 2");
                return;
            }
        }
        if (fromRegion != null && toRegion != null) {
            PlayerMoveInRegionEvent toMove = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, fromRegion));
            PlayerMoveInRegionEvent fromMove = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerMoveInRegionEvent(player, toRegion));
            if (toMove.isCancelled() || fromMove.isCancelled()) {
                if (!player.hasPermission(Perm.REGION_BYPASS))
                    event.setCancelled(true);
                player.sendMessage("player teleport cancelled 3");
            }
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
            if (enterRegionEvent.isCancelled()) {
                PlayerUtil.dispatchCommand(player, "[PLAYER] spawn");
                player.sendMessage(StringUtil.color("&cВозможно, вы могли застрять в этом регионе. Мы принудительно телепортировали вас на спавн, чтобы избежать этого.\nЕсли это ошибка, сообщите администрации код: #PZ-RGCMD-LOGIN_EVENT_SAFE_REGION-" + region.getName()));
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
            if (moveFrom.isCancelled() && moveTo.isCancelled() || event.isCancelled()) {
                PlayerUtil.dispatchCommand(player, "[PLAYER] spawn");
                player.sendMessage(StringUtil.color("&cВозможно, вы могли застрять в этом регионе. Мы принудительно телепортировали вас на спавн, чтобы избежать этого.\nЕсли это ошибка, сообщите администрации код: #PZ-RGCMD-MOVE_EVENT_SAFE_REGION-" + toRegion.getName()));
            }
        }
    }


}
