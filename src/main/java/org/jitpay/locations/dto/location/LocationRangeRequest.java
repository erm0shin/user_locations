package org.jitpay.locations.dto.location;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocationRangeRequest(UUID userId, LocalDateTime from, LocalDateTime to) {
}
