package com.hyeonuk.chatting.board.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardRegisterDto {
    private long memberId;

    @NotBlank(message="제목을 입력해주세요")
    private String title;

    @NotBlank(message="내용을 입력해주세요")
    private String content;

}
