package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class EmailForm {

    @NotEmpty(message = "이메일은 필수항목입니다.")
    @Email
    private String newEmail;
}
