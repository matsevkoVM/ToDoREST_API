package com.softserve.itacademy.todolist.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import lombok.Value;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CollaboratorResponse {
    Long id;
    String collaboratorName;
    String ownerName;

    public CollaboratorResponse(User collaborator, ToDo toDo){
        id = collaborator.getId();
        collaboratorName = collaborator.getFirstName() + " " + collaborator.getLastName();
        ownerName = String.format("%s %s", toDo.getOwner().getFirstName(), toDo.getOwner().getLastName());
    }
}
