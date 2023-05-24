package org.jitpay.locations.repository;

import org.jitpay.locations.entity.LocationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LocationRepository extends CrudRepository<LocationEntity, UUID> {

    @Query("Select l from LocationEntity l order by l.createdOn desc limit 1")
    LocationEntity findLatestLocation(UUID userId);

    List<LocationEntity> findByUserIdAndCreatedOnGreaterThanAndCreatedOnLessThanOrderByCreatedOnAsc(
            UUID userId,
            LocalDateTime left,
            LocalDateTime right
    );

}
