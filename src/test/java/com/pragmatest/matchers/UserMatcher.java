package com.pragmatest.matchers;

import com.pragmatest.models.User;
import org.mockito.ArgumentMatcher;

public class UserMatcher implements ArgumentMatcher<User> {

    private User left;

    public UserMatcher(User left) {
        this.left = left;
    }

    @Override
    public boolean matches(User argument) {
        if (left == argument) return true;
        if (argument == null) return false;

        return left.getAge() == argument.getAge() &&
                left.getLocality().equals(argument.getLocality()) &&
                left.getFullName().equals(argument.getFullName()) &&
                left.getId() == argument.getId();
    }
}
