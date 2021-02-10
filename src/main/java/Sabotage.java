import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Sabotage {
    Map<String, String> sabotage = new HashMap<>();
    private String unavailable;
    private String type;
    private boolean status;
    private boolean beforeVote;
    private ArrayList<String> lightSolvers = new ArrayList<>();
    private ArrayList<String> reactorSolvers = new ArrayList<>();
    private ArrayList<String> oxygenSolvers = new ArrayList<>();
    private ArrayList<String> networkSolvers = new ArrayList<>();
    public Map<String, ArrayList<String>> sabotageSolvers = new HashMap<>();

    public Sabotage() {
        unavailable = "все";
        type = "type";
        status = false;
        beforeVote = false;
        makeSabotage();
        makeSabotageSolvers();
    }

    public void makeSabotageSolvers() {
        makeLightSolvers();
        makeNetworkSolvers();
        makeOxygenSolvers();
        makeReactorSolvers();
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
        oxygenSolvers.add("774459");
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

    public void makeSabotage(){
        Map<String, String> sabotage = new HashMap<>();
        sabotage.put("Свет", "гараж");
        sabotage.put("Реактор", "штаб");
        sabotage.put("Связь", "оружейная");
        sabotage.put("Кислород", "хранилище");
        sabotage.forEach((k, v) -> {
            if (v.equals(unavailable))
                sabotage.replace(k, v, "гостиная");
        });
        this.sabotage = sabotage;
    }

    public String getSabotageLocations(){
        String result = "";
        result += "Свет " + sabotage.get("Свет") + "\n";
        result += "Реактор " + sabotage.get("Реактор") + "\n";
        result += "Связь " + sabotage.get("Связь") + "\n";
        result += "Кислород " + sabotage.get("Кислород");
        return result;
    }

    public void reboot(){
        type = "v";
        status = false;
        beforeVote = false;
        makeSabotageSolvers();
    }
}
