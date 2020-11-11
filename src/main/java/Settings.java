import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Settings {
    private Integer players;
    private Integer easyTasks;
    private Integer normalTasks;
    private Integer hardTasks;
    private Integer imposterKD;
    private Integer impostersCount;

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
