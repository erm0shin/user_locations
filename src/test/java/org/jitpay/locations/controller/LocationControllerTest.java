package org.jitpay.locations.controller;

import org.jitpay.locations.dto.location.LocationDTO;
import org.jitpay.locations.dto.location.LocationRangeRequest;
import org.jitpay.locations.dto.location.TimedLocationDTO;
import org.jitpay.locations.dto.user.UserDTO;
import org.jitpay.locations.dto.user.UserWithLatestLocationDTO;
import org.jitpay.locations.dto.user.UserWithLocationRangeDTO;
import org.jitpay.locations.service.LocationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationControllerTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test_email";
    private static final String FIRST_NAME = "first_name";
    private static final String SECOND_NAME = "second_name";
    private static final String LATITUDE = "1.10";
    private static final String LONGITUDE = "2.20";

    @Value(value = "${local.server.port}")
    private int port;

    @MockBean
    LocationService locationService;

    @Test
    void submitLocationTest() {
        //@formatter:off
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new UserDTO(USER_ID, EMAIL, FIRST_NAME, SECOND_NAME))
        .when()
                .post("http://localhost:" + port + "/v1/location")
        .then()
                .statusCode(HttpStatus.ACCEPTED.value())
        .log();
        //@formatter:on
    }

    @Test
    void getLatestLocationTest() {
        var location = new UserWithLatestLocationDTO(
                USER_ID, EMAIL, FIRST_NAME, SECOND_NAME,
                new LocationDTO(LATITUDE, LONGITUDE)
        );
        Mockito.when(locationService.getUserLatestLocation(Mockito.any()))
                .thenReturn(location);

        //@formatter:off
        given()
        .when()
                .get("http://localhost:" + port + "/v1/location/latest?userId=" + USER_ID)
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo(USER_ID.toString()))
                .body("email", equalTo(EMAIL))
                .body("firstName", equalTo(FIRST_NAME))
                .body("secondName", equalTo(SECOND_NAME))
                .body("location.latitude", equalTo(LATITUDE))
                .body("location.longitude", equalTo(LONGITUDE))
        .log();
        //@formatter:on
    }

    @Test
    void getLocationRangeTest() {
        Mockito.when(locationService.getUserLocations(Mockito.any()))
                .thenReturn(new UserWithLocationRangeDTO(USER_ID, List.of(
                        new TimedLocationDTO(LocalDateTime.now().minusSeconds(30), new LocationDTO(LATITUDE, LONGITUDE))
                )));

        //@formatter:off
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(new LocationRangeRequest(USER_ID, LocalDateTime.now().minusMinutes(1), LocalDateTime.now()))
        .when()
                .post("http://localhost:" + port + "/v1/location/range")
        .then()
                .statusCode(HttpStatus.OK.value())
                .body("userId", equalTo(USER_ID.toString()))
                .body("locations.size()", equalTo(1))
                .body("locations[0].createdOn", notNullValue())
                .body("locations[0].location.latitude", equalTo(LATITUDE))
                .body("locations[0].location.longitude", equalTo(LONGITUDE))
        .log();
        //@formatter:on
    }

}
