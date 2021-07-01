package com.pragmatest;

import com.pragmatest.exceptions.UserInvalidException;
import com.pragmatest.utils.UserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class UserEntityUtilsTest {

    UserUtils userUtils;

    @BeforeEach
    void init() {
        // Arrange
        userUtils = new UserUtils();
    }

    @Test
    void testIsAdultAgeUnder18() {
        // Arrange
        int age = 17;

        // Act
        boolean isAdult = userUtils.isAdult(age);

        // Assert
        assertFalse(isAdult);
    }

    @ParameterizedTest
    // Arrange
    @ValueSource(ints = {18, 19})
    void testIsAdultAgeIsOver18(int age) {
        // Act
        boolean isAdult = userUtils.isAdult(age);

        // Assert
        assertTrue(isAdult);
    }

    @Test
    void testIsAdultAge0() {
        // Act
        Executable executable = () -> {
            userUtils.isAdult(0);
        };

        // Assert
        assertThrows(UserInvalidException.class, executable);
    }
}


