package t.me.p1azmer.plugin.regioncommand.listener;

import org.bukkit.Location;
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
import t.me.p1azmer.aves.engine.utils.MessageUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.cuboid.PlayerEnterCuboidEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.*;
import t.me.p1azmer.plugin.regioncommand.api.events.cuboid.PlayerLeaveCuboidEvent;
import t.me.p1azmer.plugin.regioncommand.api.type.Events;
import t.me.p1azmer.plugin.regioncommand.data.Lang;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;

import static t.me.p1azmer.plugin.regioncommand.api.type.Events.*;

public class PlayerListener extends AbstractListener<RegPlugin> {

    private final RegionManager manager;

    public PlayerListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
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

                EventAction eventAction = activeRegion.getEventActionByEvent(MOVE);
                if (eventAction != null) {
                    JPermission permission = eventAction.getPermission();
                    if (permission != null && !player.hasPermission(permission)) {
                        plugin.getMessage(Lang.Permission_Event_Move).send(player);
                        return;
                    }
                    time = eventAction.getCooldown();
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_MOVE", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
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

                EventAction eventAction = activeRegion.getEventActionByEvent(BLOCK_PLACE);
                if (eventAction != null) {
                    JPermission permission = eventAction.getPermission();
                    if (permission != null && !player.hasPermission(permission)) {
                        plugin.getMessage(Lang.Permission_Event_Place).send(player);
                        return;
                    }
                    time = eventAction.getCooldown();
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_PLACE", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);
                }
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
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

                EventAction eventAction = activeRegion.getEventActionByEvent(BLOCK_BREAK);
                if (eventAction != null) {
                    JPermission permission = eventAction.getPermission();
                    if (permission != null && !player.hasPermission(permission)) {
                        plugin.getMessage(Lang.Permission_Event_Break).send(player);
                        return;
                    }
                    time = eventAction.getCooldown();
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_BREAK", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);
                }
            }
        }
    }

    @EventHandler
    public void onEnter(PlayerEnterRegionEvent event) {
        Player player = event.getPlayer();
        if (event.getRegion() != null) {

            Region region = event.getRegion();
            ActiveRegion activeRegion = region.getActiveRegion();

            EventAction eventAction = activeRegion.getEventActionByEvent(ENTER);
            if (eventAction != null) {
                JPermission permission = eventAction.getPermission();
                if (permission != null && !player.hasPermission(permission)) {
                    plugin.getMessage(Lang.Permission_Event_Enter).send(player);
                    return;
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
                if (event.isCancelled())
                    return;
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
    public void onLeave(PlayerLeaveRegionEvent event) {
        Player player = event.getPlayer();
        if (event.getRegion() != null) {
            Region region = event.getRegion();
            if (event.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            ActiveRegion activeRegion = region.getActiveRegion();

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

            EventAction eventAction = activeRegion.getEventActionByEvent(LEAVE);
            if (eventAction != null) {
                JPermission permission = eventAction.getPermission();
                if (permission != null && !player.hasPermission(permission)) {
                    plugin.getMessage(Lang.Permission_Event_Leave).send(player);
                    return;
                }
                time = eventAction.getCooldown();
                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_LEAVE", time)) {
                    if (eventAction.getLangKey() != null)
                        plugin.getMessage(eventAction.getLangKey())
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                .send(player);
                    return;
                }
                eventAction.getManipulator().process(player);
            }
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        if (this.manager.inRegion(player) && this.manager.inRegion(event.getInteractionPoint())) {
            switch (action) {
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                    Region region = this.manager.getRegion(player);
                    if (region != null) {
                        PlayerLMBInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLMBInRegionEvent(player, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                        ActiveRegion activeRegion = region.getActiveRegion();

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

                        EventAction eventAction = activeRegion.getEventActionByEvent(LMB);
                        if (eventAction != null) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                plugin.getMessage(Lang.Permission_Event_LMB).send(player);
                                return;
                            }
                            time = eventAction.getCooldown();
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_LMB", time)) {
                                if (eventAction.getLangKey() != null)
                                    plugin.getMessage(eventAction.getLangKey())
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                            .send(player);
                                return;
                            }
                            eventAction.getManipulator().process(player);
                        }
                    }
                }
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                    Region region = this.manager.getRegion(player);
                    if (region != null) {
                        PlayerRMBInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerRMBInRegionEvent(player, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                        ActiveRegion activeRegion = region.getActiveRegion();

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

                        EventAction eventAction = activeRegion.getEventActionByEvent(RMB);
                        if (eventAction != null) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                plugin.getMessage(Lang.Permission_Event_RMB).send(player);
                                return;
                            }
                            time = eventAction.getCooldown();
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_RMB", time)) {
                                if (eventAction.getLangKey() != null)
                                    plugin.getMessage(eventAction.getLangKey())
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                            .send(player);
                                return;
                            }
                            eventAction.getManipulator().process(player);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onShift(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                PlayerShiftInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerShiftInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }
                ActiveRegion activeRegion = region.getActiveRegion();

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

                EventAction eventAction = activeRegion.getEventActionByEvent(SHIFT);
                if (eventAction != null) {
                    JPermission permission = eventAction.getPermission();
                    if (permission != null && !player.hasPermission(permission)) {
                        plugin.getMessage(Lang.Permission_Event_Shift).send(player);
                        return;
                    }
                    time = eventAction.getCooldown();
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_SHIFT", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);
                }
            }
        }
    }

    @EventHandler
    public void onJump(PlayerMoveEvent event) {
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

                    EventAction eventAction = activeRegion.getEventActionByEvent(JUMP);
                    if (eventAction != null) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Jump).send(player);
                            return;
                        }
                        time = eventAction.getCooldown();
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_JUMP", time)) {
                            if (eventAction.getLangKey() != null)
                                plugin.getMessage(eventAction.getLangKey())
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                        .send(player);
                            return;
                        }
                        eventAction.getManipulator().process(player);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
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

                EventAction eventAction = activeRegion.getEventActionByEvent(COMMANDS);
                if (eventAction != null) {
                    JPermission permission = eventAction.getPermission();
                    if (permission != null && !player.hasPermission(permission)) {
                        plugin.getMessage(Lang.Permission_Event_Commands).send(player);
                        return;
                    }
                    time = eventAction.getCooldown();
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_COMMANDS", time)) {
                        if (eventAction.getLangKey() != null)
                            plugin.getMessage(eventAction.getLangKey())
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time * 20L))
                                    .send(player);
                        return;
                    }
                    eventAction.getManipulator().process(player);
                }
            }
        }
    }


    // register custom events (like enter cuboid)

    @EventHandler
    public void detectCuboidEvent(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        Player player = event.getPlayer();

        Region toRegion = plugin.getManager().getRegion(to);
        Region fromRegion = plugin.getManager().getRegion(from);
        if (fromRegion == null && toRegion != null) {
            PlayerEnterCuboidEvent playerEnterCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterCuboidEvent(player, toRegion.getCuboid()));
            PlayerEnterRegionEvent playerEnterRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerEnterRegionEvent(player, toRegion));
            if (playerEnterCuboidEvent.isCancelled() || playerEnterRegionEvent.isCancelled()) {
                event.setCancelled(true);
                event.setTo(from);
            }
        } else if (fromRegion != null && toRegion == null) {
            PlayerLeaveCuboidEvent playerLeaveCuboidEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveCuboidEvent(player, fromRegion.getCuboid()));
            PlayerLeaveRegionEvent playerLeaveRegionEvent = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerLeaveRegionEvent(player, fromRegion));
            if (playerLeaveCuboidEvent.isCancelled() || playerLeaveRegionEvent.isCancelled()) {
                event.setCancelled(true);
                event.setTo(from);
            }
        }
    }

//    @EventHandler
//    public void onRegionEnter(PlayerEnterRegionEvent event) {
//        Player player = event.getPlayer();
//        Region region = event.getRegion();
//        ActiveRegion activeRegion = region.getActiveRegion();
//        EventAction eventAction = activeRegion.getEventActionByEvent(Events.ENTER);
//        if (eventAction != null) {
//            if (eventAction.getPermission() != null && !player.hasPermission(eventAction.getPermission())) {
//                event.setCancelled(true);
//            }
//        }
//    }


    // detect and cancelled spigot events (nothing now)


}