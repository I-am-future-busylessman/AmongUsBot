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
    private ArrayList<String> lightSolvers = new ArrayList<>();
    private ArrayList<String> reactorSolvers = new ArrayList<>();
    private ArrayList<String> oxygenSolvers = new ArrayList<>();
    private ArrayList<String> networkSolvers = new ArrayList<>();
    public Map<String, ArrayList<String>> sabotageSolvers = new HashMap<>();

    public Settings(Integer players, Integer easyTasks, Integer normalTasks, Integer timerTasks, Integer imposterKD, Integer impostersCount) {
        this.players = players;
        this.easyTasks = easyTasks;
        this.normalTasks = normalTasks;
        this.timerTasks = timerTasks;
        this.imposterKD = imposterKD;
        this.impostersCount = impostersCount;
        makeSabotageSolvers();
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

    public void makeLightSolvers(){
        lightSolvers.add("880055");
        lightSolvers.add("312456");
        lightSolvers.add("784212");
        lightSolvers.add("443355");
        lightSolvers.add("915677");
        sabotageSolvers.put("Свет", lightSolvers);
    }

    public void makeReactorSolvers(){
        reactorSolvers.add("972830");
        reactorSolvers.add("672459");
        reactorSolvers.add("840981");
        reactorSolvers.add("123123");
        reactorSolvers.add("101010");
        sabotageSolvers.put("Реактор", reactorSolvers);
    }

    public void makeOxygenSolvers(){
        oxygenSolvers.add("231231");
        oxygenSolvers.add("141315");
        oxygenSolvers.add("965439");
        oxygenSolvers.add("902930");
        oxygenSolvers.add("145367");
        sabotageSolvers.put("Кислород", oxygenSolvers);
    }

    public void makeNetworkSolvers(){
        networkSolvers.add("324325");
        networkSolvers.add("495343");
        networkSolvers.add("123453");
        networkSolvers.add("114325");
        networkSolvers.add("774459");
        sabotageSolvers.put("Связь", networkSolvers);
    }

    public void makeSabotageSolvers() {
        makeLightSolvers();
        makeNetworkSolvers();
        makeOxygenSolvers();
        makeReactorSolvers();
    }

    public void makeEasyTasks(){
        Map<Integer,TaskVersions> easyTasksMap = new HashMap<>();
        TaskVersions task = new TaskVersions();
        task.putTaskVersion(0, new Task("Рассортирую папки с данными в алфавитном порядке.", "Добавить"));
        task.putTaskVersion(1, new Task("Рассортируй папки c данными по первой букве их цвета.", "Добавить"));
        task.putTaskVersion(2, new Task("Расставь папки по порядку: Синий, Красный, Фиолетовый, Зелёный", "Добавить"));
        //task.putTaskVersion(3, new Task("Расставь папки по количеству листов в них.", "Добавить"));
        easyTasksMap.put(0, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их.", "Добавить"));
        task.putTaskVersion(1, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их. Введи только чётные числа кода.", "Добавить"));
        task.putTaskVersion(2, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их. Введи только нечётные числа кода.", "Добавить"));
        task.putTaskVersion(3, new Task("Проверь работу защитных щитов. Активируй, а затем дезактивируй их. Введи сумму чисел кода.", "Добавить"));
        easyTasksMap.put(1, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Расположи цифры в правильном порядке и переверни панель.", "Добавить"));
        task.putTaskVersion(1, new Task("Расположи цифры в правильном порядке и переверни панель. Введи только чётные числа кода.", "Добавить"));
        task.putTaskVersion(2, new Task("Расположи цифры в правильном порядке и переверни панель. Введи только нечётные числа кода.", "Добавить"));
        task.putTaskVersion(3, new Task("Расположи цифры в правильном порядке и переверни панель. Введи сумму чисел кода.", "Добавить"));
        easyTasksMap.put(2, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Кто дойдёт до кнопки.", "Добавить"));
        task.putTaskVersion(1, new Task("Кто дойдёт до лаборатории.", "Добавить"));
        task.putTaskVersion(2, new Task("Кто дойдёт до столовой.", "Добавить"));
        easyTasksMap.put(3, task);
        //task = new TaskVersions();
        //task.putTaskVersion(0, new Task("Подбери ключ от замка под номером 1", "Добавить"));
        //task.putTaskVersion(1, new Task("Подбери ключ от замка под номером 2", "Добавить"));
        //task.putTaskVersion(2, new Task("Подбери ключ от замка под номером 3", "Добавить"));
        //easyTasksMap.put(4, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Собери обед из следующих блюд: <<Борщ, Оливье, Медовик, Компот>>.", "Добавить"));
        task.putTaskVersion(1, new Task("Собери обед из следующих блюд: <<Фо Бо, Фуагра, Финики, Фанта>>.", "Добавить"));
        task.putTaskVersion(2, new Task("Собери обед из следующих блюд: <<Картофельное пюре, Сосиски, Цезарь, Сок>>.", "Добавить"));
        easyTasksMap.put(4, task);
        this.easyTasksMap = easyTasksMap;
    }

    public void makeNormalTasks(){
        Map<Integer,TaskVersions> normalTasksMap = new HashMap<>();
        TaskVersions task = new TaskVersions();
        task.putTaskVersion(0, new Task("Взвесь метеориты. Найди самый тяжёлый.\n Округляй до десятков.", "Добавить"));
        task.putTaskVersion(1, new Task("Взвесь метеориты. Найди самый лёгкий.\n Округляй до десятков.", "Добавить"));
        task.putTaskVersion(2, new Task("Взвесь метеориты. Посчитай их общую массу.\n Округляй до десятков.", "Добавить"));
        normalTasksMap.put(0, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Рассортируй шарики по цветам Посчитай количество <<ЦВЕТ1>>", "Добавить"));
        task.putTaskVersion(1, new Task("Рассортируй шарики по цветам Посчитай количество <<ЦВЕТ2>>", "Добавить"));
        task.putTaskVersion(2, new Task("Рассортируй шарики по цветам Посчитай количество <<ЦВЕТ3>>", "Добавить"));
        normalTasksMap.put(1, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Реши пример. X = 60, Y = 36, Z = 16", "Добавить"));
        task.putTaskVersion(1, new Task("Реши пример. X = 99, Y = 45, Z = 17", "Добавить"));
        task.putTaskVersion(2, new Task("Реши пример. X = -30, Y = 6, Z = -13", "Добавить"));
        normalTasksMap.put(2, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Измерь длину ботинка скафандра.", "Добавить"));
        task.putTaskVersion(1, new Task("Измерь ширину ботинка скафандра.", "Добавить"));
        task.putTaskVersion(2, new Task("Измерь высоту ботинка скафандра.", "Добавить"));
        normalTasksMap.put(3, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Посчитай сколько весит весь груз на корабле.", "Добавить"));
        task.putTaskVersion(1, new Task("Посчитай сколько весят все продукты на корабле.", "Добавить"));
        task.putTaskVersion(2, new Task("Посчитай сколько весят все медикаменты на корабле.", "Добавить"));
        normalTasksMap.put(4, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Найди всех астронавтов на картинке.", "Добавить"));
        task.putTaskVersion(1, new Task("Найди всех красных астронавтов на картинке.", "Добавить"));
        task.putTaskVersion(2, new Task("Найди всех синих астронавтов на картинке.", "Добавить"));
        task.putTaskVersion(3, new Task("Найди всех желтых астронавтов на картинке.", "Добавить"));
        task.putTaskVersion(4, new Task("Найди всех зелёных астронавтов на картинке.", "Добавить"));
        normalTasksMap.put(5, task);
        this.normalTasksMap = normalTasksMap;
    }

    public void makeHardTasks(){
        Map<Integer,TaskVersions> hardTasksMap = new HashMap<>();
        TaskVersions task = new TaskVersions();
        task.putTaskVersion(0, new Task("Найди весь металлический мусор, введи количество.", "Добавить"));
        task.putTaskVersion(1, new Task("Найди весь бумажный мусор, введи количество.", "Добавить"));
        task.putTaskVersion(2, new Task("Найди весь пластиковый мусор, введи количество.", "Добавить"));
        hardTasksMap.put(0, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Расшифруй послание номер 1.", "Добавить"));
        task.putTaskVersion(1, new Task("Расшифруй послание номер 2", "Добавить"));
        task.putTaskVersion(2, new Task("Расшифруй послание номер 3", "Добавить"));
        hardTasksMap.put(1, task);
        task = new TaskVersions();
        task.putTaskVersion(0, new Task("Собери код по территории хранилища. Тебе нужны данные помеченные вертикальными линиями.", "Добавить"));
        task.putTaskVersion(1, new Task("Собери код по территории хранилища. Тебе нужны данные помеченные горизонтальными линиями.", "Добавить"));
        task.putTaskVersion(2, new Task("Собери код по территории хранилища. Тебе нужны данные без пометок.", "Добавить"));
        hardTasksMap.put(2, task);
        this.hardTasksMap = hardTasksMap;
    }

    public String getAllSettings(){
        String str = "";
        str += getPlayers().toString() + " ";
        str += getEasyTasks().toString() + " ";
        str += getNormalTasks().toString() + " ";
        str += getTimerTasks().toString() + " ";
        str += getImposterKD().toString() + " ";
        str += getImpostersCount();
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
