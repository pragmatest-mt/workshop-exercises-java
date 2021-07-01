package com.pragmatest.controllers;

import com.pragmatest.exceptions.UserInvalidException;
import com.pragmatest.exceptions.UserNotFoundException;
import com.pragmatest.models.User;
import com.pragmatest.models.UserRequest;
import com.pragmatest.models.UserResponse;
import com.pragmatest.services.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;

    @GetMapping("/users")
    List<UserResponse> findAll() {

        List<User> users = userService.getAllUsers();

        Type responseType = new TypeToken<List<UserResponse>>() {
        }.getType();

        List<UserResponse> response = modelMapper.map(users, responseType);

        return response;
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse newUser(@Valid @RequestBody UserRequest userRequest) {

        User user = modelMapper.map(userRequest, User.class);

        user = userService.saveUser(user).orElseThrow(() -> new UserInvalidException());

        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        return userResponse;
    }

    @GetMapping("/users/{id}")
    UserResponse findOne(@PathVariable Long id) {

        User user = userService.getUserById(id).orElseThrow(() -> new UserNotFoundException(id));

        UserResponse response = modelMapper.map(user, UserResponse.class);

        return response;
    }

    @PutMapping("/users/{id}")
    UserResponse saveOrUpdate(@Valid @RequestBody UserRequest userRequest, @PathVariable Long id) {

        User existentUser = userService.getUserById(id).orElseThrow(() -> new UserNotFoundException(id));

        User newUser = modelMapper.map(userRequest, User.class);

        newUser.setId(existentUser.getId());
        User updatedUser = userService.saveUser(newUser).orElseThrow(() -> new UserInvalidException());

        UserResponse userResponse = modelMapper.map(updatedUser, UserResponse.class);

        return userResponse;
    }

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable Long id) {

        userService.deleteUserById(id);
    }
}