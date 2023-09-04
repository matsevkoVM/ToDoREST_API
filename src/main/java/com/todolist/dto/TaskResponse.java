package com.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.todolist.model.Priority;
import com.todolist.model.Task;
import lombok.Value;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskResponse {
    Long id;
    String name;
    Priority priority;
    Long toDoId;
    String state;
    String toDoOwner;


    public TaskResponse(Task task) {
        id = task.getId();
        name = task.getName();
        priority = task.getPriority();
        toDoId = task.getTodo().getId();
        toDoOwner = task.getTodo().getOwner().getFirstName() + " " + task.getTodo().getOwner().getLastName();
        state = task.getState().getName();
    }
}
