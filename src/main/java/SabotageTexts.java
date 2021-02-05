import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class SabotageTexts {
    Map<String, String> sabotage = new HashMap<>();
    String unavailable;
    public SabotageTexts() {
        unavailable = "все";
        makeSabotage();
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

    public String getSabotageLocations(){
        String result = "";
        result += "Свет" + " " + sabotage.get("Свет") + "\n";
        result += "Реактор" + " " + sabotage.get("Реактор") + "\n";
        result += "Связь" + " " + sabotage.get("Связь") + "\n";
        result += "Кислород" + " " + sabotage.get("Кислород") + "\n";
        return result;
    }
}
