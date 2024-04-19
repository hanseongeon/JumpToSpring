package com.example.sbb.comment;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentForm {

    @NotEmpty(message = "내용은 필수입니다")
    private String content;
}
