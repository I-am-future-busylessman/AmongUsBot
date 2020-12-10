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
    private Map<Integer,Integer> easyTasksMap = new HashMap<>();
    private Map<Integer,Integer> normalTasksMap = new HashMap<>();
    private Map<Integer,Integer> timerTasksMap = new HashMap<>();
    private ArrayList<Integer> lightSolvers = new ArrayList<>();
    private ArrayList<Integer> reactorSolvers = new ArrayList<>();
    private ArrayList<Integer> oxygenSolvers = new ArrayList<>();
    private ArrayList<Integer> networkSolvers = new ArrayList<>();
    private Map<Integer, Integer> availableTasks = new HashMap<>();
    public Map<String, ArrayList<Integer>> sabotageSolvers = new HashMap<>();

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

    public int getTask(int task){
        if (task / 10 == 3){
            return timerTasksMap.get(task % 10);
        }else if (task / 10 == 2){
            return normalTasksMap.get(task % 10);
        }else{
            return easyTasksMap.get(task % 10);
        }
    }

    public void makeSabotageSolvers() {
        lightSolvers.add(880055);
        lightSolvers.add(312456);
        lightSolvers.add(784212);
        lightSolvers.add(443355);
        lightSolvers.add(915677);
        reactorSolvers.add(972830);
        reactorSolvers.add(672459);
        reactorSolvers.add(840981);
        reactorSolvers.add(123123);
        reactorSolvers.add(101010);
        oxygenSolvers.add(231231);
        oxygenSolvers.add(141315);
        oxygenSolvers.add(965439);
        oxygenSolvers.add(902930);
        oxygenSolvers.add(145367);
        networkSolvers.add(324325);
        networkSolvers.add(495343);
        networkSolvers.add(123453);
        networkSolvers.add(114325);
        networkSolvers.add(774459);
        sabotageSolvers.put("Свет", lightSolvers);
        sabotageSolvers.put("Реактор", reactorSolvers);
        sabotageSolvers.put("Кислород", oxygenSolvers);
        sabotageSolvers.put("Связь", networkSolvers);
    }

    public void makeEasyTasks(){
        Map<Integer,Integer> easyTasksMap = new HashMap<>();
        easyTasksMap.put(0, 0);//вписать новый код
        easyTasksMap.put(2, 6941);
        easyTasksMap.put(3, 4571);
        easyTasksMap.put(4, 9783);
        easyTasksMap.put(5, 0);//вписать новый код
        easyTasksMap.put(6, 3);
        easyTasksMap.put(7, new Date().getHours()*100 + new Date().getMinutes());
        this.easyTasksMap = easyTasksMap;
    }

    public void makeNormalTasks(){
        Map<Integer,Integer> normalTasksMap = new HashMap<>();
        normalTasksMap.put(0, 9475);
        normalTasksMap.put(1, 3915);
        normalTasksMap.put(2, 32);
        normalTasksMap.put(3, 9039);
        normalTasksMap.put(4, 0);//вписать новый код
        normalTasksMap.put(5, 2147);
        normalTasksMap.put(6, 9120);
        normalTasksMap.put(7, 27);
        normalTasksMap.put(8, 8160);
        this.normalTasksMap = normalTasksMap;
    }

    public void makeHardTasks(){
        Map<Integer,Integer> hardTasksMap = new HashMap<>();
        hardTasksMap.put(0, 2756);//
        hardTasksMap.put(1, 4);//
        hardTasksMap.put(2, 7361);
        hardTasksMap.put(3, 4);
        hardTasksMap.put(4, 4);
        hardTasksMap.put(5, 0);//вписать новый код
        hardTasksMap.put(7, new Date().getHours()*100 + new Date().getMinutes());
        hardTasksMap.put(8, 0);//вписать новый код
        this.timerTasksMap = hardTasksMap;
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

    public void removeTask(Integer key){
        timerTasksMap.remove(key);
    }

    public void addTask(Integer key, Integer value){
        timerTasksMap.put(key, value);

    }

    public boolean checkAvailableTasks(Integer number){
        if (number/10 == 1)
            return easyTasksMap.containsKey(number%10);
        else if (number/10 == 2)
            return normalTasksMap.containsKey(number%10);
        else
            return timerTasksMap.containsKey(number%10);
    }
}
