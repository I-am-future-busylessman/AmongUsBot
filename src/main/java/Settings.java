import lombok.AllArgsConstructor;
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
    private Integer hardTasks;
    private Integer imposterKD;
    private Integer impostersCount;
    private Map<Integer,Integer> easyTasksMap = new HashMap<>();
    private Map<Integer,Integer> normalTasksMap = new HashMap<>();
    private Map<Integer,Integer> hardTasksMap = new HashMap<>();
    private Map<String, Integer> sabotageSolvers = new HashMap<>();

    public Settings(Integer players, Integer easyTasks, Integer normalTasks, Integer hardTasks, Integer imposterKD, Integer impostersCount) {
        this.players = players;
        this.easyTasks = easyTasks;
        this.normalTasks = normalTasks;
        this.hardTasks = hardTasks;
        this.imposterKD = imposterKD;
        this.impostersCount = impostersCount;
        makeSabotageSolvers();
        makeEasyTasks();
        makeNormalTasks();
        makeHardTasks();
    }

    public int getTask(int task){
        if (task / 10 == 3){
            return hardTasksMap.get(task % 10);
        }else if (task / 10 == 2){
            return normalTasksMap.get(task % 10);
        }else{
            return easyTasksMap.get(task % 10);
        }
    }

    public void makeSabotageSolvers() {
        Map<String, Integer> sabotageTypes = new HashMap<>();
        sabotageTypes.put("Свет", 2020);
        sabotageTypes.put("Реактор", 3030);
        sabotageTypes.put("Связь", 4040);
        sabotageTypes.put("Кислород", 1010);
        sabotageSolvers = sabotageTypes;

    }

    public void makeEasyTasks(){
        Map<Integer,Integer> easyTasksMap = new HashMap<>();
        easyTasksMap.put(0, 1000);
        easyTasksMap.put(1, 1111);
        easyTasksMap.put(2, 2222);
        easyTasksMap.put(3, 3333);
        easyTasksMap.put(4, 4444);
        easyTasksMap.put(5, 5555);
        easyTasksMap.put(6, 6666);
        easyTasksMap.put(7, 7777);
        easyTasksMap.put(8, 8888);
        easyTasksMap.put(9, 9999);
        this.easyTasksMap = easyTasksMap;
    }

    public void makeNormalTasks(){
        Map<Integer,Integer> normalTasksMap = new HashMap<>();
        normalTasksMap.put(0, 1101);
        normalTasksMap.put(1, 1212);
        normalTasksMap.put(2, 2323);
        normalTasksMap.put(3, 3434);
        normalTasksMap.put(4, 4545);
        normalTasksMap.put(5, 5656);
        normalTasksMap.put(6, 6767);
        normalTasksMap.put(7, 7878);
        normalTasksMap.put(8, 8989);
        normalTasksMap.put(9, 9090);
        this.normalTasksMap = normalTasksMap;
    }

    public void makeHardTasks(){
        Map<Integer,Integer> hardTasksMap = new HashMap<>();
        hardTasksMap.put(0, 1123);
        hardTasksMap.put(1, 1234);
        hardTasksMap.put(2, 2345);
        hardTasksMap.put(3, 3456);
        hardTasksMap.put(4, 4567);
        hardTasksMap.put(5, 5678);
        hardTasksMap.put(6, 6789);
        hardTasksMap.put(7, 7890);
        hardTasksMap.put(8, 8901);
        hardTasksMap.put(9, 9012);
        this.hardTasksMap = hardTasksMap;
    }

    public String getAllSettings(){
        String str = "";
        str += getPlayers().toString() + " ";
        str += getEasyTasks().toString() + " ";
        str += getNormalTasks().toString() + " ";
        str += getHardTasks().toString() + " ";
        str += getImposterKD().toString() + " ";
        str += getImpostersCount();
        return str;
    }
}
