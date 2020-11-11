import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class User {
    private int activeTask;
    private Boolean role;
    private Long chatID;
    private String color = null;
    private Boolean alive;
    private Boolean voted;
    private String colorToKill ="y";
    private String confirmColorToKill = "e";
    private long killTime = 0;
    int easyTasks;
    int normalTasks;
    int hardTasks;
    private List<Integer> complitedTasks;

    public User(Long chatID) {
        this.chatID = chatID;
    }
}

