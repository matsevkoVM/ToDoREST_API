package com.todolist.controller;

import com.todolist.dto.CollaboratorResponse;
import com.todolist.dto.ToDoResponse;
import com.todolist.exception.NullEntityReferenceException;
import com.todolist.model.ToDo;
import com.todolist.model.User;
import com.todolist.service.ToDoService;
import com.todolist.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping()
public class ToDoController {
    private final ToDoService toDoService;
    private final UserService userService;

    @Autowired
    public ToDoController(ToDoService toDoService, UserService userService) {
        this.toDoService = toDoService;
        this.userService = userService;
    }

    @PostMapping("/api/users/{user_id}/todos")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> createToDo(@PathVariable("user_id") Long userId, @Valid @RequestBody ToDo toDoCreate, BindingResult result) {

        if (result.hasErrors()) {
            log.info("Entity refers to null");
            throw new NullEntityReferenceException();
        }

        if (checkUserIdAvailability(userId)) {
            log.info("User with ID " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found");
        }

        ToDo toDo = new ToDo();
        toDo.setOwner(userService.readById(userId));
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setTitle(toDoCreate.getTitle());
        log.info("Created new ToDo with title " + toDo.getTitle() + " and ID " + userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ToDoResponse(toDoService.create(toDo)));
    }

    @GetMapping("/api/users/todos/{todo_id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> readToDo(@PathVariable("todo_id") Long toDoId) {
        if (checkTodoIdAvailability(toDoId)) {
            log.info("ToDo with ID " + toDoId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info("View ToDo with ID " + toDoId);
        return ResponseEntity.status(HttpStatus.OK).body(new ToDoResponse(toDoService.readById(toDoId)));
    }

    @PutMapping("/api/users/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<?> updateToDo(@PathVariable("todo_id") Long toDoId, @Valid @RequestBody ToDo toDoUpdate, BindingResult result) {
        if (checkTodoIdAvailability(toDoId)) {
            log.info("ToDo with ID " + toDoId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (result.hasErrors()) {
            log.info("Entity refers to null");
            throw new NullEntityReferenceException();
        }
        ToDo toDo = toDoService.readById(toDoId);
        toDo.setTitle(toDoUpdate.getTitle());
        log.info("Edited ToDo with ID " + toDoId);
        return ResponseEntity.status(HttpStatus.OK).body(new ToDoResponse(toDoService.update(toDo)));
    }

    @DeleteMapping("/api/users/todos/{todo_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<?> deleteToDo(@PathVariable("todo_id") Long toDoId) {
        if (checkTodoIdAvailability(toDoId)) {
            log.info("ToDo with ID " + toDoId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ToDoResponse toDoResponse = new ToDoResponse(toDoService.readById(toDoId));
        toDoService.delete(toDoId);
        log.info("Deleted ToDo with ID " + toDoId);
        return ResponseEntity.status(HttpStatus.OK).body(toDoResponse);
    }

    @GetMapping("/api/users/todos")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getAllToDos() {
        log.info("Got the list of all ToDos");
        return ResponseEntity.status(HttpStatus.OK)
                .body(toDoService.getAll().stream()
                        .map(ToDoResponse::new)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/api/users/{user_id}/todos")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getAllUsersToDos(@PathVariable("user_id") Long userId){
        if (checkUserIdAvailability(userId)) {
            log.info("User with ID " + userId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + userId + " not found");
        }
        log.info(String.format("Got the list of all %s's ToDos", userService.readById(userId)));
        return ResponseEntity.status(HttpStatus.OK)
                .body(toDoService.getByUserId(userId).stream()
                        .map(ToDoResponse::new)
                        .collect(Collectors.toList()));
    }

    @GetMapping("/api/users/todos/{todo_id}/collaborators")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<?> getCollaborators(@PathVariable("todo_id") Long toDoId) {
        if (checkTodoIdAvailability(toDoId)) {
            log.info("ToDo with ID " + toDoId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        log.info(String.format("Got hte list of collaborators of the ToDo %s",
                toDoService.readById(toDoId).getTitle()));
        return ResponseEntity.status(HttpStatus.OK).body(toDoService.readById(toDoId).getCollaborators().stream()
                .map(c -> new CollaboratorResponse(c, toDoService.readById(toDoId)))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/users/todos/{todo_id}/collaborators/add/{collaborator_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<?> addCollaborator(@PathVariable("todo_id") Long toDoId,
                                               @PathVariable("collaborator_id") Long collaboratorId) {
        if (checkTodoIdAvailability(toDoId)){
            log.info("ToDo with ID " + toDoId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (checkUserIdAvailability(collaboratorId)){
            log.info("Impossible to add collaborator. User with ID " + collaboratorId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ToDo toDo = toDoService.readById(toDoId);
        List<User> collaborators = toDo.getCollaborators();
        collaborators.add(userService.readById(collaboratorId));
        toDo.setCollaborators(collaborators);
        toDoService.update(toDo);
        log.info(String.format("Added new collaborator %s %s to the ToDo %s",
                userService.readById(collaboratorId).getFirstName(), userService.readById(collaboratorId).getLastName(),
                toDo.getTitle()));
        return ResponseEntity.status(HttpStatus.OK).body(toDoService.readById(toDoId).getCollaborators().stream()
                .map(c -> new CollaboratorResponse(c, toDoService.readById(toDoId)))
                .collect(Collectors.toList()));
    }

    @GetMapping("/api/users/todos/{todo_id}/collaborators/remove/{collaborator_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<?> deleteCollaborator(@PathVariable("todo_id") Long toDoId,
                                                  @PathVariable("collaborator_id") Long collaboratorId) {
        if (checkUserIdAvailability(collaboratorId)){
            log.info("Impossible to remove collaborator. User with ID " + collaboratorId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        if (checkTodoIdAvailability(toDoId)){
            log.info("ToDo with ID " + toDoId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        ToDo toDo = toDoService.readById(toDoId);
        List<User> collaborators = toDo.getCollaborators();
        collaborators.remove(userService.readById(collaboratorId));
        toDo.setCollaborators(collaborators);
        toDoService.update(toDo);
        log.info(String.format("Removed collaborator %s %s from the ToDo %s",
                userService.readById(collaboratorId).getFirstName(), userService.readById(collaboratorId).getLastName(),
                toDo.getTitle()));
        return ResponseEntity.status(HttpStatus.OK).body(toDoService.readById(toDoId).getCollaborators().stream()
                .map(c -> new CollaboratorResponse(c, toDoService.readById(toDoId)))
                .collect(Collectors.toList()));
    }

    private boolean checkTodoIdAvailability(long toDoId) {
        List<Long> idList = toDoService.getAll().stream()
                .map(ToDo::getId)
                .toList();
        return !idList.contains(toDoId);
    }

    private boolean checkUserIdAvailability(long userId) {
        List<Long> idList = userService.getAll().stream()
                .map(User::getId)
                .toList();
        return !idList.contains(userId);
    }
}
