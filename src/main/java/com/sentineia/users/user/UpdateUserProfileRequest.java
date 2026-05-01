package com.sentineia.users.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequest(
        @NotBlank String name,
        @NotBlank @Email String email) {
}
