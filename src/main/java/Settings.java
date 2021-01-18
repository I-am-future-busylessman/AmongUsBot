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

    public void makeSabotageSolvers() {
        lightSolvers.add("880055");
        lightSolvers.add("312456");
        lightSolvers.add("784212");
        lightSolvers.add("443355");
        lightSolvers.add("915677");
        reactorSolvers.add("972830");
        reactorSolvers.add("672459");
        reactorSolvers.add("840981");
        reactorSolvers.add("123123");
        reactorSolvers.add("101010");
        oxygenSolvers.add("231231");
        oxygenSolvers.add("141315");
        oxygenSolvers.add("965439");
        oxygenSolvers.add("902930");
        oxygenSolvers.add("145367");
        networkSolvers.add("324325");
        networkSolvers.add("495343");
        networkSolvers.add("123453");
        networkSolvers.add("114325");
        networkSolvers.add("774459");
        sabotageSolvers.put("Свет", lightSolvers);
        sabotageSolvers.put("Реактор", reactorSolvers);
        sabotageSolvers.put("Кислород", oxygenSolvers);
        sabotageSolvers.put("Связь", networkSolvers);
    }

    public void makeEasyTasks(){
        Map<Integer,TaskVersions> easyTasksMap = new HashMap<>();
        TaskVersions task0 = new TaskVersions();
        task0.putTaskVersion(0, new Task("Что-то не так с порядком букв...", "2980"));
        task0.putTaskVersion(1, new Task("Что-то не так с порядком букв...", "3031"));
        task0.putTaskVersion(2, new Task("Что-то не так с порядком букв...", "1000"));
        easyTasksMap.put(0, task0);
        this.easyTasksMap = easyTasksMap;
    }

    public void makeNormalTasks(){
        Map<Integer,TaskVersions> normalTasksMap = new HashMap<>();
        TaskVersions task0 = new TaskVersions();
        task0.putTaskVersion(0, new Task("Собери квадрат, затем верни все в исходное положение", "2980"));
        task0.putTaskVersion(1, new Task("Собери квадрат, затем верни все в исходное положение", "3031"));
        task0.putTaskVersion(2, new Task("Собери квадрат, затем верни все в исходное положение", "1000"));
        this.normalTasksMap = normalTasksMap;
    }

    public void makeHardTasks(){
        Map<Integer,TaskVersions> hardTasksMap = new HashMap<>();
        TaskVersions task0 = new TaskVersions();
        task0.putTaskVersion(0, new Task("Собери квадрат, затем верни все в исходное положение", "2980"));
        task0.putTaskVersion(1, new Task("Собери квадрат, затем верни все в исходное положение", "3031"));
        task0.putTaskVersion(2, new Task("Собери квадрат, затем верни все в исходное положение", "1000"));
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
