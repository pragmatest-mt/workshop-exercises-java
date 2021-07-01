package com.pragmatest.models;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserRequestValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void testValidUserRequest() {
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 20);
        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    public void testEmptyName() {
        UserRequest userRequest = new UserRequest("", "Manchester", 20);
        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertEquals(1, violations.size());

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("Full Name cannot be empty.", violation.getMessage());
    }

    @Test
    public void testLongName() {
        UserRequest userRequest = new UserRequest("", "Manchester", 20);
        userRequest.setFullName("John Smith John Smith John Smith John Smith John Smith John Smith John Smith " +
                "John Smith John Smith John Smith John Smith John Smith");
        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertEquals(1, violations.size());

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("Full Name is too long.", violation.getMessage());
    }

    @Test
    public void testEmptyLocality() {
        UserRequest userRequest = new UserRequest("John Smith", "", 20);
        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertEquals(1, violations.size());

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("Locality cannot be empty.", violation.getMessage());
    }

    @Test
    public void testLongLocality() {
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 20);
        userRequest.setLocality("Manchester Manchester Manchester Manchester Manchester");
        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertEquals(1, violations.size());

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("Locality is too long.", violation.getMessage());
    }

    @Test
    public void testAge0() {
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 0);

        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertEquals(1, violations.size());

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("Age cannot be less than 1.", violation.getMessage());
    }

    @Test
    public void testAge201() {
        UserRequest userRequest = new UserRequest("John Smith", "Manchester", 201);
        Set<ConstraintViolation<UserRequest>> violations
                = validator.validate(userRequest);

        assertEquals(1, violations.size());

        ConstraintViolation<UserRequest> violation = violations.iterator().next();
        assertEquals("Age cannot be more than 200.", violation.getMessage());
    }


}
