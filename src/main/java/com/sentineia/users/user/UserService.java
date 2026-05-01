package com.sentineia.users.user;

import com.sentineia.base.BaseService;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService extends BaseService<User> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.userRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null && user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return super.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfileForAuthenticatedUser(String principalEmail) {
        User user = userRepository
                .findByEmail(normalizeEmail(principalEmail))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilizador não encontrado."));
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail());
    }

    @Transactional
    public User updateProfileForAuthenticatedUser(String principalEmail, UpdateUserProfileRequest body) {
        User user = userRepository
                .findByEmail(normalizeEmail(principalEmail))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilizador não encontrado."));
        String newEmail = normalizeEmail(body.email());
        if (userRepository.existsByEmailAndIdNot(newEmail, user.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
        }
        user.setName(body.name().trim());
        user.setEmail(newEmail);
        return save(user);
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
