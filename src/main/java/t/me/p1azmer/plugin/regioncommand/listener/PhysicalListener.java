package t.me.p1azmer.plugin.regioncommand.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
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
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerDamageEntityInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerDamagePlayerInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerTakeByPlayerDamageInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.damage.PlayerTakeDamageInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.use.PlayerDropItemInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.use.PlayerPickUpItemInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;

import static t.me.p1azmer.plugin.regioncommand.api.type.Events.*;

public class PhysicalListener extends AbstractListener<RegPlugin> {

    private final RegionManager manager;

    public PhysicalListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

    @EventHandler
    public void onDamagePlayer(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            if (event.getDamager() instanceof Player damager) {
                if (this.manager.inRegion(player) && this.manager.inRegion(damager)) {
                    Region region = this.manager.getRegion(player);
                    Region damagerRegion = this.manager.getRegion(damager);
                    if (region != null) {
                        if (damagerRegion != null && damagerRegion.equals(region)) {
                            PlayerDamagePlayerInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerDamagePlayerInRegionEvent(player, region));
                            if (customEventCaller.isCancelled()) {
                                event.setCancelled(true);
                                return;
                            }

                            ActiveRegion activeRegion = region.getActiveRegion();
                            EventAction eventAction = activeRegion.getEventActionByEvent(DAMAGE_PLAYER);
                            if (eventAction != null) {
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        plugin.getMessage(Lang.Permission_Event_Damage_Player).send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                                int time = eventAction.getCooldown();
                                if (time > 0)
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + DAMAGE_PLAYER.name(), time)) {
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

                                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + DAMAGE_PLAYER.name();
                                eventAction.getManipulator().replace(s -> s
                                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                );

                                if (DAMAGE_PLAYER.cancelledEvents.contains(player)) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            int time = activeRegion.getCooldowns().getOrDefault(DAMAGE_PLAYER, -1);
                            if (time > 0) {
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_DAMAGE_PLAYER", time)) {
                                    plugin.getMessage(Lang.Cooldown_Event_Damage_Player)
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            plugin.getMessage(Lang.Events_Damage_Player).send(player); // message for event
                        }
                    }
                }

            }
        }
    }

    @EventHandler
    public void onTakeDamageByPlayer(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            if (event.getDamager() instanceof Player damager) {
                if (this.manager.inRegion(player) && this.manager.inRegion(damager)) {
                    Region region = this.manager.getRegion(player);
                    Region damagerRegion = this.manager.getRegion(damager);
                    if (region != null) {
                        if (damagerRegion != null && damagerRegion.equals(region)) {
                            PlayerTakeByPlayerDamageInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerTakeByPlayerDamageInRegionEvent(player, region));
                            if (customEventCaller.isCancelled()) {
                                event.setCancelled(true);
                                return;
                            }

                            ActiveRegion activeRegion = region.getActiveRegion();
                            EventAction eventAction = activeRegion.getEventActionByEvent(TAKE_DAMAGE_PLAYER);
                            if (eventAction != null) {
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        plugin.getMessage(Lang.Permission_Event_Take_Damage_Player).send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                                int time = eventAction.getCooldown();
                                if (time > 0)
                                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + TAKE_DAMAGE_PLAYER.name(), time)) {
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

                                String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + TAKE_DAMAGE_PLAYER.name();
                                eventAction.getManipulator().replace(s -> s
                                        .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                                );

                                if (TAKE_DAMAGE_PLAYER.cancelledEvents.contains(player)) {
                                    event.setCancelled(true);
                                    return;
                                }
                                if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            int time = activeRegion.getCooldowns().getOrDefault(TAKE_DAMAGE_PLAYER, -1);
                            if (time > 0) {
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_TAKE_DAMAGE_PLAYER", time)) {
                                    plugin.getMessage(Lang.Cooldown_Event_Take_Damage_Player)
                                            .replace("%time%", time)
                                            .replace("%time_correct%", TimeUtil.leftTime(time))
                                            .send(player);
                                    event.setCancelled(true);
                                    return;
                                }
                            }

                            plugin.getMessage(Lang.Events_Take_Damage_Player).send(player); // message for event
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamageEntity(@NotNull EntityDamageByEntityEvent event) {
        if (event.isCancelled()) return;

        if (event.getDamager() instanceof Player player && !(event.getEntity() instanceof Player)) {


            Location entityLocation = event.getEntity().getLocation();

            if (this.manager.inRegion(player) && this.manager.inRegion(entityLocation)) {
                Region region = this.manager.getRegion(player);
                Region damagerRegion = this.manager.getRegion(entityLocation);
                if (region != null) {
                    if (damagerRegion != null && damagerRegion.equals(region)) {
                        PlayerDamageEntityInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerDamageEntityInRegionEvent(player, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }

                        ActiveRegion activeRegion = region.getActiveRegion();
                        EventAction eventAction = activeRegion.getEventActionByEvent(DAMAGE_ENTITY);
                        if (eventAction != null) {
                            if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                JPermission permission = eventAction.getPermission();
                                if (permission != null && !player.hasPermission(permission)) {
                                    plugin.getMessage(Lang.Permission_Event_Damage_Entity).send(player);
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                            int time = eventAction.getCooldown();
                            if (time > 0)
                                if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + DAMAGE_ENTITY.name(), time)) {
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

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + DAMAGE_ENTITY.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                            );

                            if (DAMAGE_ENTITY.cancelledEvents.contains(player)) {
                                event.setCancelled(true);
                                return;
                            }
                            if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        int time = activeRegion.getCooldowns().getOrDefault(DAMAGE_ENTITY, -1);
                        if (time > 0) {
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_TAKE_DAMAGE_PLAYER", time)) {
                                plugin.getMessage(Lang.Cooldown_Event_Damage_Entity)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        plugin.getMessage(Lang.Events_Damage_Entity).send(player); // message for event
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTakeDamage(@NotNull EntityDamageEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            if (this.manager.inRegion(player)) {
                Region region = this.manager.getRegion(player);
                if (region != null) {
                    PlayerTakeDamageInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerTakeDamageInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }

                    ActiveRegion activeRegion = region.getActiveRegion();
                    EventAction eventAction = activeRegion.getEventActionByEvent(TAKE_DAMAGE);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                plugin.getMessage(Lang.Permission_Event_Take_Damage).send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }
                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + TAKE_DAMAGE.name(), time)) {
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

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + TAKE_DAMAGE.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (TAKE_DAMAGE.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    int time = activeRegion.getCooldowns().getOrDefault(TAKE_DAMAGE, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_TAKE_DAMAGE", time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Take_Damage)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    plugin.getMessage(Lang.Events_Take_Damage).send(player); // message for event
                }
            }
        }
    }

    @EventHandler
    public void onDrop(@NotNull PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();

        if (this.manager.inRegion(player)) {
            Region region = this.manager.getRegion(player);
            if (region != null) {
                PlayerDropItemInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerDropItemInRegionEvent(player, region));
                if (customEventCaller.isCancelled()) {
                    event.setCancelled(true);
                    return;
                }

                ActiveRegion activeRegion = region.getActiveRegion();
                EventAction eventAction = activeRegion.getEventActionByEvent(DROP_ITEMS);
                if (eventAction != null) {
                    if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        JPermission permission = eventAction.getPermission();
                        if (permission != null && !player.hasPermission(permission)) {
                            plugin.getMessage(Lang.Permission_Event_Drop_Items).send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }
                    int time = eventAction.getCooldown();
                    if (time > 0)
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + DROP_ITEMS.name(), time)) {
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

                    String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + DROP_ITEMS.name();
                    eventAction.getManipulator().replace(s -> s
                            .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                    );

                    if (DROP_ITEMS.cancelledEvents.contains(player)) {
                        event.setCancelled(true);
                        return;
                    }
                    if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                        event.setCancelled(true);
                        return;
                    }
                }

                int time = activeRegion.getCooldowns().getOrDefault(DROP_ITEMS, -1);
                if (time > 0) {
                    if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_TAKE_DAMAGE", time)) {
                        plugin.getMessage(Lang.Cooldown_Event_Drop_Items)
                                .replace("%time%", time)
                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                .send(player);
                        event.setCancelled(true);
                        return;
                    }
                }

                plugin.getMessage(Lang.Events_Drop_Items).send(player); // message for event
            }
        }

    }

    @EventHandler
    public void onPickUp(@NotNull EntityPickupItemEvent event) {
        if (event.isCancelled()) return;

        if (event.getEntity() instanceof Player player) {
            if (this.manager.inRegion(player)) {
                Region region = this.manager.getRegion(player);
                if (region != null) {
                    PlayerPickUpItemInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new PlayerPickUpItemInRegionEvent(player, region));
                    if (customEventCaller.isCancelled()) {
                        event.setCancelled(true);
                        return;
                    }

                    ActiveRegion activeRegion = region.getActiveRegion();
                    EventAction eventAction = activeRegion.getEventActionByEvent(PICKUP_ITEMS);
                    if (eventAction != null) {
                        if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                            JPermission permission = eventAction.getPermission();
                            if (permission != null && !player.hasPermission(permission)) {
                                plugin.getMessage(Lang.Permission_Event_Pickup_Items).send(player);
                                event.setCancelled(true);
                                return;
                            }
                        }
                        int time = eventAction.getCooldown();
                        if (time > 0)
                            if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_ACTION_ON_" + PICKUP_ITEMS.name(), time)) {
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

                        String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + PICKUP_ITEMS.name();
                        eventAction.getManipulator().replace(s -> s
                                .replaceAll("%cooldown_time%", (Cooldown.hasCooldown(player, timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldown.getSecondCooldown(player, timerEventActionKey)) : "0"))
                        );

                        if (PICKUP_ITEMS.cancelledEvents.contains(player)) {
                            event.setCancelled(true);
                            return;
                        }
                        if (eventAction.isCancelled() && (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR))) {
                            event.setCancelled(true);
                            return;
                        }
                    }

                    int time = activeRegion.getCooldowns().getOrDefault(PICKUP_ITEMS, -1);
                    if (time > 0) {
                        if (Cooldown.hasOrAddCooldown(player, "REGION_" + region.getId() + "_TAKE_DAMAGE", time)) {
                            plugin.getMessage(Lang.Cooldown_Event_Pickup_Items)
                                    .replace("%time%", time)
                                    .replace("%time_correct%", TimeUtil.leftTime(time))
                                    .send(player);
                            event.setCancelled(true);
                            return;
                        }
                    }

                    plugin.getMessage(Lang.Events_Pickup_Items).send(player); // message for event
                }
            }
        }
    }
}
