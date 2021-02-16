package com.among_us_bot_core;

import java.util.HashMap;
import java.util.Map;


public class TaskVersions {
    private Map<Integer, Task> taskVersions;
    private int version;

    public TaskVersions() {
        taskVersions = new HashMap<>();
        version = 0;
    }

    public Task getTask() {
        if (version >= taskVersions.size())
            version = 0;
        Task task = taskVersions.get(version);
        version++;
        return task;
    }
    public void putTaskVersion(Integer key, Task task) {
        taskVersions.put(key, task);
    }
}
