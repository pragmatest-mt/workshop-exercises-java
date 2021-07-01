package com.pragmatest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragmatest.matchers.UserMatcher;
import com.pragmatest.models.User;
import com.pragmatest.models.UserRequest;
import com.pragmatest.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationMockMvcTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userMockService;

    static Stream<Arguments> invalidUserProvider() {
        return Stream.of(
                arguments("Unborn Person", "Not Yet", -100),
                arguments("Unborn Person", "Not Yet", -1),
                arguments("George Eliot", "Warwickshire", 201),
                arguments("Iry-Hor", "Egypt", 5220),
                arguments("", "Malta", 20),
                arguments(null, "Malta", 20),
                arguments(" ", "Malta", 20),
                arguments(new String(new char[500]).replace('\0', 'a'), "Malta", 20),
                arguments("John Smith", "", 20),
                arguments("John Smith", null, 20),
                arguments("John Smith", new String(new char[500]).replace('\0', 'a'), 20)
        );
    }

    @Test
    public void testSaveUserValidUser() throws Exception {
        // Arrange
        UserRequest newUserRequest = new UserRequest();
        newUserRequest.setFullName("Marisa Jones");
        newUserRequest.setLocality("Newcastle");
        newUserRequest.setAge(20);

        User expectedServiceInput = new User("Marisa Jones", "Newcastle", 20);
        User mockedServiceOutput = new User(1L, "Marisa Jones", "Newcastle", 20);

        when(userMockService.saveUser(argThat(new UserMatcher(expectedServiceInput)))).thenReturn(Optional.of(mockedServiceOutput));

        String request = om.writeValueAsString(newUserRequest);

        String endpoint = "/users";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // Assert
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Marisa Jones"))
                .andExpect(jsonPath("$.locality").value("Newcastle"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.id").value(1));

        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(expectedServiceInput)));
    }

    @Test
    public void testSaveUserUnderAgeUser() throws Exception {
        // Arrange
        UserRequest newUserRequest = new UserRequest();
        newUserRequest.setFullName("Michelle Waters");
        newUserRequest.setLocality("Dubai");
        newUserRequest.setAge(17);
        String request = om.writeValueAsString(newUserRequest);

        User expectedServiceInput = new User("Michelle Waters", "Dubai", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(expectedServiceInput)))).thenReturn(Optional.empty());

        String endpoint = "/users";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        //Assert
        resultActions.andExpect(status().isBadRequest());

        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(expectedServiceInput)));
    }

    @Test
    public void testGetUserByIdValidId() throws Exception {
        // Arrange
        User user = new User("John Smith", "London", 23);

        when(userMockService.getUserById(1L)).thenReturn(Optional.of(user));

        String endpoint = "/users/1";

        // Act
        ResultActions resultActions = mockMvc.perform(get(endpoint));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.fullName").value("John Smith"))
                .andExpect(jsonPath("$.locality").value("London"))
                .andExpect(jsonPath("$.age").value(23));

        verify(userMockService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUsersByIdValidIds() throws Exception {
        // Arrange
        List<User> users = Arrays.asList(
                new User("John Smith", "London", 23),
                new User("Mary Walsh", "Liverpool", 30));

        when(userMockService.getAllUsers()).thenReturn(users);

        String endpoint = "/users";

        // Act
        ResultActions resultActions = mockMvc.perform(get(endpoint));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName").value("John Smith"))
                .andExpect(jsonPath("$[0].locality").value("London"))
                .andExpect(jsonPath("$[0].age").value(23))
                .andExpect(jsonPath("$[1].fullName").value("Mary Walsh"))
                .andExpect(jsonPath("$[1].locality").value("Liverpool"))
                .andExpect(jsonPath("$[1].age").value(30));

        verify(userMockService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserByIdNonExistentId() throws Exception {
        // Arrange
        String endpoint = "/users/5";

        // Act
        ResultActions resultActions = mockMvc.perform(get(endpoint));

        // Assert
        resultActions.andExpect(status().isNotFound());

        verify(userMockService, times(1)).getUserById(5L);
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    public void testSaveUserInvalidUser(String fullName, String locality, int age) throws Exception {
        // Arrange
        UserRequest newUserRequest = new UserRequest();
        newUserRequest.setFullName(fullName);
        newUserRequest.setLocality(locality);
        newUserRequest.setAge(age);
        String request = om.writeValueAsString(newUserRequest);

        String endpoint = "/users";

        // Act
        ResultActions resultActions = mockMvc.perform(
                post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        //Assert
        resultActions.andExpect(status().isBadRequest());

        verify(userMockService, never()).saveUser(any());
    }

    @Test
    public void testUpdateUserValidUser() throws Exception {
        // Arrange
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setFullName("Marisa Jones");
        updateUserRequest.setLocality("Newcastle");
        updateUserRequest.setAge(20);
        String request = om.writeValueAsString(updateUserRequest);

        long updateUserId = 1L;

        User user = new User("Marisa Jones", "Newcastle", 20);
        user.setId(updateUserId);

        when(userMockService.getUserById(updateUserId)).thenReturn(Optional.of(user));
        when(userMockService.saveUser(argThat(new UserMatcher(user)))).thenReturn(Optional.of(user));

        String endpoint = "/users/" + updateUserId;

        // Act
        ResultActions resultActions = mockMvc.perform(
                put(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Marisa Jones"))
                .andExpect(jsonPath("$.locality").value("Newcastle"))
                .andExpect(jsonPath("$.age").value(20))
                .andExpect(jsonPath("$.id").value(updateUserId));

        verify(userMockService, times(1)).getUserById(updateUserId);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(user)));
    }

    @Test
    public void testUpdateUserUnderAgeUser() throws Exception {
        // Arrange
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setFullName("Marisa Jones");
        updateUserRequest.setLocality("Newcastle");
        updateUserRequest.setAge(17);

        String request = om.writeValueAsString(updateUserRequest);

        long updateUserId = 1L;

        User getUserByIdOutput = new User(updateUserId, "Marisa Jones", "Newcastle", 20);
        when(userMockService.getUserById(updateUserId)).thenReturn(Optional.of(getUserByIdOutput));

        User saveUserInput = new User(updateUserId, "Marisa Jones", "Newcastle", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(saveUserInput)))).thenReturn(Optional.empty());

        String endpoint = "/users/" + updateUserId;

        // Act
        ResultActions resultActions = mockMvc.perform(
                put(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        verify(userMockService, times(1)).getUserById(updateUserId);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(saveUserInput)));
    }

    @Test
    public void testUpdateUserNonExistentId() throws Exception {
        // Arrange
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setFullName("Marisa Jones");
        updateUserRequest.setLocality("Newcastle");
        updateUserRequest.setAge(20);

        String request = om.writeValueAsString(updateUserRequest);

        long updateUserId = 1L;

        when(userMockService.getUserById(updateUserId)).thenReturn(Optional.empty());

        User saveUserInput = new User(updateUserId, "Marisa Jones", "Newcastle", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(saveUserInput)))).thenReturn(Optional.empty());

        String endpoint = "/users/" + updateUserId;

        // Act
        ResultActions resultActions = mockMvc.perform(
                put(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // Assert
        resultActions.andExpect(status().isNotFound());

        verify(userMockService, times(1)).getUserById(updateUserId);
        verify(userMockService, never()).saveUser(argThat(new UserMatcher(saveUserInput)));
    }

    @ParameterizedTest
    @MethodSource("invalidUserProvider")
    public void testUpdateUserInvalidUser(String fullName, String locality, int age) throws Exception {
        // Arrange
        UserRequest updateUserRequest = new UserRequest();
        updateUserRequest.setFullName(fullName);
        updateUserRequest.setLocality(locality);
        updateUserRequest.setAge(age);

        String request = om.writeValueAsString(updateUserRequest);

        long updateUserId = 1L;

        String endpoint = "/users/" + updateUserId;

        // Act
        ResultActions resultActions = mockMvc.perform(
                put(endpoint)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(request)
                        .accept(MediaType.APPLICATION_JSON_UTF8));

        // Assert
        resultActions.andExpect(status().isBadRequest());

        verify(userMockService, never()).saveUser(any());
    }

    @Test
    public void testDeleteUserValidUser() throws Exception {
        // Arrange
        doNothing().when(userMockService).deleteUserById(1L);

        long deleteUserId = 1L;
        String endpoint = "/users/" + deleteUserId;

        // Act
        ResultActions resultActions = mockMvc.perform(delete(endpoint));

        // Assert
        resultActions.andExpect(status().isOk());

        verify(userMockService, times(1)).deleteUserById(1L);
    }
}
