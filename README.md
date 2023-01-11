# RegionCommand - Features region controller
<center><b>Region controller with more features:</b></center>
<center><h1>AUTOMINE</h1></center>

* <h4>[REGION_RESTORE_BLOCK] ~name: COBBLESTONE,STONE; ~type: BEDROCK; ~amount: 10;</h4>
name - блоки, которые будут доступны для ломания; type - блок реплейсер; amount - через сколько секунд блок будет восстановлен

<center><h1>PREVENT CANCELLED EVENT</h1></center>

* <h4>[REGION_COMMAND_BLOCK_EVENT]: ~name: BLOCK_BREAK;</h4>
name - Название ивента, который будет отменен
* Пояснение: Отменяем любой ивент в любом другом ивенте. Допустим, игрок бегает по региону и пока он бегает, он не может выполнять ивент BLOCK_BREAK

<center><h1>REGION TIMER</h1></center>

* <h4>[REGION_COMMAND_TIMER]: ~name: SHIFT_DOWN; ~amount: 12</h4>
  name - Название ивента, который будет доступен после таймера; amount - Время таймера в секундах
* Пояснение: При входе в регион будет запущен таймер и после его окончания, игроку будет доступен ивент SHIFT_DOWN


>Так-как на привате не создает Wiki, то я буду тут писать информацию

<center><h3>Permissions</h3></center>

><h5>regioncommands.region.bypass.*</h5>  доступ к любому региону без всяких проверок
