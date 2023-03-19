package com.hyeonuk.chatting.member.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
    @Email(message = "이메일 형식을 확인해주세요")
    @NotBlank(message= "이메일을 입력해주세요")
    private String email;
    
    @NotBlank(message="비밀번호를 입력해주세요")
    private String password;
}
