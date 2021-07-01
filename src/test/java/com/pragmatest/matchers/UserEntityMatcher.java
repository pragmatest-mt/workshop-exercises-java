package com.pragmatest.matchers;

import com.pragmatest.models.UserEntity;
import org.mockito.ArgumentMatcher;

public class UserEntityMatcher implements ArgumentMatcher<UserEntity> {

    private UserEntity left;

    public UserEntityMatcher(UserEntity left) {
        this.left = left;
    }

    @Override
    public boolean matches(UserEntity argument) {
        if (left == argument) return true;
        if (argument == null) return false;

        return left.getAge() == argument.getAge() &&
                left.getLocality().equals(argument.getLocality()) &&
                left.getFullName().equals(argument.getFullName()) &&
                left.getId() == argument.getId();
    }
}
