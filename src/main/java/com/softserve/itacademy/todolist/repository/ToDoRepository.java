package com.softserve.itacademy.todolist.repository;

import com.softserve.itacademy.todolist.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ToDoRepository extends JpaRepository<ToDo, Long> {
    @Query("SELECT DISTINCT t FROM ToDo t WHERE t.owner.id = :userId OR :userId MEMBER OF t.collaborators")

    List<ToDo> getByUserId(long userId);
}
