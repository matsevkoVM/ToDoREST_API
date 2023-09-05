package com.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.todolist.model.ToDo;
import lombok.Value;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ToDoResponse {
    Long id;
    String title;
    String createdAt;
    String owner;
    List<String> collaborators;


    public ToDoResponse(ToDo toDo) {
        id = toDo.getId();
        title = toDo.getTitle();
        createdAt = toDo.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm"));
        owner = toDo.getOwner().getFirstName() + " " + toDo.getOwner().getLastName();
        if (toDo.getCollaborators() != null) {
            collaborators = toDo.getCollaborators().stream()
                    .map(c -> c.getFirstName() + " " + c.getLastName())
                    .collect(Collectors.toList());
        } else {
            collaborators = new ArrayList<>();
        }
    }
}
