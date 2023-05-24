package org.jitpay.locations.service;

import org.jitpay.locations.configuration.test.SyncExecutorConfiguration;
import org.jitpay.locations.dto.location.LocationCreationDTO;
import org.jitpay.locations.dto.location.LocationDTO;
import org.jitpay.locations.dto.location.LocationRangeRequest;
import org.jitpay.locations.entity.LocationEntity;
import org.jitpay.locations.entity.UserEntity;
import org.jitpay.locations.repository.LocationRepository;
import org.jitpay.locations.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(SyncExecutorConfiguration.class)
public class LocationServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "test_email";
    private static final String FIRST_NAME = "first_name";
    private static final String SECOND_NAME = "second_name";
    private static final BigDecimal LATITUDE1 = new BigDecimal("1.10");
    private static final BigDecimal LONGITUDE1 = new BigDecimal("2.20");
    private static final BigDecimal LATITUDE2 = new BigDecimal("5.50");
    private static final BigDecimal LONGITUDE2 = new BigDecimal("6.60");

    @Autowired
    LocationService locationService;
    @Autowired
    LocationRepository locationRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void initDB() {
        userRepository.save(UserEntity.builder()
                .id(USER_ID)
                .email(EMAIL)
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .build());
    }


    @AfterEach
    void cleanDB() {
        locationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void saveLocationTest_success() {
        var createdOn = LocalDateTime.now();
        var locationToSave = new LocationCreationDTO(USER_ID, createdOn, new LocationDTO(LATITUDE1.toString(), LONGITUDE1.toString()));

        assertDoesNotThrow(() -> locationService.saveUserLocation(locationToSave));

        final var savedData = locationRepository.findAll().iterator().next();
        assertAll(
                () -> assertNotNull(savedData.getId()),
                () -> assertEquals(USER_ID, savedData.getUser().getId()),
                () -> assertEquals(createdOn, savedData.getCreatedOn()),
                () -> assertEquals(LATITUDE1, savedData.getLatitude()),
                () -> assertEquals(LONGITUDE1, savedData.getLongitude())
        );
    }

    @Test
    void saveLocationTest_userNotFound() {
        userRepository.deleteAll();
        var createdOn = LocalDateTime.now();
        var locationToSave = new LocationCreationDTO(USER_ID, createdOn, new LocationDTO(LATITUDE1.toString(), LONGITUDE1.toString()));

        assertDoesNotThrow(() -> locationService.saveUserLocation(locationToSave));
        assertEquals(0, locationRepository.count());
    }

    @Test
    void getUserLatestLocationTest() {
        var createdOn1 = LocalDateTime.now().minusMinutes(1);
        var createdOn2 = LocalDateTime.now();
        createTestLocations(createdOn1, createdOn2);

        var result = locationService.getUserLatestLocation(USER_ID);

        assertAll(
                () -> assertEquals(USER_ID, result.userId()),
                () -> assertEquals(EMAIL, result.email()),
                () -> assertEquals(FIRST_NAME, result.firstName()),
                () -> assertEquals(SECOND_NAME, result.secondName()),
                () -> assertEquals(LATITUDE2.toString(), result.location().latitude()),
                () -> assertEquals(LONGITUDE2.toString(), result.location().longitude())
        );
    }

    @Test
    void getUserLatestLocationTest_noData() {
        var result = locationService.getUserLatestLocation(USER_ID);
        assertNull(result);
    }

    @Test
    void getUserLocationRangeTest_allLocationsMatch() {
        var createdOn1 = LocalDateTime.now().minusMinutes(1);
        var createdOn2 = LocalDateTime.now();
        createTestLocations(createdOn1, createdOn2);

        var result = locationService.getUserLocations(
                new LocationRangeRequest(USER_ID, createdOn1.minusMinutes(1), createdOn2.plusMinutes(1))
        );

        assertAll(
                () -> assertEquals(USER_ID, result.userId()),
                () -> assertEquals(2, result.locations().size()),
                () -> assertTrue(result.locations().get(0).createdOn()
                        .isBefore(result.locations().get(1).createdOn())),

                () -> assertEquals(createdOn1, result.locations().get(0).createdOn()),
                () -> assertEquals(LATITUDE1.toString(), result.locations().get(0).location().latitude()),
                () -> assertEquals(LONGITUDE1.toString(), result.locations().get(0).location().longitude()),

                () -> assertEquals(createdOn2, result.locations().get(1).createdOn()),
                () -> assertEquals(LATITUDE2.toString(), result.locations().get(1).location().latitude()),
                () -> assertEquals(LONGITUDE2.toString(), result.locations().get(1).location().longitude())
        );
    }

    @Test
    void getUserLocationRangeTest_oneLocationMatches() {
        var createdOn1 = LocalDateTime.now().minusMinutes(1);
        var createdOn2 = LocalDateTime.now();
        createTestLocations(createdOn1, createdOn2);

        var result = locationService.getUserLocations(
                new LocationRangeRequest(USER_ID, createdOn2.minusSeconds(30), createdOn2.plusMinutes(1))
        );

        assertAll(
                () -> assertEquals(USER_ID, result.userId()),
                () -> assertEquals(1, result.locations().size()),

                () -> assertEquals(createdOn2, result.locations().get(0).createdOn()),
                () -> assertEquals(LATITUDE2.toString(), result.locations().get(0).location().latitude()),
                () -> assertEquals(LONGITUDE2.toString(), result.locations().get(0).location().longitude())
        );
    }

    @Test
    void getUserLocationRangeTest_noLocationMatches() {
        var createdOn1 = LocalDateTime.now().minusMinutes(1);
        var createdOn2 = LocalDateTime.now();
        createTestLocations(createdOn1, createdOn2);

        var result = locationService.getUserLocations(
                new LocationRangeRequest(USER_ID, createdOn2.plusSeconds(30), createdOn2.plusMinutes(1))
        );

        assertNull(result);
    }

    private void createTestLocations(LocalDateTime createdOn1, LocalDateTime createdOn2) {
        var user = userRepository.findById(USER_ID).get();
        locationRepository.saveAll(List.of(
                new LocationEntity(UUID.randomUUID(), LATITUDE1, LONGITUDE1, createdOn1, user),
                new LocationEntity(UUID.randomUUID(), LATITUDE2, LONGITUDE2, createdOn2, user)
        ));
    }

}
