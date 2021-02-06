import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class User {
    private int activeTaskNum = 0;
    private Task activeTask;
    private Boolean role;
    private Long chatId;
    private String color = null;
    private Boolean alive;
    private Boolean voted;
    private String colorToKill ="y";
    private String confirmColorToKill = "e";
    private long killTime = 0;
    private long sabotageTime = 0;
    private int totalKills = 0;
    private int totalSabotages = 0;
    int easyTasks;
    int normalTasks;
    int hardTasks;
    int totalTasks;
    private List<Integer> complitedTasks = new ArrayList<>();
    private List<Integer> oldTasks = new ArrayList<>();

    public User(Long chatID) {
        this.chatId = chatID;
    }

    public User(User user){
        chatId = user.getChatId();
        oldTasks = user.getComplitedTasks();
    }

    public Long getChatId(){
        if (chatId != null)
            return chatId;
        return -1L;
    }

    public void getTask(){
        int taskNumber = 0;
        int number = (int)(Math.random()*100);
        if (number % 3 == 2 && hardTasks > 0){
            taskNumber += 30;
            int finalTaskNumber = taskNumber;
            while (true){
                int finalNumber = (int)(Math.random()*10);
                if (complitedTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber) && oldTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber)){
                    taskNumber += finalNumber;
                    break;
                }
            }
        }else if (number % 3 == 1 && normalTasks > 0){
            taskNumber += 20;
            int finalTaskNumber = taskNumber;
            while (true){
                int finalNumber = (int)(Math.random()*10);
                if (complitedTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber) && oldTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber)){
                    taskNumber += finalNumber;
                    break;
                }
            }
        }else if(easyTasks > 0){
            taskNumber += 10;
            int finalTaskNumber = taskNumber;
            while (true){
                int finalNumber = (int)(Math.random()*10);
                if (complitedTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber) && oldTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber)){
                    taskNumber += finalNumber;
                    break;
                }
            }
        }
        if (complitedTasks.size() < totalTasks && taskNumber == 0 ) {
            getTask();
        }
        else {
            activeTaskNum = taskNumber;
        }
    }
    public String getColor(User user) {
        return color;
    }
}

