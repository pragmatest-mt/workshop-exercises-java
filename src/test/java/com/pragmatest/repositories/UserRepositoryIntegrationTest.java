package com.pragmatest.repositories;

import com.pragmatest.models.User;
import com.pragmatest.models.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private UserRepository userRepository;

    UserEntity userEntity1;
    UserEntity userEntity2;

    public UserRepositoryIntegrationTest() {
        // Arrange
        userEntity1 = new UserEntity();
        userEntity1.setFullName("John Smith");
        userEntity1.setLocality("London");
        userEntity1.setAge(20);

        userEntity2 = new UserEntity();
        userEntity2.setFullName("Mary Jones");
        userEntity2.setLocality("Manchester");
        userEntity2.setAge(24);
    }

    @BeforeEach
    void beforeTest() {
        // Arrange
        testEntityManager.persist(userEntity1);
        testEntityManager.persist(userEntity2);

        testEntityManager.flush();
    }

    @Test
    void testSaveUser() {
        // Arrange
        UserEntity userEntity3 = new UserEntity();
        userEntity3.setFullName("John Smith");
        userEntity3.setLocality("London");
        userEntity3.setAge(20);

        // Act
        UserEntity savedUser = userRepository.save(userEntity3);

        // Assert
        UserEntity retrievedUser = testEntityManager.find(UserEntity.class, savedUser.getId());

        assertEquals(userEntity3, retrievedUser);
    }

    @Test
    void testFindUserById() {
        // Act
        Optional<UserEntity> retrievedUser = userRepository.findById(1L);

        // Assert
        assertFalse(retrievedUser.isEmpty());
        assertEquals(userEntity1, retrievedUser.get());
    }

    @Test
    void testFindAllUsers() {
        // Act
        List<UserEntity> retrievedUsers = userRepository.findAll();

        // Assert
        assertFalse(retrievedUsers.isEmpty());
        assertTrue(retrievedUsers.contains(userEntity1));
        assertTrue(retrievedUsers.contains(userEntity2));
        assertEquals(2, retrievedUsers.size());
    }

    @Test
    void testFindUserByNonExistentId() {
        // Act
        Optional<UserEntity> retrievedUser = userRepository.findById(3L);

        // Assert
        assertTrue(retrievedUser.isEmpty());
    }

    @Test
    void testDeleteUser() {
        // Arrange
        Long id = userEntity1.getId();

        // Act
        userRepository.delete(userEntity1);
        UserEntity retrievedUserEntity = testEntityManager.find(UserEntity.class, id);

        // Assert
        assertNull(retrievedUserEntity);
    }
}
