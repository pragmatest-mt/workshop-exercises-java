package com.pragmatest.controllers;

import com.pragmatest.exceptions.UserInvalidException;
import com.pragmatest.exceptions.UserNotFoundException;
import com.pragmatest.matchers.UserMatcher;
import com.pragmatest.models.User;
import com.pragmatest.models.UserRequest;
import com.pragmatest.models.UserResponse;
import com.pragmatest.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserControllerUnitTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userMockService;

    @Test
    public void testSaveUserValidUser() {
        // Arrange
        User userInput = new User("Marisa Jones", "Newcastle", 20);
        User userOutput = new User("Marisa Jones", "Newcastle", 20);
        userOutput.setId(1L);

        when(userMockService.saveUser(argThat(new UserMatcher(userInput)))).thenReturn(Optional.of(userOutput));

        UserRequest userRequest = new UserRequest("Marisa Jones", "Newcastle", 20);

        UserResponse expectedResponse = new UserResponse(1L, "Marisa Jones", "Newcastle", 20);

        // Act
        UserResponse actualResponse = userController.newUser(userRequest);

        // Assert
        assertThat(actualResponse).isEqualToComparingFieldByField(expectedResponse);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(userInput)));
    }

    @Test
    public void testSaveUserUnderAgeUser() {
        // Arrange
        User newUser = new User("Jane Stark", "Newcastle", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(newUser)))).thenReturn(Optional.empty());

        UserRequest userRequest = new UserRequest("Jane Stark", "Newcastle", 17);

        // Act
        Executable executable = () -> {
            userController.newUser(userRequest);
        };

        // Assert
        assertThrows(UserInvalidException.class, executable);

        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(newUser)));
    }

    @Test
    public void testGetUserByIdValidId() {
        // Arrange
        User user = new User(1L, "John Smith", "London", 24);
        when(userMockService.getUserById(1L)).thenReturn(Optional.of(user));

        UserResponse expectedResponse = new UserResponse(1L, "John Smith", "London", 24);

        Long userId = 1L;

        //Act
        UserResponse actualResponse = userController.findOne(userId);

        // Assert
        assertThat(actualResponse).isEqualToComparingFieldByField(expectedResponse);
        verify(userMockService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUserByIdNonExistentId() {
        // Arrange
        when(userMockService.getUserById(1L)).thenReturn(Optional.empty());

        // Act
        Executable executable = () -> {
            userController.findOne(1L);
        };

        // Assert
        assertThrows(UserNotFoundException.class, executable);
        verify(userMockService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUsersValidUsers() {
        // Arrange
        List<User> users = Arrays.asList(
                new User(1L, "John Smith", "London", 23),
                new User(2L, "Mary Walsh", "Liverpool", 30)
        );

        when(userMockService.getAllUsers()).thenReturn(users);

        UserResponse expectedUserResponse1 = new UserResponse(1L, "John Smith", "London", 23);
        UserResponse expectedUserResponse2 = new UserResponse(2L, "Mary Walsh", "Liverpool", 30);

        // Act
        List<UserResponse> actualResponse = userController.findAll();

        // Assert
        //check on this
        assertThat(actualResponse)
                .extracting(UserResponse::getId,UserResponse::getFullName,
                        UserResponse::getLocality, UserResponse::getAge)
                .containsExactly(
                        tuple(expectedUserResponse1.getId(), expectedUserResponse1.getFullName(),
                                expectedUserResponse1.getLocality(), expectedUserResponse1.getAge()),
                        tuple(expectedUserResponse2.getId(), expectedUserResponse2.getFullName(),
                                expectedUserResponse2.getLocality(), expectedUserResponse2.getAge())
                );

        verify(userMockService, times(1)).getAllUsers();
    }

    @Test
    public void testDeleteUserValidUser() {
        // Arrange
        doNothing().when(userMockService).deleteUserById(1L);

        // Act
        userController.deleteUser(1L);

        // Assert
        verify(userMockService, times(1)).deleteUserById(1L);
    }

    @Test
    public void testUpdateUserValidUser() {
        // Arrange
        User user = new User(1L, "John Smith", "Manchester", 20);
        when(userMockService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userMockService.saveUser(argThat(new UserMatcher(user)))).thenReturn(Optional.of(user));

        // Act
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 20);
        UserResponse actualResponse = userController.saveOrUpdate(userRequest, 1L);

        UserResponse expectedResponse = new UserResponse(1L, "John Smith", "Manchester", 20);

        // Assert
        assertThat(actualResponse).isEqualToComparingFieldByField(expectedResponse);
        verify(userMockService, times(1)).getUserById(1L);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(user)));
    }

    @Test
    public void testUpdateUserUnderAgeUser() {
        // Arrange
        User user = new User(1L, "John Smith", "Manchester", 17);
        when(userMockService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userMockService.saveUser(argThat(new UserMatcher(user)))).thenReturn(Optional.empty());

        // Act
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 17);

        Executable executable = () -> {
          userController.saveOrUpdate(userRequest, 1L);
        };

        // Assert
        assertThrows(UserInvalidException.class, executable);
        verify(userMockService, times(1)).getUserById(1L);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(user)));
    }

    @Test
    public void testUpdateUserNonexistentUser() {
        // Arrange
        User user = new User(1L, "John Smith", "Manchester", 17);
        when(userMockService.getUserById(1L)).thenReturn(Optional.empty());

        // Act
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 17);

        Executable executable = () -> {
            userController.saveOrUpdate(userRequest, 1L);
        };

        // Assert
        assertThrows(UserNotFoundException.class, executable);
        verify(userMockService, times(1)).getUserById(1L);
        verify(userMockService, never()).saveUser(any(User.class));
    }
}
