import lombok.Getter;

import java.util.*;

@Getter
public class TaskText {
    Map<String, String> sabotage = new HashMap<>();

    public TaskText() {
        makeSabotage();
    }


    private void makeSabotage(){
        Map<String, String> sabotage = new HashMap<>();
        sabotage.put("Свет", "В хранилище");
        sabotage.put("Реактор", "В гараже");
        sabotage.put("Связь", "В оружейной");
        sabotage.put("Кислород", "В гостиной");
        this.sabotage = sabotage;
    }
}
