package com.sentineia.users.user;

import com.sentineia.base.BaseService;
import com.sentineia.users.perfil.Perfil;
import com.sentineia.users.perfil.PerfilRepository;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService extends BaseService<User> {

    private final UserRepository userRepository;
    private final PerfilRepository perfilRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PerfilRepository perfilRepository, PasswordEncoder passwordEncoder) {
        super(repository);
        this.userRepository = repository;
        this.perfilRepository = perfilRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createFromRequest(CreateUserRequest request) {
        Perfil perfil = perfilRepository
                .findById(request.perfilId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil inválido."));
        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(normalizeEmail(request.email()));
        user.setPassword(request.password());
        user.setPerfil(perfil);
        return save(user);
    }

    @Override
    @Transactional
    public User save(User user) {
        if (user.getId() == null) {
            String email = normalizeEmail(user.getEmail());
            user.setEmail(email);
            if (userRepository.existsByEmail(email)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este e-mail já está em uso.");
            }
            if (user.getPerfil() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil é obrigatório.");
            }
        }
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
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPerfil().getId(),
                user.getPerfil().getName());
    }

    /**
     * Atualiza a palavra-passe (já em texto plano) para um utilizador existente — usado na recuperação de senha.
     */
    @Transactional
    public void updatePasswordFromReset(User user, String rawNewPassword) {
        if (rawNewPassword == null || rawNewPassword.length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "A palavra-passe deve ter pelo menos 8 caracteres.");
        }
        user.setPassword(passwordEncoder.encode(rawNewPassword));
        userRepository.save(user);
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
        Perfil perfil = perfilRepository
                .findById(body.perfilId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Perfil inválido."));
        user.setName(body.name().trim());
        user.setEmail(newEmail);
        user.setPerfil(perfil);
        return save(user);
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
