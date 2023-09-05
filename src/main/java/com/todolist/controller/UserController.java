package com.todolist.controller;

import com.todolist.dto.UserRequest;
import com.todolist.dto.UserResponse;
import com.todolist.model.User;
import com.todolist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<UserResponse> getUserById(@PathVariable long userId) {
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(userService.readById(userId)));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList()));
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN')")
    ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        User user = new User();
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
        userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<UserResponse> updateUser(@PathVariable long userId, @RequestBody @Valid User user) {
        user.setId(userId);
        userService.update(user);
        return ResponseEntity.status(HttpStatus.OK).body(new UserResponse(user));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    ResponseEntity<UserResponse> deleteUser(@PathVariable long userId) {
        UserResponse userResponse = new UserResponse(userService.readById(userId));
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userResponse);
    }
}
