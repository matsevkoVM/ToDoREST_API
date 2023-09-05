package com.todolist.controller;

import com.todolist.dto.TaskDto;
import com.todolist.dto.TaskResponse;
import com.todolist.dto.TaskTransformer;
import com.todolist.exception.NullEntityReferenceException;
import com.todolist.model.Task;
import com.todolist.model.ToDo;
import com.todolist.service.StateService;
import com.todolist.service.TaskService;
import com.todolist.service.ToDoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/users/{user_id}/todos/{todo_id}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;


    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> create(@PathVariable("user_id") Long userId, @PathVariable("todo_id") Long toDoId,
                                    @Valid @RequestBody TaskDto taskDto, BindingResult result) {
        ToDo toDo = toDoService.readById(toDoId);
        if (result.hasErrors()){
            throw new NullEntityReferenceException("The source object has error/errors");
        }
        if (!Objects.equals(toDo.getOwner().getId(), userId)) {
            log.info(String.format("The User with ID %s is not the owner of the ToDo with ID %s " +
                    "and cannot create Tasks in current ToDo", userId, toDoId));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((String.format("The User with ID %s is not the owner of the ToDo with ID %s " +
                    "and cannot create Tasks in current ToDo", userId, toDoId)));
        }
        log.info("ToDo with id " + toDoId + " is present");
        Task task = TaskTransformer.convertToEntity(taskDto, toDo, stateService.getByName("NEW"));
        Task newTask = taskService.create(task);
        log.info("Task created with ID: " + newTask.getId() + " Name: " + newTask.getName() + " and Priority: " + newTask.getPriority());

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", "api/tasks/" + newTask.getId())
                .body(new TaskResponse(newTask));
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> read(@PathVariable("user_id") Long userId, @PathVariable("todo_id") Long toDoId,
                                             @PathVariable("id") Long id) {
        Task task = taskService.readById(id);
        if (!Objects.equals(userId, toDoService.readById(toDoId).getOwner().getId())){
            log.info("User with ID " + userId + "does not have ToDo with ID " + toDoId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User with ID " + userId + "does not have ToDo with ID " + toDoId);
        }
        log.info("Reading Task with ID " + id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new TaskResponse(task));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> update(@PathVariable("user_id") Long userId, @PathVariable("todo_id") Long toDoId, @PathVariable("id") Long id,
                                               @Valid @RequestBody TaskDto taskDto, BindingResult result) {
        if (result.hasErrors()){
            throw new NullEntityReferenceException("The source object has error/errors");
        }
        log.info("TaskDto with Name: " + taskDto.getName() + ", Priority: " + taskDto.getPriority() +
                ", ToDoId" + taskDto.getTodoId() + " and StateID: " + taskDto.getStateId());
        if (!Objects.equals(userId, toDoService.readById(toDoId).getOwner().getId())){
            log.info(String.format("The User with ID %s is not the owner of the ToDo with ID %s " +
                    "and cannot edit Tasks in current ToDo", userId, toDoId));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((String.format("The User with ID %s is not the owner of the ToDo with ID %s " +
                    "and cannot edit Tasks in current ToDo", userId, toDoId)));
        }
        Task task = TaskTransformer.convertToEntity(taskDto,
                toDoService.readById(toDoId),
                stateService.readById(taskDto.getStateId())
        );
        task.setId(id);
        taskService.update(task);
        log.info("Task with ID " + task.getId() + " was updated successfully");

        return ResponseEntity.status(HttpStatus.OK)
                .body(new TaskResponse(task));
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable("user_id") Long userId, @PathVariable("todo_id") Long toDoId, @PathVariable("id") Long id) {
        if (!Objects.equals(userId, toDoService.readById(toDoId).getOwner().getId())){
            log.info(String.format("The User with ID %s is not the owner of the ToDo with ID %s " +
                    "and cannot delete Tasks in current ToDo", userId, toDoId));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((String.format("The User with ID %s is not the owner of the ToDo with ID %s " +
                    "and cannot delete Tasks in current ToDo", userId, toDoId)));
        }
        if (!Objects.equals(toDoId, taskService.readById(id).getTodo().getId())){
            log.info(String.format("Delete operation cannot be completed. The ToDo with ID %s does not contain Task with ID %s.",
                    toDoId, id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body((String.format("Delete operation cannot be completed. The ToDo with ID %s does not contain Task with ID %s.",
                    toDoId, id)));
        }
        taskService.delete(id);
        log.info("Task with ID " + id + " was deleted successfully");

        return ResponseEntity.status(HttpStatus.OK).body("Task with ID " + id + " was deleted successfully");
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAll(@PathVariable("user_id") Long userId, @PathVariable("todo_id") Long toDoId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.getAll().stream()
                        .map(TaskResponse::new)
                        .collect(Collectors.toList())
                );
    }

    @GetMapping("/by_todo")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    public ResponseEntity<?> getAllByToDoId(@PathVariable("user_id") Long userId, @PathVariable("todo_id") Long toDoId) {
        if (!Objects.equals(userId, toDoService.readById(toDoId).getOwner().getId())){
            log.info(String.format("Operation cannot be completed.  The User with ID %s does not have the ToDo with ID %s",
                    userId, toDoId));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body((String.format("Operation cannot be completed.  The User with ID %s does not have the ToDo with ID %s",
                    userId, toDoId)));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(taskService.getByTodoId(toDoId).stream()
                        .map(TaskResponse::new)
                        .collect(Collectors.toList())
                );
    }
}
