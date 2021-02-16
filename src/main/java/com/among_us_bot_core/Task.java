package com.among_us_bot_core;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Task {
    private int number;
    private String taskText;
    private String code;

    public Task(String taskText, String code) {
        this.taskText = taskText;
        this.code = code;
    }
}
