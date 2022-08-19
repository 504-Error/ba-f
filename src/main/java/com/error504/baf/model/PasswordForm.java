package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
@Getter
@Setter
public class PasswordForm {
    @Size(min = 8, max = 50)
    private String newPassword;

    @Size(min = 8, max = 50)
    private String newPasswordConfirm;
}
