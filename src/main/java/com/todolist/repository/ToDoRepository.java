package com.todolist.repository;

import com.todolist.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    @Query(value = "SELECT DISTINCT t.* FROM todos t " +
            "LEFT JOIN todo_collaborator tc ON t.id = tc.todo_id " +
            "WHERE t.owner_id = :userId OR :userId IN (SELECT collaborator_id FROM todo_collaborator WHERE todo_id = t.id) " +
            "ORDER BY t.id",
            nativeQuery = true)
    List<ToDo> getByUserId(long userId);
}
