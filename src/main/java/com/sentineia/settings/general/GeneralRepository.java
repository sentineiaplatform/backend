package com.sentineia.settings.general;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GeneralRepository extends JpaRepository<General, UUID> {

    Optional<General> findTopByOrderByCreatedAtAsc();
}
