package org.jitpay.locations.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.jitpay.locations.dto.location.LocationCreationDTO;
import org.jitpay.locations.dto.location.LocationRangeRequest;
import org.jitpay.locations.dto.user.UserWithLatestLocationDTO;
import org.jitpay.locations.dto.user.UserWithLocationRangeDTO;
import org.jitpay.locations.service.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/location")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Operation(summary = "Submit new user location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "User location is accepted")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void submitLocation(@RequestBody LocationCreationDTO locationCreationDTO) {
        locationService.saveUserLocation(locationCreationDTO);
    }

    @Operation(summary = "Get latest user location")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Latest user location",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserWithLatestLocationDTO.class))})
    })
    @GetMapping("/latest")
    public UserWithLatestLocationDTO getLatestLocation(@RequestParam("userId") UUID userId) {
        return locationService.getUserLatestLocation(userId);
    }

    @Operation(summary = "Get user locations within the given range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User locations",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserWithLocationRangeDTO.class))})
    })
    @PostMapping("/range")
    public UserWithLocationRangeDTO getLocationRange(@RequestBody LocationRangeRequest locationRangeRequest) {
        return locationService.getUserLocations(locationRangeRequest);
    }

}
