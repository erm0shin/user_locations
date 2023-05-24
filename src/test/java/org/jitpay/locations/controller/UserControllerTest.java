package org.jitpay.locations.controller;

import org.jitpay.locations.dto.user.UserDTO;
import org.jitpay.locations.exception.AlreadyExistsException;
import org.jitpay.locations.exception.NotFoundException;
import org.jitpay.locations.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test_email";
    private static final String FIRST_NAME = "first_name";
    private static final String SECOND_NAME = "second_name";
    private static final UserDTO USER = new UserDTO(USER_ID, EMAIL, FIRST_NAME, SECOND_NAME);

    @Value(value = "${local.server.port}")
    private int port;

    @MockBean
    UserService userService;

    @Test
    void createUserTest_success() {
        Mockito.when(userService.createUser(USER))
                .thenReturn(USER);

        //@formatter:off
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(USER)
        .when()
                .post("http://localhost:" + port + "/v1/user")
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("userId", equalTo(USER_ID.toString()))
                .body("email", equalTo(EMAIL))
                .body("firstName", equalTo(FIRST_NAME))
                .body("secondName", equalTo(SECOND_NAME))
        .log();
        //@formatter:on
    }

    @Test
    void createUserTest_alreadyExists() {
        Mockito.when(userService.createUser(Mockito.any()))
                .thenThrow(new AlreadyExistsException("test_exception"));

        //@formatter:off
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(USER)
        .when()
                .post("http://localhost:" + port + "/v1/user")
        .then()
                .statusCode(HttpStatus.CONFLICT.value())
                .body("error", equalTo("Resource with this id already exists: test_exception"))
        .log();
        //@formatter:on
    }

    @Test
    void updateUserInfoTest_success() {
        Mockito.when(userService.updateUserInfo(USER))
                .thenReturn(USER);

        //@formatter:off
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(USER)
        .when()
                .put("http://localhost:" + port + "/v1/user")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo(USER_ID.toString()))
                .body("email", equalTo(EMAIL))
                .body("firstName", equalTo(FIRST_NAME))
                .body("secondName", equalTo(SECOND_NAME))
        .log();
        //@formatter:on
    }

    @Test
    void updateUserInfoTest_userNotFound() {
        Mockito.when(userService.updateUserInfo(Mockito.any()))
                .thenThrow(new NotFoundException("test_exception"));

        //@formatter:off
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(USER)
        .when()
                .put("http://localhost:" + port + "/v1/user")
        .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .body("error", equalTo("Resource wasn't found: test_exception"))
        .log();
        //@formatter:on
    }

}
