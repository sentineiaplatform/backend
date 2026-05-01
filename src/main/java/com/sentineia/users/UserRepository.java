package com.sentineia.users;

import java.util.Optional;

import com.sentineia.base.BaseRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends BaseRepository<User> {

    Optional<User> findByEmail(String email);
}
