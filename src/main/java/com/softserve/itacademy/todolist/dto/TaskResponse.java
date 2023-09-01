package com.softserve.itacademy.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.todolist.model.Priority;
import com.softserve.itacademy.todolist.model.Task;
import lombok.Value;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskResponse {
    Long id;
    String name;
    Priority priority;
    Long toDoId;
    String state;


    public TaskResponse(Task task) {
        id = task.getId();
        name = task.getName();
        priority = task.getPriority();
        toDoId = task.getTodo().getId();
        state = task.getState().getName();
    }
}
