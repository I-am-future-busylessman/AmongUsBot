import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class Settings {
    private Integer players;
    private Integer easyTasks;
    private Integer normalTasks;
    private Integer timerTasks;
    private Integer imposterKD;
    private Integer impostersCount;
    private Map<Integer, TaskVersions> allTasks = new HashMap<>();
    private Map<Integer,TaskVersions> easyTasksMap = new HashMap<>();
    private Map<Integer,TaskVersions> normalTasksMap = new HashMap<>();
    private Map<Integer,TaskVersions> hardTasksMap = new HashMap<>();


    public Settings(Integer players, Integer easyTasks, Integer normalTasks, Integer timerTasks, Integer imposterKD, Integer impostersCount) {
        this.players = players;
        this.easyTasks = easyTasks;
        this.normalTasks = normalTasks;
        this.timerTasks = timerTasks;
        this.imposterKD = imposterKD;
        this.impostersCount = impostersCount;
        makeEasyTasks();
        makeNormalTasks();
        makeHardTasks();
    }

    public Task getTask(int task){
        if (task / 10 == 3){
            return hardTasksMap.get(task % 10).getTask();
        }else if (task / 10 == 2){
            return normalTasksMap.get(task % 10).getTask();
        }else{
            return easyTasksMap.get(task % 10).getTask();
        }
    }

    public void makeEasyTasks(){
        Map<Integer,TaskVersions> easyTasksMap = new HashMap<>();
        TaskVersions task = new TaskVersions();
        task.putTaskVersion(0, new Task("Рассортирую папки с данными в алфавитном порядке.", "4571"));//
        task.putTaskVersion(1, new Task("Рассортируй папки c данными по первой букве их цвета.", "5417"));
        task.putTaskVersion(2, new Task("Расставь папки по порядку: Синий, Красный, Фиолетовый, Зелёный", "1475"));
        easyTasksMap.put(0, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их.", "6941"));
        task.putTaskVersion(1, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их. Введи только чётные числа кода.", "64"));
        task.putTaskVersion(2, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их. Введи только нечётные числа кода.", "91"));
        task.putTaskVersion(3, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их. Введи сумму чисел кода.", "20"));
        easyTasksMap.put(1, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Расположи цифры в правильном порядке и переверни панель.", "9783"));
        task.putTaskVersion(1, new Task("Расположи цифры в правильном порядке и переверни панель. Введи только чётные числа кода.", "8"));
        task.putTaskVersion(2, new Task("Расположи цифры в правильном порядке и переверни панель. Введи только нечётные числа кода.", "973"));
        task.putTaskVersion(3, new Task("Расположи цифры в правильном порядке и переверни панель. Введи сумму чисел кода.", "27"));
        easyTasksMap.put(2, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Кто дойдёт до кнопки.", "3"));
        task.putTaskVersion(1, new Task("Кто дойдёт до лаборатории.", "2"));
        task.putTaskVersion(2, new Task("Кто дойдёт до столовой.", "1"));
        easyTasksMap.put(3, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Подбери ключ от замка под номером 1", "0088"));
        task.putTaskVersion(1, new Task("Подбери ключ от замка под номером 2", "8912"));
        task.putTaskVersion(2, new Task("Подбери ключ от замка под номером 3", "2450"));
        easyTasksMap.put(4, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Собери обед из следующих блюд: Борщ, Оливье, Медовик, Чай.", "12302243"));
        task.putTaskVersion(1, new Task("Собери обед из следующих блюд: Солянка, Цезарь, Наполеон, Сок.", "10342856"));
        task.putTaskVersion(2, new Task("Собери обед из следующих блюд: <<Борщ, Цезарь, Медовик, Сок>>.", "12342256"));
        easyTasksMap.put(5, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Найди четырёхзначный код написанный невидимыми чернилами.", "9702"));
        task.putTaskVersion(1, new Task("Найди слово написанное невидимыми чернилами.", "Марс"));
        task.putTaskVersion(2, new Task("Найди что нарисовано невидимыми чернилами.", "Солнце"));
        easyTasksMap.put(6, task);
        this.easyTasksMap = easyTasksMap;
    }

    public void makeNormalTasks(){
        Map<Integer,TaskVersions> normalTasksMap = new HashMap<>();
        TaskVersions task = new TaskVersions();
        task.putTaskVersion(0, new Task("Найди код на стороне А. Обязательно делай это в перчатке.", "791"));
        task.putTaskVersion(1, new Task("Найди код на стороне B. Обязательно делай это в перчатке.", "824"));
        task.putTaskVersion(2, new Task("Найди код на стороне C. Обязательно делай это в перчатке.", "503"));
        normalTasksMap.put(0, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Рассортируй шарики по цветам Посчитай количество серых", "13"));
        task.putTaskVersion(1, new Task("Рассортируй шарики по цветам Посчитай количество розовых", "13"));
        task.putTaskVersion(2, new Task("Рассортируй шарики по цветам Посчитай количество золотых", "13"));
        normalTasksMap.put(1, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Собери команду из космонавтов в скафандрах следующих цветов: Коричневый, Оранжевый, Тёмно-зелёный, Белый", "19183173"));
        task.putTaskVersion(1, new Task("Собери команду из космонавтов в скафандрах следующих цветов: Жёлтый, Салатовый, Розовый, Красный", "45219844"));
        task.putTaskVersion(2, new Task("Собери команду из космонавтов в скафандрах следующих цветов: Синий, Белый, Голубой, Чёрный", "16731310"));
        normalTasksMap.put(2, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Измерь длину ботинка скафандра.", "28"));
        task.putTaskVersion(1, new Task("Измерь ширину ботинка скафандра.", "39"));
        normalTasksMap.put(3, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Собери предложение чёрного цвета.", "В отсеке с кислородом произошла утечка"));
        task.putTaskVersion(1, new Task("Собери предложение красного цвета.", "Под столом в столовой требуется уборка"));
        task.putTaskVersion(2, new Task("Собери предложение зелёного цвета.", "Из щитка в электро-щитовой торчат провода"));
        task.putTaskVersion(2, new Task("Собери предложение оранжевого цвета.", "У двери реактора сломан датчик"));
        task.putTaskVersion(2, new Task("Собери предложение розового цвета.", "За автоматом с едой прячется предатель"));
        normalTasksMap.put(4, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Найди всех астронавтов на картинке.", "46"));
        task.putTaskVersion(1, new Task("Найди всех красных астронавтов на картинке.", "12"));
        task.putTaskVersion(2, new Task("Найди всех синих астронавтов на картинке.", "11"));
        task.putTaskVersion(3, new Task("Найди всех желтых астронавтов на картинке.", "11"));
        task.putTaskVersion(4, new Task("Найди всех зелёных астронавтов на картинке.", "12"));
        normalTasksMap.put(5, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Посчитай сколько астероидов на этом видео\nhttps://youtu.be/qkQfE8mqHFU", "14"));
        task.putTaskVersion(1, new Task("Посчитай сколько астероидов на этом видео\nhttps://youtu.be/CcBc3ssVKrg", "17"));
        task.putTaskVersion(2, new Task("Посчитай сколько астероидов на этом видео\nhttps://youtu.be/78w52BCTp8A", "19"));
        normalTasksMap.put(6, task);
        this.normalTasksMap = normalTasksMap;
    }

    public void makeHardTasks(){
        Map<Integer,TaskVersions> hardTasksMap = new HashMap<>();
        TaskVersions task = new TaskVersions();
        task.putTaskVersion(0, new Task("Найди весь металлический мусор, введи количество.", "17"));
        task.putTaskVersion(1, new Task("Найди все батарейки, введи количество.", "9"));
        task.putTaskVersion(2, new Task("Найди весь пластиковый мусор, введи количество.", "4"));
        hardTasksMap.put(0, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Сопоставь цветы и подставки по цвету: зелёный, белый, чёрный, коричневый", "6839"));
        task.putTaskVersion(1, new Task("Сопоставь цветы и подставки по спецсимволам: треугольник, квадрат, звёздочка, круг", "9836"));
        task.putTaskVersion(2, new Task("Сопоставь цветы и подставки по парам:\n" +
                            "Зелёный горшок - белая подставка\n" +
                            "Чёрный горшок - зелёная подставка\n" +
                            "Белый горшок - коричневая подставка" +
                            "Коричневый горшок - чёрная подставка\n", "3689"));
        hardTasksMap.put(1, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Собери код по территории хранилища. Тебе нужны данные помеченные вертикальными линиями.", "6180"));
        task.putTaskVersion(1, new Task("Собери код по территории хранилища. Тебе нужны данные помеченные горизонтальными линиями.", "4124"));
        task.putTaskVersion(2, new Task("Собери код по территории хранилища. Тебе нужны данные без пометок.", "7361"));
        hardTasksMap.put(2, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Посчитай сколько весит весь груз на корабле.", "8888"));
        task.putTaskVersion(1, new Task("Посчитай сколько весят все продукты на корабле.", "9999"));
        task.putTaskVersion(2, new Task("Посчитай сколько весят все медикаменты на корабле.", "1823"));
        hardTasksMap.put(3, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Реши пример. X = 60, Y = 36, Z = 16", "32"));
        task.putTaskVersion(1, new Task("Реши пример. X = 99, Y = 45, Z = 17", "55"));
        task.putTaskVersion(2, new Task("Реши пример. X = -30, Y = 6, Z = -13", "-5"));
        hardTasksMap.put(4, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Взвесь метеориты. Напиши вес самого тяжёлого, с точностью до десятых.", "1070"));
        task.putTaskVersion(1, new Task("Взвесь метеориты. Напиши вес самого лёгкого, с точностью до десятых.", "910"));
        task.putTaskVersion(2, new Task("Взвесь метеориты. Напиши общий вес, с точностью до десятых.", "4000"));
        hardTasksMap.put(5, task);
        this.hardTasksMap = hardTasksMap;
    }

    public String getAllSettings(){
        String str = "";
        str += "Количество игроков: " + getPlayers().toString() + "\n";
        str += "Количество простых заданий: " + getEasyTasks().toString() + "\n";
        str += "Количество средних заданий:" + getNormalTasks().toString() + "\n";
        str += "Количество сложных заданий: " + getTimerTasks().toString() + "\n";
        str += "Перезарядка импостера: " + getImposterKD().toString() + "\n";
        str += "Количество импостеров: " + getImpostersCount() + "\n";
        return str;
    }

    public boolean checkAvailableTasks(Integer number){
        if (number/10 == 1)
            return easyTasksMap.containsKey(number%10);
        else if (number/10 == 2)
            return normalTasksMap.containsKey(number%10);
        else
            return hardTasksMap.containsKey(number%10);
    }
}
