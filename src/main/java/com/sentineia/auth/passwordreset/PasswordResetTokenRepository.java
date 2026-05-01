package com.sentineia.auth.passwordreset;

import java.util.Optional;

import com.sentineia.users.user.User;

import org.springframework.stereotype.Repository;

import com.sentineia.base.BaseRepository;

@Repository
public interface PasswordResetTokenRepository extends BaseRepository<PasswordResetToken> {

    Optional<PasswordResetToken> findByTokenAndUsedIsFalse(String token);

    void deleteByUser(User user);
}
