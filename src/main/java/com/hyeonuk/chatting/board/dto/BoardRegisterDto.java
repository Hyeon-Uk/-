package com.hyeonuk.chatting.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardRegisterDto {
    private Long memberId;

    @NotBlank(message="제목을 입력해주세요")
    private String title;

    @NotBlank(message="내용을 입력해주세요")
    private String content;

}
