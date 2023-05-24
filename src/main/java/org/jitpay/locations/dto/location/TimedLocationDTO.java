package org.jitpay.locations.dto.location;

import java.time.LocalDateTime;

public record TimedLocationDTO(LocalDateTime createdOn, LocationDTO location) {
}
