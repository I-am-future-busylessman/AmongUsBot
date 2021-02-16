package com.among_us_bot_core;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * User class.
 */

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
    private String colorToKill = "y";
    private String confirmColorToKill = "e";
    private long killTime = 0;
    private long sabotageTime = 0;
    private int totalKills = 0;
    private int totalSabotages = 0;
    private int easyTasks;
    private int normalTasks;
    private int hardTasks;
    private int totalTasks;
    private List<Integer> complitedTasks = new ArrayList<>();
    private List<Integer> oldTasks = new ArrayList<>();

    public User(final Long chatID) {
        this.chatId = chatID;
    }

    public User(final User user) {
        chatId = user.getChatId();
        oldTasks = user.getComplitedTasks();
    }

    /**
     * Returns user chatId.
     */
    public Long getChatId() {
        if (chatId != null) {
            return chatId;
        }
        return -1L;
    }

    public void getTask() {
        int taskNumber = 0;
        int number = new Random().nextInt(1000);
        if (number % 3 == 2 && hardTasks > 0) {
            taskNumber += 30;
            taskNumber = getTaskNumber(taskNumber);
        } else if (number % 3 == 1 && normalTasks > 0) {
            taskNumber += 20;
            taskNumber = getTaskNumber(taskNumber);
        } else if (easyTasks > 0) {
            taskNumber += 10;
            taskNumber = getTaskNumber(taskNumber);
        }
        if (complitedTasks.size() < totalTasks && taskNumber == 0) {
            getTask();
        } else {
            activeTaskNum = taskNumber;
        }
    }

    private int getTaskNumber(int taskNumber) {
        int finalTaskNumber = taskNumber;
        while (true) {
            int finalNumber = new Random().nextInt(1000);
            if (complitedTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber)
                    && oldTasks.stream().noneMatch(i -> i == finalNumber + finalTaskNumber)) {
                taskNumber += finalNumber;
                break;
            }
        }
        return taskNumber;
    }

    public String getColor(final User user) {
        return color;
    }
}

