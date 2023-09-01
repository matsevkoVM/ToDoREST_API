package com.softserve.itacademy.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.todolist.model.ToDo;
import lombok.Value;

import java.time.format.DateTimeFormatter;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ToDoResponse {
    Long id;
    String title;
    String createdAt;
    String owner;


    public ToDoResponse(ToDo toDo) {
        id = toDo.getId();
        title = toDo.getTitle();
        createdAt = toDo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm"));
        owner = toDo.getOwner().getFirstName() + " " + toDo.getOwner().getLastName();
    }
}
