package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
@Getter
@Setter
public class BoardForm {
    //    @NotEmpty(message="게시판 내용은 필수항목입니다.")
    @Size(max=200)
    private String boardName;

    @NotEmpty(message="게시판 소개는 필수항목입니다.")
    private String boardIntro;

    private Boolean isInterested;
}