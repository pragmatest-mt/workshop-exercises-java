package com.pragmatest.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class UserEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String fullName;
    private String locality;
    private int age;

    public UserEntity() {
        this(null, null, null, 0);
    }

    public UserEntity(String fullName, String locality, int age) {
        this(null, fullName, locality, age);
    }

    public UserEntity(Long id, String fullName, String locality, int age) {
        this.id = id;
        this.fullName = fullName;
        this.locality = locality;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
