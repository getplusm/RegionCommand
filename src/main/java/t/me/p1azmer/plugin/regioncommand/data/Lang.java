package t.me.p1azmer.plugin.regioncommand.data;

import t.me.p1azmer.aves.engine.api.lang.LangKey;

public class Lang {

    public static LangKey Permission_Event_Enter = LangKey.of("Permission.Event.Enter",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Leave = LangKey.of("Permission.Event.Leave",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Move = LangKey.of("Permission.Event.Move",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Shift = LangKey.of("Permission.Event.Shift",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Inside = LangKey.of("Permission.Event.Inside",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_LMB = LangKey.of("Permission.Event.LMB",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_RMB = LangKey.of("Permission.Event.RMB",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Commands = LangKey.of("Permission.Event.Commands",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Place = LangKey.of("Permission.Event.Place",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Break = LangKey.of("Permission.Event.Break",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Permission_Event_Jump = LangKey.of("Permission.Event.Jump",
            """
                    {message: ~prefix: false;}&cУ Вас нет доступа к этому региону. Купите статус выше вашего, чтобы войти
                    &fПокупка на сайте: https://avesworld.ru
                    """);
    public static LangKey Cooldown_Event_Enter = LangKey.of("Cooldown.Event.Enter", "{message: ~prefix: false;}&cВы не можете войти в эту зону так быстро. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_Leave = LangKey.of("Cooldown.Event.Leave", "{message: ~prefix: false;}&cВы не можете выйти из этой зоны так быстро. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_Move = LangKey.of("Cooldown.Event.Move", "{message: ~prefix: false;}&cВы не можете ходить в этой зоне так быстро. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_Shift = LangKey.of("Cooldown.Event.Shift", "{message: ~prefix: false;}&cВы не можете использовать ШИФТ в этой зоне так быстро. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_Inside = LangKey.of("Cooldown.Event.Inside", "{message: ~prefix: false;}&cВы не можете ничего делать в этой зоне. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_LMB = LangKey.of("Cooldown.Event.LMB", "{message: ~prefix: false;}&cВы не можете ничего делать в этой зоне. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_RMB = LangKey.of("Cooldown.Event.RMB", "{message: ~prefix: false;}&cВы не можете ничего делать в этой зоне. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_Commands = LangKey.of("Cooldown.Event.Commands", """
            {message: ~prefix: false;}&cВы не можете использовать команды в этой зоне так быстро!
            &cОжидайте %time% %time_correct%
            """);
    public static LangKey Cooldown_Event_Jump = LangKey.of("Cooldown.Event.Jump", """
            {message: ~prefix: false;}&cВы не можете прыгать в этой зоне так часто!
            &cОжидайте %time% %time_correct%
            """);
    public static LangKey Cooldown_Event_Place = LangKey.of("Cooldown.Event.Place", "{message: ~prefix: false;}&cВы не можете ставить блоки так быстро. Ожидайте %time% %time_correct%");
    public static LangKey Cooldown_Event_Break = LangKey.of("Cooldown.Event.Break", "{message: ~prefix: false;}&cВы не можете ломать блоки так быстро. Ожидайте %time% %time_correct%");

    public static LangKey Events_Enter = LangKey.of("Events.Message.Enter", """
            {message: ~type: TITLES; ~fadeIn: 0; ~stay: 40; ~fadeOut: 0;}
            &6Вы вошли в зону тестирования
            &6Напишите в чат, что вы крутой
            """);
    public static LangKey Events_Leave = LangKey.of("Events.Message.Leave", """
            {message: ~type: TITLES; ~fadeIn: 0; ~stay: 40; ~fadeOut: 0;}
            &6Удачи!
            &6Вы вышли из зоны тестирования
            """);
    public static LangKey Events_Move = LangKey.of("Events.Message.Move", """
            {message: ~type: TITLES; ~fadeIn: 0; ~stay: 40; ~fadeOut: 0;}
            &6ничего, вы всё еще крутой
            &6Перемещение в кубоиде
            """);
    public static LangKey Events_Shift = LangKey.of("Events.Message.Shift", """
            Shift
            """);
    public static LangKey Events_Inside = LangKey.of("Events.Message.Inside", """
            Inside
            """);
    public static LangKey Events_LMB = LangKey.of("Events.Message.LMB", """
            LMB
            """);
    public static LangKey Events_RMB = LangKey.of("Events.Message.RMB", """
            RMB
            """);
    public static LangKey Events_Commands = LangKey.of("Events.Message.Commands", """
            Commands
            """);
    public static LangKey Events_Break = LangKey.of("Events.Message.Break", """
            Break
            """);
    public static LangKey Events_Place = LangKey.of("Events.Message.Place", """
            Place
            """);
    public static LangKey Events_Jump = LangKey.of("Events.Message.Jump", """
            Jump
            """);

    public static LangKey EDITOR_REGION_EXIST = LangKey.of("Editor.Region.Exist", "&cРегион с таким ID уже существует!");
    public static LangKey EDITOR_REGION_CREATED = LangKey.of("Editor.Region.Created", "&aРегион создан! Настройте его");
    public static LangKey EDITOR_WRITE_ID = LangKey.of("Editor.Write.Id", "&aВведите уникальный ID");
    public static LangKey EDITOR_REGION_CHANGE_CUBOID_FIRST = LangKey.of("Editor.Region.Cuboid.First", "&aЛокация кубоида #1 установлена!");
    public static LangKey EDITOR_REGION_CHANGE_CUBOID_SECOND = LangKey.of("Editor.Region.Cuboid.Second", "&aЛокация кубоида #2 установлена!");

    public static LangKey EDITOR_REGION_CHANGE_WRITE_NAME = LangKey.of("Editor.Region.Write.Name", "&aВведите название");
    public static LangKey EDITOR_REGION_CHANGE_WRITE_RADIUS = LangKey.of("Editor.Region.Write.Radius", "&aВведите радиус действия");
    public static LangKey EDITOR_REGION_CHANGE_CHANGE_LOCATION = LangKey.of("Editor.Region.Change.Location", "&aВыберите локацию и напишите в чат &b&nnow");
    public static LangKey EDITOR_ERROR_ACTION_SECTION_NOT_FOUND = LangKey.of("Editor.Error.Action.Section.Not_Found", "&cДействия не найдены. Это баг!");
    public static LangKey EDITOR_WRITE_ACTION = LangKey.of("Editor.Write.Action", "&fВведите текст с действием");
    public static LangKey EDITOR_WRITE_INTEGER = LangKey.of("Editor.Write.Integer", "&fВведите число");
    public static LangKey EDITOR_WRITE_EVENTS = LangKey.of("Editor.Write.Events", "&fВведите ивент обработчик");
}
