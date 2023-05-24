package org.jitpay.locations.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jitpay.locations.dto.location.LocationCreationDTO;
import org.jitpay.locations.dto.location.LocationRangeRequest;
import org.jitpay.locations.dto.user.UserWithLatestLocationDTO;
import org.jitpay.locations.dto.user.UserWithLocationRangeDTO;
import org.jitpay.locations.mapper.LocationMapper;
import org.jitpay.locations.repository.LocationRepository;
import org.jitpay.locations.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final LocationMapper locationMapper;

    @Async
    @Transactional
    public void saveUserLocation(LocationCreationDTO locationCreationDTO) {
        final var userOptional = userRepository.findById(locationCreationDTO.userId());
        if (userOptional.isEmpty()) {
            log.error("No user was found to save location by id = '{}'", locationCreationDTO.userId());
            return;
        }
        final var user = userOptional.get();

        final var locationEntity = locationMapper.mapCreateRequest(locationCreationDTO, user);
        locationRepository.save(locationEntity);
    }

    public UserWithLatestLocationDTO getUserLatestLocation(UUID userId) {
        final var location = locationRepository.findLatestLocation(userId);
        return locationMapper.mapLatestLocation(location);
    }

    public UserWithLocationRangeDTO getUserLocations(LocationRangeRequest locationRangeRequest) {
        final var locations = locationRepository.findByUserIdAndCreatedOnGreaterThanAndCreatedOnLessThanOrderByCreatedOnAsc(
                locationRangeRequest.userId(), locationRangeRequest.from(), locationRangeRequest.to()
        );
        return locationMapper.mapLocationRange(locations);
    }

}
