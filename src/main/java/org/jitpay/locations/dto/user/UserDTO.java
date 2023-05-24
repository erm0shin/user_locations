package org.jitpay.locations.dto.user;

import java.util.UUID;

public record UserDTO(UUID userId, String email, String firstName, String secondName) {
}
