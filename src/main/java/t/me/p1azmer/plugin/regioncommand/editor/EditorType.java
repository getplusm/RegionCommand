package t.me.p1azmer.plugin.regioncommand.editor;

import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import t.me.p1azmer.aves.engine.api.editor.EditorButtonType;
import t.me.p1azmer.aves.engine.utils.StringUtil;
import t.me.p1azmer.plugin.regioncommand.Placeholders;

import java.util.ArrayList;
import java.util.List;

import static t.me.p1azmer.plugin.regioncommand.Placeholders.*;

public enum EditorType implements EditorButtonType {

    CREATE_REGION(Material.ANVIL, "&aСоздать регион"
            , EditorButtonType.click("Нажмите, чтобы создать")
    ),

    REGION_CHANGE_NAME(Material.NAME_TAG, "&6Изменить название"
            , EditorButtonType.current(PLACEHOLDER_REGION_NAME)
            , EditorButtonType.click("Нажмите, чтобы изменить")
    ),

    REGION_CHANGE_CUBOID_MIN(Material.PAPER, "&6Изменить нижнюю локацию"
            , EditorButtonType.current(PLACEHOLDER_REGION_CUBOID_MIN)
            , EditorButtonType.click("Нажмите, чтобы изменить\nЗажмите ШИФТ, чтобы удалить")
    ),
    REGION_CHANGE_CUBOID_MAX(Material.PAPER, "&6Изменить верхнюю локацию"
            , EditorButtonType.current(PLACEHOLDER_REGION_CUBOID_MAX)
            , EditorButtonType.click("Нажмите, чтобы изменить\nЗажмите ШИФТ, чтобы удалить")
    ),

    REGION_SHOW(Material.LANTERN, "&6Показать кубоид эффектом"
            , EditorButtonType.click("Нажмите, чтобы показать/убрать")
    ),

    REGION_ACTIVE_OBJECT(Material.ACACIA_DOOR, "&6Настройка активного региона"
            , EditorButtonType.current("&7Активных действий: &6" + Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE +
            "\n&7Радиус действия: &6" + Placeholders.PLACEHOLDER_ACTION_RADIUS)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),
    REGION_ACTIVE_EVENTS_OBJECT(Material.OBSERVER, "&6Список ивент обработчиков"
            , EditorButtonType.current("Ивентов: &6" + Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),
    REGION_ACTIVE_COOLDOWNS(Material.CLOCK, "&6Настройка задержек"
            //, EditorButtonType.current("Ивентов с задержками: &6" + Placeholders.PLACEHOLDER_REGION_ACTION_SIZE)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ), // no need now
    ACTION_SELECT_CONDITIONS(Material.REDSTONE_TORCH, "&6Условие"
            , EditorButtonType.info("&fУсловие на ивенте: " + Placeholders.PLACEHOLDER_EVENTS_NAME)
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE)
            , EditorButtonType.click("ЛКМ, чтобы добавить условие\nПКМ, чтобы удалить последние условие\nНажмите с ШИФТОМ, чтобы очистить")
    ),
    ACTION_SELECT_EXECUTORS(Material.COMMAND_BLOCK, "&6Выполнение действия"
            , EditorButtonType.info("&fУсловие на ивенте: " + Placeholders.PLACEHOLDER_EVENTS_NAME)
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE)
            , EditorButtonType.click("ЛКМ, чтобы добавить условие\nПКМ, чтобы удалить последние условие\nНажмите с ШИФТОМ, чтобы очистить")
    ),
    ACTION_SELECT_FAIL(Material.HOPPER, "&6Ошибка при выполнении"
            , EditorButtonType.info("&fУсловие на ивенте: " + Placeholders.PLACEHOLDER_EVENTS_NAME)
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID)
            , EditorButtonType.click("Нажмите, чтобы изменить ивент\nНажмите с ШИФТОМ, чтобы очистить")
    ),

    ACTION_CONDITIONS_LIST_OBJECT(Material.REDSTONE_TORCH, "&6Список условий"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),
    ACTION_EXECUTORS_OBJECT(Material.COMMAND_BLOCK, "&6Список действий"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),
    ACTION_FAIL_ACTIONS_OBJECT(Material.HOPPER, "&6ID действия при ошибке"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),

    ACTION_CREATE_CONDITION(Material.ANVIL, "&aСоздать условие"),
    ACTION_CREATE_EXECUTOR(Material.ANVIL, "&aСоздать действие"),
    ACTION_CREATE_FAIL(Material.ANVIL, "&aСоздать ошибку"),

    ACTION_CHANGE_RADIUS(Material.ELYTRA, "&6Радиус действия"
            , EditorButtonType.current(PLACEHOLDER_ACTION_RADIUS)
            , EditorButtonType.click("Нажмите, чтобы изменить")
    ),

    EVENTS_CHANGE_CANCELLED(Material.WARPED_FENCE_GATE, "Отмена действия"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_CANCELLED)
            , EditorButtonType.click("Нажмите, чтобы изменить")
    ),
    EVENTS_CHANGE_PERMISSION(Material.COMMAND_BLOCK_MINECART, "Право на действие"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_PERMISSION)
            , EditorButtonType.click("Нажмите, чтобы изменить")
    ),
    EVENTS_CHANGE_LANGKEY(Material.FLOWER_BANNER_PATTERN, "Сообщение при кд"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_LANGKEY)
            , EditorButtonType.click("Нажмите, чтобы изменить")
    ),
    EVENTS_CHANGE_COOLDOWN(Material.CLOCK, "КД в секундах"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_COOLDOWN)
            , EditorButtonType.click("Нажмите, чтобы изменить")
    ),

    EVENTS_OBJECT(Material.BEDROCK, "Ивент обработчик"
            , EditorButtonType.info("&7Ивент: " + PLACEHOLDER_EVENTS_NAME),
            "&7Задержка: &6" + PLACEHOLDER_EVENTS_COOLDOWN,
            "&7Сообщение при задержке:\n&f" + PLACEHOLDER_EVENTS_LANGKEY,
            "&7Право доступа: &6" + PLACEHOLDER_EVENTS_PERMISSION,
            "&7Ивент отменен: &6" + PLACEHOLDER_EVENTS_CANCELLED
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),

    EVENTS_CREATE(Material.ANVIL, "Создать новый обработчик", EditorButtonType.click("Нажмите, чтобы создать")),
    MANIPULATOR_CREATE_ACTION(Material.ANVIL, "Создать новое действие", EditorButtonType.click("Нажмите, чтобы создать")),

    EVENTS_MANIPULATORS_OBJECT(Material.COMMAND_BLOCK, "Действия ивента"
            , EditorButtonType.info("Список действий: " + Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),

    EVENTS_MANIPULATOR_ACTION_OBJECT(Material.COMMAND_BLOCK, "Действие #" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTIN_ID
            , EditorButtonType.info(
            "&7Условий: &6" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE
                    + "\n&7Обработчиков: &6" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE
                    + "\n&7Ошибочное действие: ID=" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID)
            , EditorButtonType.click("Нажмите, чтобы перейти")
    ),
    ;


    private final Material material;
    private String name;
    private List<String> lore;

    EditorType() {
        this(Material.AIR, "", "");
    }

    EditorType(@NotNull Material material, @NotNull String name, @NotNull String... lores) {
        this.material = material;
        this.setName(name);
        this.setLore(EditorButtonType.fineLore(lores));
    }

    @NotNull
    @Override
    public Material getMaterial() {
        return material;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = StringUtil.color(name);
    }

    @NotNull
    public List<String> getLore() {
        return lore;
    }

    public void setLore(@NotNull List<String> lore) {
        this.lore = StringUtil.color(new ArrayList<>(lore));
    }
}
