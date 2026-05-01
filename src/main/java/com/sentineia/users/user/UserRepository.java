package com.sentineia.users.user;

import java.util.Optional;
import java.util.UUID;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
