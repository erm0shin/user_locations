package org.jitpay.locations.mapper;


import org.jitpay.locations.dto.location.LocationCreationDTO;
import org.jitpay.locations.dto.location.LocationDTO;
import org.jitpay.locations.dto.location.TimedLocationDTO;
import org.jitpay.locations.dto.user.UserWithLatestLocationDTO;
import org.jitpay.locations.dto.user.UserWithLocationRangeDTO;
import org.jitpay.locations.entity.LocationEntity;
import org.jitpay.locations.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class LocationMapper {

    public LocationEntity mapCreateRequest(LocationCreationDTO locationCreationDTO, UserEntity userEntity) {
        return LocationEntity.builder()
                .id(UUID.randomUUID())
                .latitude(new BigDecimal(locationCreationDTO.location().latitude()))
                .longitude(new BigDecimal(locationCreationDTO.location().longitude()))
                .createdOn(locationCreationDTO.createdOn())
                .user(userEntity)
                .build();
    }

    public UserWithLatestLocationDTO mapLatestLocation(LocationEntity location) {
        if (location == null) {
            return null;
        }
        final var user = location.getUser();
        return new UserWithLatestLocationDTO(
                user.getId(), user.getEmail(), user.getFirstName(), user.getSecondName(),
                new LocationDTO(location.getLatitude().toString(), location.getLongitude().toString())
        );
    }

    public UserWithLocationRangeDTO mapLocationRange(List<LocationEntity> locations) {
        if (locations.isEmpty()) {
            return null;
        }
        final var userId = locations.get(0).getUser().getId();

        final var timedLocations = locations.stream()
                .map(l -> new TimedLocationDTO(l.getCreatedOn(), new LocationDTO(l.getLatitude().toString(), l.getLongitude().toString())))
                .toList();

        return new UserWithLocationRangeDTO(userId, timedLocations);
    }

}
