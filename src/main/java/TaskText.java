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
        sabotage.put("Свет", "В столовой");
        sabotage.put("Реактор", "В штабе");
        sabotage.put("Связь", "В хранилище");
        sabotage.put("Кислород", "В Лаборатории");
        this.sabotage = sabotage;
    }
}
