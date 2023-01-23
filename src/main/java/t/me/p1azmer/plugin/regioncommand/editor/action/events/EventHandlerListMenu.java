package t.me.p1azmer.plugin.regioncommand.editor.action.events;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.config.JYML;
import t.me.p1azmer.aves.engine.api.menu.AbstractMenuAuto;
import t.me.p1azmer.aves.engine.api.menu.MenuClick;
import t.me.p1azmer.aves.engine.api.menu.MenuItem;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.utils.ComponentUtil;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.ActiveRegion;
import t.me.p1azmer.plugin.regioncommand.api.EventAction;
import t.me.p1azmer.plugin.regioncommand.api.type.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventHandlerListMenu extends AbstractMenuAuto<RegPlugin, EventHandler> {

    private final ActiveRegion activeRegion;

    public EventHandlerListMenu(@NotNull ActiveRegion activeRegion) {
        super(activeRegion.plugin(), JYML.loadOrExtract(activeRegion.plugin(), "menu/events.menu.yml"), "");

        this.activeRegion = activeRegion;

        MenuClick click = (player, type, inventoryClickEvent) -> {
            if (type == null) return;
            if (type instanceof MenuItemType type2) {
                if (type.equals(MenuItemType.RETURN)) {
                    this.activeRegion.getEventActiveListEditor().open(player, 1);
                    return;
                }
                this.onItemClickDefault(player, type2);
            }
        };
        for (String id : cfg.getSection("Content")) {
            MenuItem menuItem = cfg.getMenuItem("Content." + id, MenuItemType.class);
            menuItem.setClickHandler(click);
            this.addItem(menuItem);
        }

    }

    @Override
    protected int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    protected @NotNull List<EventHandler> getObjects(@NotNull Player player) {
        List<EventHandler> eventHandlers = new ArrayList<>(Arrays.stream(EventHandler.values()).toList());
        eventHandlers = eventHandlers.stream().filter(eventHandler -> this.activeRegion.getEventActions().stream().noneMatch(action -> action.getEventHandler().equals(eventHandler)))
                .collect(Collectors.toList());
        return eventHandlers;
    }

    @Override
    protected @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull EventHandler eventHandler) {
        ItemStack item = eventHandler.getItem();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(ComponentUtil.asComponent("&7Ивент: &6" + eventHandler.getName()));
            meta.lore(ComponentUtil.asComponent(eventHandler.getLore()));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return eventHandler.getItem();
    }

    @Override
    protected @NotNull MenuClick getObjectClick(@NotNull Player player, @NotNull EventHandler eventHandler) {
        return (player1, type, event) -> {
            EventAction eventAction = new EventAction(activeRegion, eventHandler);
            activeRegion.getEventActions().add(eventAction);
            activeRegion.save();
            eventAction.getEditor().open(player, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
