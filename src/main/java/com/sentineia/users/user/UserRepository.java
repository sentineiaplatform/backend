package com.sentineia.users.user;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseRepository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User> {

    @EntityGraph(attributePaths = {"perfil"})
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
