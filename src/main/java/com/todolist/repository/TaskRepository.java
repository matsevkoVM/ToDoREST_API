package com.todolist.repository;

import com.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query(value = "SELECT * FROM tasks  WHERE todo_id = :todoId", nativeQuery = true)
    List<Task> getByTodoId(long todoId);
}
