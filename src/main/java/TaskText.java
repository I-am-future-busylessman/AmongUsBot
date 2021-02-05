import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class TaskText {
    Map<String, String> sabotage = new HashMap<>();
    String unavailable;
    public TaskText() {
    }


    public void makeSabotage(){
        Map<String, String> sabotage = new HashMap<>();
        sabotage.put("Свет", "хранилище");
        sabotage.put("Реактор", "гараж");
        sabotage.put("Связь", "оружейная");
        sabotage.put("Кислород", "штаб");
        sabotage.forEach((k, v) -> {
            if (v.equals(unavailable))
                sabotage.replace(k, v, "гостиная");
        });
        this.sabotage = sabotage;
    }
}
