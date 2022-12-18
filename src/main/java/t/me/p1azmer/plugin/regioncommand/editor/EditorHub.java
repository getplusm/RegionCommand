package t.me.p1azmer.plugin.regioncommand.editor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.api.editor.EditorInput;
import t.me.p1azmer.aves.engine.api.menu.IMenuClick;
import t.me.p1azmer.aves.engine.api.menu.MenuItemType;
import t.me.p1azmer.aves.engine.editor.AbstractEditorMenuAuto;
import t.me.p1azmer.aves.engine.editor.EditorManager;
import t.me.p1azmer.aves.engine.utils.ItemUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;
import t.me.p1azmer.plugin.regioncommand.RegPlugin;
import t.me.p1azmer.plugin.regioncommand.api.Region;
import t.me.p1azmer.plugin.regioncommand.data.Lang;
import t.me.p1azmer.plugin.regioncommand.manager.RegionManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class EditorHub extends AbstractEditorMenuAuto<RegPlugin, RegPlugin, Region> {

    public EditorHub(@NotNull RegPlugin plugin) {
        super(plugin, plugin, "Настройка регионов", 45);

        EditorInput<RegionManager, EditorType> input = (player, manager, type, e) -> {
            String msg = e.getMessage();
            if (type == EditorType.CREATE_REGION) {
                String id = EditorManager.fineId(msg);
                if (plugin.getManager().regions.containsKey(id)) {
                    EditorManager.error(player, plugin.getMessage(Lang.EDITOR_REGION_EXIST).getLocalized());
                    return false;
                }
                Region region = new Region(manager, id, player.getLocation());
                region.save();
                plugin.getMessage(Lang.EDITOR_REGION_CREATED).send(player);
                EditorManager.endEdit(player);
                region.getEditor().open(player, 1);
                return true;
            }
            return true;
        };

        IMenuClick click = (player, type, e) -> {
            if (type instanceof MenuItemType type2) {
                if (type2 == MenuItemType.CLOSE) {
                    player.closeInventory();
                }
            } else if (type instanceof EditorType type2) {
                if (type2 == EditorType.CREATE_REGION) {
                    EditorManager.startEdit(player, plugin.getManager(), type2, input);
                    EditorManager.tip(player, plugin.getMessage(Lang.EDITOR_WRITE_ID).getLocalized());
                    player.closeInventory();
                }
            }
        };

        this.loadItems(click);
    }

    @Override
    public void setTypes(@NotNull Map<EditorButtonType, Integer> map) {
        map.put(EditorType.CREATE_REGION, 41);
        map.put(MenuItemType.RETURN, 39);
        map.put(MenuItemType.PAGE_NEXT, 44);
        map.put(MenuItemType.PAGE_PREVIOUS, 36);
    }

    @Override
    public int[] getObjectSlots() {
        return IntStream.range(0, 36).toArray();
    }

    @Override
    protected @NotNull List<Region> getObjects(@NotNull Player player) {
        return new ArrayList<>(parent.getManager().regions.values());
    }

    @Override
    protected @NotNull ItemStack getObjectStack(@NotNull Player player, @NotNull Region region) {
        ItemStack item = new ItemStack(Material.ZOMBIE_HEAD);
        ItemUtil.setName(item, "&6Название: " + Placeholders.PLACEHOLDER_REGION_NAME);
        ItemUtil.addLore(item, "item_lore", Arrays.asList(
                "&7Радиус действия: &6" + Placeholders.PLACEHOLDER_ACTION_RADIUS,
                "",
                "&7Кубоид:",
                "&7Первая локация:",
                Placeholders.PLACEHOLDER_REGION_CUBOID_FIRST,
                "&7Вторая локация:",
                Placeholders.PLACEHOLDER_REGION_CUBOID_FIRST,
                "",
                "&eЛКМ, чтобы настроить",
                "&eПКМ, для телепорта",
                "&eЗажмите ШИФТ, чтобы удалить"
        ), 1);
        ItemUtil.replace(item, region.replacePlaceholders());
        ItemUtil.replace(item, region.getActiveRegion().replacePlaceholders());
        return item;
    }

    @Override
    protected @NotNull IMenuClick getObjectClick(@NotNull Player player, @NotNull Region region) {
        return (player1, type, event) -> {
            if (event.isRightClick()) {
                player.teleport(region.getCuboid().getCenter());
                return;
            }
            if (event.isShiftClick()) {
                this.parent.getManager().removeRegion(region);
                this.open(player, getPage(player));
                return;
            }
            region.getEditor().open(player, 1);
        };
    }

    @Override
    public boolean cancelClick(@NotNull InventoryClickEvent inventoryClickEvent, @NotNull SlotType slotType) {
        return true;
    }
}
