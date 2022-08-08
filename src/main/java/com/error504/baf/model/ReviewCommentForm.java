package com.error504.baf.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ReviewCommentForm {
    @NotEmpty(message="댓글을 작성해주세요.")
    private String content;

    private Boolean isAnonymous;
}
