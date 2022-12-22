package com.marionete.service.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Credentials {
    @NotNull(message = "Username required")
    private String username;

    @NotBlank(message = "Password required")
    private String password;
}
