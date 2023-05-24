package org.jitpay.locations.dto.user;

import org.jitpay.locations.dto.location.TimedLocationDTO;

import java.util.List;
import java.util.UUID;

public record UserWithLocationRangeDTO(UUID userId, List<TimedLocationDTO> locations) {
}
