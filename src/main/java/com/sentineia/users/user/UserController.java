package com.sentineia.users.user;

import com.sentineia.base.BaseController;
import com.sentineia.security.JwtService;

import jakarta.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController extends BaseController<User, UserService> {

    private final JwtService jwtService;

    public UserController(UserService service, JwtService jwtService) {
        super(service);
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public UserProfileResponse me(Authentication authentication) {
        return service().getProfileForAuthenticatedUser(authentication.getName());
    }

    @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileUpdateResponse patchMe(
            Authentication authentication, @Valid @RequestBody UpdateUserProfileRequest body) {
        User updated = service().updateProfileForAuthenticatedUser(authentication.getName(), body);
        String token = jwtService.generateAccessToken(updated);
        return new UserProfileUpdateResponse(
                updated.getId(),
                updated.getName(),
                updated.getEmail(),
                token,
                "Bearer",
                jwtService.getExpirationMs());
    }

    @PostMapping
    @Override
    public ResponseEntity<User> create(@Valid @RequestBody User entity) {
        return super.create(entity);
    }
}
