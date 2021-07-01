package com.pragmatest.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pragmatest.matchers.UserMatcher;
import com.pragmatest.models.User;
import com.pragmatest.services.UserService;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {

    private static final ObjectMapper om = new ObjectMapper();

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private UserService userMockService;

    @Test
    public void testSaveUserValidUser() throws Exception {
        // Arrange
        User serviceInput = new User("Marisa Jones", "Newcastle", 20);
        User serviceOutput = new User(1L,"Marisa Jones", "Newcastle", 20);
        when(userMockService.saveUser(argThat(new UserMatcher(serviceInput)))).thenReturn(Optional.of(serviceOutput));

        String expectedResponseBody = om.writeValueAsString(serviceOutput);

        String endpoint = "/users";

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity(endpoint, serviceInput, String.class);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        JSONAssert.assertEquals(expectedResponseBody, response.getBody(), true);

        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(serviceInput)));
    }

    @Test
    public void testSaveUserUnderAgeUser() {
        // Arrange
        User newUser = new User("Marisa Jones", "Newcastle", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(newUser)))).thenReturn(Optional.empty());

        String endpoint = "/users";

        // Act
        ResponseEntity<String> response = testRestTemplate.postForEntity(endpoint, newUser, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(newUser)));
    }

    @Test
    public void testGetUserByIdValidId() throws JSONException {
        // Arrange
        User user = new User(1L, "John Smith", "London", 23);
        when(userMockService.getUserById(1L)).thenReturn(Optional.of(user));

        String expectedResponseBody = "{id:1, fullName:\"John Smith\", locality:\"London\", age:23}";
        String endpoint = "/users/1";

        // Act
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(endpoint, String.class);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

        JSONAssert.assertEquals(expectedResponseBody, responseEntity.getBody(), true);

        verify(userMockService, times(1)).getUserById(1L);
    }

    @Test
    public void testGetUsersByIdValidIds() throws JsonProcessingException, JSONException {
        // Arrange
        List<User> users = Arrays.asList(
                new User("John Smith", "London", 23),
                new User("Mary Walsh", "Liverpool", 30));

        when(userMockService.getAllUsers()).thenReturn(users);

        String expectedUserList = om.writeValueAsString(users);
        String endpoint = "/users";

        // Act
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(endpoint, String.class);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_UTF8, responseEntity.getHeaders().getContentType());

        JSONAssert.assertEquals(expectedUserList, responseEntity.getBody(), true);

        verify(userMockService, times(1)).getAllUsers();
    }

    @Test
    public void testGetUserByIdNonExistentId() {
        // Arrange
        String endpoint = "/users/5";

        // Act
        ResponseEntity<String> response = testRestTemplate.getForEntity(endpoint, String.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateUserValidUser() throws Exception {
        // Arrange
        User updateService = new User( 1L, "Peter Marshall", "London", 40);
        when(userMockService.saveUser(argThat(new UserMatcher(updateService)))).thenReturn(Optional.of(updateService));

        User getServiceOutput = new User( 1L, "John Marshall", "London", 40);
        when(userMockService.getUserById(1L)).thenReturn(Optional.of(getServiceOutput));


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(updateService), headers);

        String endpoint = "/users/1";

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(endpoint, HttpMethod.PUT, entity, String.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(om.writeValueAsString(updateService), response.getBody(), true);

        verify(userMockService, times(1)).getUserById(1L);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(updateService)));
    }

    @Test
    public void testUpdateUserUnderageUser() throws Exception {
        // Arrange
        User saveUserInput = new User( 1L, "Peter Marshall", "London", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(saveUserInput)))).thenReturn(Optional.empty());

        User getUserByIdOutput = new User( 1L, "John Marshall", "London", 40);
        when(userMockService.getUserById(1L)).thenReturn(Optional.of(getUserByIdOutput));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(saveUserInput), headers);

        String endpoint = "/users/1";

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(endpoint, HttpMethod.PUT, entity, String.class);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        verify(userMockService, times(1)).getUserById(1L);
        verify(userMockService, times(1)).saveUser(argThat(new UserMatcher(saveUserInput)));
    }

    @Test
    public void testUpdateUserNonExistentId() throws Exception {
        // Arrange
        User saveUserInput = new User( 1L, "Peter Marshall", "London", 17);
        when(userMockService.saveUser(argThat(new UserMatcher(saveUserInput)))).thenReturn(Optional.empty());

        when(userMockService.getUserById(1L)).thenReturn(Optional.empty());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(om.writeValueAsString(saveUserInput), headers);

        String endpoint = "/users/1";

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(endpoint, HttpMethod.PUT, entity, String.class);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(userMockService, times(1)).getUserById(1L);
        verify(userMockService, never()).saveUser(argThat(new UserMatcher(saveUserInput)));
    }

    @Test
    public void testDeleteUserValidUser() {
        // Arrange
        doNothing().when(userMockService).deleteUserById(1L);

        HttpEntity<String> entity = new HttpEntity<>(null, new HttpHeaders());

        String endpoint = "/users/1";

        // Act
        ResponseEntity<String> response = testRestTemplate.exchange(endpoint, HttpMethod.DELETE, entity, String.class);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userMockService, times(1)).deleteUserById(1L);
    }
}
