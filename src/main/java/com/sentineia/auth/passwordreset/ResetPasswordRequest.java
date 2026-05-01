package com.sentineia.auth.passwordreset;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Token é obrigatório.") String token,
        @NotBlank(message = "Informe a nova palavra-passe.")
                @Size(min = 8, max = 200, message = "A palavra-passe deve ter entre 8 e 200 caracteres.")
                String newPassword) {}
