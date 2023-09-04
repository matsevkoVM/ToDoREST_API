package com.todolist.repository;

import com.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.todo.id = :todoId")
    List<Task> getByTodoId(long todoId);

//    @Query("from Task where ")
//    List<Task> getByUserId(long userId);
}
