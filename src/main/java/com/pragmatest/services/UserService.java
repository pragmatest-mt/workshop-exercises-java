package com.pragmatest.services;

import com.pragmatest.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    Optional<User> saveUser(User user);

    void deleteUserById(Long id);
}
