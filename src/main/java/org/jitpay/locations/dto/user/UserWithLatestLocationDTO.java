package org.jitpay.locations.dto.user;

import org.jitpay.locations.dto.location.LocationDTO;

import java.util.UUID;

public record UserWithLatestLocationDTO(UUID userId, String email, String firstName, String secondName, LocationDTO location) {
}
