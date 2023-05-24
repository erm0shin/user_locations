package org.jitpay.locations.service;

import org.jitpay.locations.dto.user.UserDTO;
import org.jitpay.locations.entity.UserEntity;
import org.jitpay.locations.exception.AlreadyExistsException;
import org.jitpay.locations.exception.NotFoundException;
import org.jitpay.locations.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test_email";
    private static final String FIRST_NAME = "first_name";
    private static final String SECOND_NAME = "second_name";

    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @AfterEach
    void cleanDB() {
        userRepository.deleteAll();
    }

    @Test
    void saveUserTest_success() {
        var userToSave = new UserDTO(USER_ID, EMAIL, FIRST_NAME, SECOND_NAME);

        var result = userService.createUser(userToSave);

        var savedData = userRepository.findById(USER_ID).get();
        assertAll(
                () -> assertEquals(userToSave, result),
                () -> assertEquals(USER_ID, userToSave.userId()),
                () -> assertEquals(EMAIL, userToSave.email()),
                () -> assertEquals(FIRST_NAME, userToSave.firstName()),
                () -> assertEquals(SECOND_NAME, userToSave.secondName())
        );
    }

    @Test
    void saveUserTest_userAlreadyExists() {
        userRepository.save(UserEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .build());
        var userToSave = new UserDTO(USER_ID, "new_email", FIRST_NAME, SECOND_NAME);

        assertThrows(
                AlreadyExistsException.class,
                () -> userService.createUser(userToSave)
        );
        assertEquals(EMAIL, userRepository.findById(USER_ID).get().getEmail());
    }

    @Test
    void updateUserTest_success() {
        userRepository.save(UserEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .build());
        var newUserData = new UserDTO(USER_ID, "new_email", "new_first_name", "new_second_name");

        var result = userService.updateUserInfo(newUserData);

        var updatedData = userRepository.findById(USER_ID).get();
        assertAll(
                () -> assertEquals(newUserData, result),
                () -> assertEquals(USER_ID, newUserData.userId()),
                () -> assertEquals("new_email", newUserData.email()),
                () -> assertEquals("new_first_name", newUserData.firstName()),
                () -> assertEquals("new_second_name", newUserData.secondName())
        );
    }

    @Test
    void updateUserTest_userNotFound() {
        var userToUpdate = new UserDTO(USER_ID, EMAIL, FIRST_NAME, SECOND_NAME);

        assertThrows(
                NotFoundException.class,
                () -> userService.updateUserInfo(userToUpdate)
        );
        assertEquals(0, userRepository.count());
    }

}
