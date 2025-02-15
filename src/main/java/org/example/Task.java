package org.example;

import org.example.enums.Role;
import org.example.enums.TaskType;

import java.io.Serializable;

public class Task implements Serializable {
    private final TaskType type;
    private final Object data;
    private final Role role;

    public Task(TaskType type, Object data, Role role) {
        this.type = type;
        this.data = data;
        this.role = role;
    }

    public TaskType getType() { return type; }
    public Object getData() { return data; }
    public Role getRole(){return role;}
}
