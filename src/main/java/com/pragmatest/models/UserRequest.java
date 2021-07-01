package com.pragmatest.models;

import javax.validation.constraints.*;

public class UserRequest {

    @NotBlank(message = "Full Name cannot be empty.")
    @Size(max = 120, message = "Full Name is too long.")
    private String fullName;

    @NotBlank(message = "Locality cannot be empty.")
    @Size(max = 50, message = "Locality is too long.")
    private String locality;

    @Min(value = 1, message = "Age cannot be less than 1.")
    @Max(value = 200, message = "Age cannot be more than 200.")
    private int age;

    public UserRequest() {
        this(null, null, 0);
    }

    public UserRequest(String fullName, String locality, int age) {
        this.fullName = fullName;
        this.locality = locality;
        this.age = age;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
