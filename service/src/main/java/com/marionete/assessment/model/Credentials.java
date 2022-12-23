package com.marionete.assessment.model;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Credentials {
    @NotBlank(message = "Username required")
    private String username;

    @NotBlank(message = "Password required")
    private String password;
}
