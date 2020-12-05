import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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
    private Map<String, Integer> sabotageSolvers = new HashMap<>();
    private Map<Integer, Integer> availableTasks = new HashMap<>();

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
        Map<String, Integer> sabotageTypes = new HashMap<>();
        sabotageTypes.put("Свет", 443355);
        sabotageTypes.put("Реактор", 915677);
        sabotageTypes.put("Связь", 784212);
        sabotageTypes.put("Кислород", 672459);
        sabotageSolvers = sabotageTypes;

    }

    public void makeEasyTasks(){
        Map<Integer,Integer> easyTasksMap = new HashMap<>();
        easyTasksMap.put(5, 9632);//ловец
        easyTasksMap.put(6, 63);//бэт
        easyTasksMap.put(7, 9475);//холл
        easyTasksMap.put(8, 6951);//лабиринт
        this.easyTasksMap = easyTasksMap;
    }

    public void makeNormalTasks(){
        Map<Integer,Integer> normalTasksMap = new HashMap<>();
        normalTasksMap.put(5, 4779);//маньяк
        normalTasksMap.put(6, 46);//бэт
        normalTasksMap.put(7, 7563);//диваны за шторами
        normalTasksMap.put(8, 1484);//лабиринт
        normalTasksMap.put(9, 7301);
        this.normalTasksMap = normalTasksMap;
    }

    public void makeHardTasks(){
        Map<Integer,Integer> hardTasksMap = new HashMap<>();
        hardTasksMap.put(0, 8713);//
        hardTasksMap.put(1, 4);//
        hardTasksMap.put(3, 6983);//колонка
        hardTasksMap.put(5, 1457);//лабиринт второй этаж
        hardTasksMap.put(6, 16);//раздевалка
        hardTasksMap.put(7, 9231);//раздевалка корридор
        hardTasksMap.put(8, 3396);//ловец
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
