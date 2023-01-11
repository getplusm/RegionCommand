package t.me.p1azmer.plugin.regioncommand.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.api.util.TimeUtil;
import t.me.p1azmer.aves.engine.api.manager.AbstractListener;
import t.me.p1azmer.aves.engine.api.server.JPermission;
import t.me.p1azmer.aves.engine.utils.Cooldowner;
import t.me.p1azmer.plugin.regioncommand.Perm;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityTakeDamageFromBlockInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.api.events.region.entity.EntityTakeDamageFromLavaInRegionEvent;
import t.me.p1azmer.plugin.regioncommand.config.Lang;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;
import t.me.p1azmer.plugin.regioncommand.utils.action.executors.TimerEventAction;

import static t.me.p1azmer.plugin.regioncommand.api.type.Events.TAKE_DAMAGE_FROM_BLOCK;
import static t.me.p1azmer.plugin.regioncommand.api.type.Events.TAKE_DAMAGE_FROM_LAVA;

public class EntityListener extends AbstractListener<RegPlugin> {

    private final RegionManager manager;

    public EntityListener(@NotNull RegionManager manager) {
        super(manager.plugin());
        this.manager = manager;
    }

//    @EventHandler
//    public void onCollision(){} later

    @EventHandler
    public void onTakeDamageFromLava(EntityDamageEvent event) {
        if (event.getFinalDamage() <= 0D || event.isCancelled()) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            if (event.getEntity() instanceof LivingEntity entity) {
                Location location = entity.getLocation();

                if (this.manager.inRegion(location)) {
                    Region region = this.manager.getRegion(location);
                    if (region != null) {
                        EntityTakeDamageFromLavaInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new EntityTakeDamageFromLavaInRegionEvent(entity, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }

                        ActiveRegion activeRegion = region.getActiveRegion();
                        EventAction eventAction = activeRegion.getEventActionByEvent(TAKE_DAMAGE_FROM_LAVA);
                        if (eventAction != null) {
                            if (entity instanceof Player player)
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        plugin.getMessage(Lang.Permission_Event_Damage_Entity).send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            int time = eventAction.getCooldown();
                            String cooldown = "REGION_" + region.getId() + "_ACTION_ON_" + TAKE_DAMAGE_FROM_LAVA.name();
                            if (time > 0) {
                                if (!Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                    Cooldowner cooldowner = new Cooldowner(entity.getName(), cooldown, time);
                                    cooldowner.start();
                                }
                                if (Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                    if (eventAction.getLangMessage() != null) {
                                        eventAction.getLangMessage()
                                                .replace("%time%", time)
                                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                                .send(entity);
                                    }
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                            if (entity instanceof Player player)
                                eventAction.getManipulator().processAll(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + TAKE_DAMAGE_FROM_LAVA.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldowner.isInCooldown(entity.getName(), timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldowner.getTimeLeft(entity.getName(), timerEventActionKey)) : "0"))
                            );

                            if (TAKE_DAMAGE_FROM_LAVA.cancelledEvents.contains(entity)) {
                                event.setCancelled(true);
                                return;
                            }
                            if (eventAction.isCancelled() && entity instanceof Player && (!entity.hasPermission(Perm.REGION_BYPASS) || !((Player) entity).getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        int time = activeRegion.getCooldowns().getOrDefault(TAKE_DAMAGE_FROM_LAVA, -1);
                        String cooldown = "REGION_" + region.getId() + "_" + TAKE_DAMAGE_FROM_LAVA.name();
                        if (time > 0) {
                            if (!Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                Cooldowner cooldowner = new Cooldowner(entity.getName(), cooldown, time);
                                cooldowner.start();
                            }
                            if (Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                plugin.getMessage(Lang.Cooldown_Event_Damage_Entity)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(entity);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        //plugin.getMessage(Lang.Events_Damage_Entity).send(entity); // message for event
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTakeDamageFromBlock(EntityDamageByBlockEvent event) {
        if (event.getDamager() == null) return;
        if (event.getEntity() instanceof LivingEntity entity) {
            Location location = entity.getLocation();
            Location blockLocation = event.getDamager().getLocation();

            if (this.manager.inRegion(location) && this.manager.inRegion(blockLocation)) {
                Region region = this.manager.getRegion(location);
                Region damagerRegion = this.manager.getRegion(blockLocation);
                if (region != null) {
                    if (damagerRegion != null && damagerRegion.equals(region)) {
                        EntityTakeDamageFromBlockInRegionEvent customEventCaller = t.me.p1azmer.api.Events.callSyncAndJoin(new EntityTakeDamageFromBlockInRegionEvent(entity, region));
                        if (customEventCaller.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }

                        ActiveRegion activeRegion = region.getActiveRegion();
                        EventAction eventAction = activeRegion.getEventActionByEvent(TAKE_DAMAGE_FROM_BLOCK);
                        if (eventAction != null) {
                            if (entity instanceof Player player)
                                if (!player.hasPermission(Perm.REGION_BYPASS) || !player.getGameMode().equals(GameMode.SPECTATOR)) {
                                    JPermission permission = eventAction.getPermission();
                                    if (permission != null && !player.hasPermission(permission)) {
                                        plugin.getMessage(Lang.Permission_Event_Damage_Entity).send(player);
                                        event.setCancelled(true);
                                        return;
                                    }
                                }
                            int time = eventAction.getCooldown();
                            String cooldown = "REGION_" + region.getId() + "_ACTION_ON_" + TAKE_DAMAGE_FROM_BLOCK.name();
                            if (time > 0) {
                                if (!Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                    Cooldowner cooldowner = new Cooldowner(entity.getName(), cooldown, time);
                                    cooldowner.start();
                                }
                                if (Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                    if (eventAction.getLangMessage() != null) {
                                        eventAction.getLangMessage()
                                                .replace("%time%", time)
                                                .replace("%time_correct%", TimeUtil.leftTime(time))
                                                .send(entity);
                                    }
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                            if (entity instanceof Player player)
                                eventAction.getManipulator().processAll(player);

                            String timerEventActionKey = TimerEventAction.COOLDOWN_KEY + "_" + TAKE_DAMAGE_FROM_BLOCK.name();
                            eventAction.getManipulator().replace(s -> s
                                    .replaceAll("%cooldown_time%", (Cooldowner.isInCooldown(entity.getName(), timerEventActionKey) ? t.me.p1azmer.aves.engine.utils.TimeUtil.formatTimeLeft(Cooldowner.getTimeLeft(entity.getName(), timerEventActionKey)) : "0"))
                            );

                            if (TAKE_DAMAGE_FROM_BLOCK.cancelledEvents.contains(entity)) {
                                event.setCancelled(true);
                                return;
                            }
                            if (eventAction.isCancelled() && entity instanceof Player && (!entity.hasPermission(Perm.REGION_BYPASS) || !((Player) entity).getGameMode().equals(GameMode.SPECTATOR))) {
                                event.setCancelled(true);
                                return;
                            }
                        }

                        int time = activeRegion.getCooldowns().getOrDefault(TAKE_DAMAGE_FROM_BLOCK, -1);
                        String cooldown = "REGION_" + region.getId() + "_" + TAKE_DAMAGE_FROM_BLOCK.name();
                        if (time > 0) {
                            if (!Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                Cooldowner cooldowner = new Cooldowner(entity.getName(), cooldown, time);
                                cooldowner.start();
                            }
                            if (Cooldowner.isInCooldown(entity.getName(), cooldown)) {
                                plugin.getMessage(Lang.Cooldown_Event_Damage_Entity)
                                        .replace("%time%", time)
                                        .replace("%time_correct%", TimeUtil.leftTime(time))
                                        .send(entity);
                                event.setCancelled(true);
                                return;
                            }
                        }

                        //plugin.getMessage(Lang.Events_Damage_Entity).send(entity); // message for event
                    }
                }
            }
        }
    }
}
