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

    CREATE_REGION(Material.ANVIL, "&a������� ������"
            , EditorButtonType.click("�������, ����� �������")
    ),

    REGION_CHANGE_NAME(Material.NAME_TAG, "&6�������� ��������"
            , EditorButtonType.current(PLACEHOLDER_REGION_NAME)
            , EditorButtonType.click("�������, ����� ��������")
    ),

    REGION_CHANGE_CUBOID_FIRST(Material.PAPER, "&6�������� ������ �������"
            , EditorButtonType.current(PLACEHOLDER_REGION_CUBOID_FIRST)
            , EditorButtonType.click("�������, ����� ��������\n������� ����, ����� �������")
    ),
    REGION_CHANGE_CUBOID_SECOND(Material.PAPER, "&6�������� ������ �������"
            , EditorButtonType.current(PLACEHOLDER_REGION_CUBOID_SECOND)
            , EditorButtonType.click("�������, ����� ��������\n������� ����, ����� �������")
    ),

    REGION_ACTIVE_OBJECT(Material.ACACIA_DOOR, "&6��������� ��������� �������"
            , EditorButtonType.current("&7�������� ��������: &6" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_SIZE +
            "\n&7������ ��������: &6" + Placeholders.PLACEHOLDER_ACTION_RADIUS)
            , EditorButtonType.click("�������, ����� �������")
    ),
    REGION_ACTIVE_EVENTS_OBJECT(Material.OBSERVER, "&6������ ����� ������������"
            , EditorButtonType.current("�������: &6" + Placeholders.PLACEHOLDER_ACTION_EVENTS_SIZE)
            , EditorButtonType.click("�������, ����� �������")
    ),
    REGION_ACTIVE_COOLDOWNS(Material.CLOCK, "&6��������� ��������"
            //, EditorButtonType.current("������� � ����������: &6" + Placeholders.PLACEHOLDER_REGION_ACTION_SIZE)
            , EditorButtonType.click("�������, ����� �������")
    ), // no need now
    ACTION_SELECT_CONDITIONS(Material.REDSTONE_TORCH, "&6�������"
            , EditorButtonType.info("&f������� �� ������: " + Placeholders.PLACEHOLDER_EVENTS_NAME)
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE)
            , EditorButtonType.click("���, ����� �������� �������\n���, ����� ������� ��������� �������\n������� � ������, ����� ��������")
    ),
    ACTION_SELECT_EXECUTORS(Material.COMMAND_BLOCK, "&6���������� ��������"
            , EditorButtonType.info("&f������� �� ������: " + Placeholders.PLACEHOLDER_EVENTS_NAME)
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE)
            , EditorButtonType.click("���, ����� �������� �������\n���, ����� ������� ��������� �������\n������� � ������, ����� ��������")
    ),
    ACTION_SELECT_FAIL(Material.HOPPER, "&6������ ��� ����������"
            , EditorButtonType.info("&f������� �� ������: " + Placeholders.PLACEHOLDER_EVENTS_NAME)
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID)
            , EditorButtonType.click("�������, ����� �������� �����\n������� � ������, ����� ��������")
    ),

    ACTION_CONDITIONS_LIST_OBJECT(Material.REDSTONE_TORCH, "&6������ �������"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE)
            , EditorButtonType.click("�������, ����� �������")
    ),
    ACTION_EXECUTORS_OBJECT(Material.COMMAND_BLOCK, "&6������ ��������"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE)
            , EditorButtonType.click("�������, ����� �������")
    ),
    ACTION_FAIL_ACTIONS_OBJECT(Material.HOPPER, "&6ID �������� ��� ������"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID)
            , EditorButtonType.click("�������, ����� �������")
    ),

    ACTION_CREATE_CONDITION(Material.ANVIL, "&a������� �������"),
    ACTION_CREATE_EXECUTOR(Material.ANVIL, "&a������� ��������"),
    ACTION_CREATE_FAIL(Material.ANVIL, "&a������� ������"),

    ACTION_CHANGE_RADIUS(Material.ELYTRA, "&6������ ��������"
            , EditorButtonType.current(PLACEHOLDER_ACTION_RADIUS)
            , EditorButtonType.click("�������, ����� ��������")
    ),

    EVENTS_CHANGE_CANCELLED(Material.WARPED_FENCE_GATE, "������ ��������"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_CANCELLED)
            , EditorButtonType.click("�������, ����� ��������")
    ),
    EVENTS_CHANGE_PERMISSION(Material.COMMAND_BLOCK_MINECART, "����� �� ��������"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_PERMISSION)
            , EditorButtonType.click("�������, ����� ��������")
    ),
    EVENTS_CHANGE_LANGKEY(Material.FLOWER_BANNER_PATTERN, "��������� ��� ��"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_LANGKEY)
            , EditorButtonType.click("�������, ����� ��������")
    ),
    EVENTS_CHANGE_COOLDOWN(Material.CLOCK, "�� � ��������"
            , EditorButtonType.current(PLACEHOLDER_EVENTS_COOLDOWN)
            , EditorButtonType.click("�������, ����� ��������")
    ),

    EVENTS_OBJECT(Material.BEDROCK, "����� ����������"
            , EditorButtonType.info("&7�����: " + PLACEHOLDER_EVENTS_NAME),
            "&7��������: &6" + PLACEHOLDER_EVENTS_COOLDOWN,
            "&7��������� ��� ��������:\n&f" + PLACEHOLDER_EVENTS_LANGKEY,
            "&7����� �������: &6" + PLACEHOLDER_EVENTS_PERMISSION,
            "&7����� �������: &6" + PLACEHOLDER_EVENTS_CANCELLED
            , EditorButtonType.click("�������, ����� �������")
    ),

    EVENTS_CREATE(Material.ANVIL, "������� ����� ����������", EditorButtonType.click("�������, ����� �������")),
    MANIPULATOR_CREATE_ACTION(Material.ANVIL, "������� ����� ��������", EditorButtonType.click("�������, ����� �������")),

    EVENTS_MANIPULATORS_OBJECT(Material.COMMAND_BLOCK, "�������� ������"
            , EditorButtonType.info("������ ��������: " + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_SIZE)
            , EditorButtonType.click("�������, ����� �������")
    ),

    EVENTS_MANIPULATOR_ACTION_OBJECT(Material.COMMAND_BLOCK, "�������� #" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTIN_ID
            , EditorButtonType.info(
            "&7�������: &6" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_CONDITIONS_SIZE
                    + "\n&7������������: &6" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_EXECUTORS_SIZE
                    + "\n&7��������� ��������: ID=" + Placeholders.PLACEHOLDER_EVENTS_MANIPULATOR_ACTION_FAIL_ID)
            , EditorButtonType.click("�������, ����� �������")
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
