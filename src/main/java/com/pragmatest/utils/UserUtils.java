package com.pragmatest.utils;

import com.pragmatest.exceptions.UserInvalidException;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    public boolean isAdult(int age) {
        if (age <= 0) {
            throw new UserInvalidException("Age must be more than 0.");
        }

        return (age >= 18);
    }
}
