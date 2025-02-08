package org.example;

import org.example.enums.TaskType;

import java.io.Serializable;

public class Task implements Serializable {
    private final TaskType type;
    private final Object data;

    public Task(TaskType type, Object data) {
        this.type = type;
        this.data = data;
    }

    public TaskType getType() { return type; }
    public Object getData() { return data; }
}
