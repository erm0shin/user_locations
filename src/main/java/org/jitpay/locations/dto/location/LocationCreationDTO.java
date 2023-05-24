package org.jitpay.locations.dto.location;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocationCreationDTO(UUID userId, LocalDateTime createdOn, LocationDTO location) {
}
