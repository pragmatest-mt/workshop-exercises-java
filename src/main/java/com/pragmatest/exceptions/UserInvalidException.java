package com.pragmatest.exceptions;

public class UserInvalidException extends RuntimeException {

    public UserInvalidException(String message) {
        super(message);
    }

    public UserInvalidException() {
        super("Invalid User");
    }
}
