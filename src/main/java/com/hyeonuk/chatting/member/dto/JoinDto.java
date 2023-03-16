package com.hyeonuk.chatting.member.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JoinDto {
    @NotBlank(message="이메일을 입력해주세요")
    private String email;
    @NotBlank(message="비밀번호를 입력해주세요")
    private String password;
    @NotBlank(message="비밀번호확인을 입력해주세요")
    private String passwordCheck;
    @NotBlank(message="닉네임을 입력해주세요")
    private String nickname;
}
