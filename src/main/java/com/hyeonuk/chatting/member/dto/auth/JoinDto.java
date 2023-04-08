package com.hyeonuk.chatting.member.dto.auth;


import com.hyeonuk.chatting.member.entity.MemberSecurity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JoinDto{
    @NotBlank(message="이메일을 입력해주세요")
    @Email(message = "이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message="비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+])[a-zA-Z\\d!@#$%^&*()_+]{8,20}$\n",message ="8자리이상, 20자리이하의 소문자,대문자,특수문자의 조합으로 만들어주세요")//8자리이상, 20자리이하의 소문자,대문자,특수문자의 조합
    private String password;
    @NotBlank(message="비밀번호확인을 입력해주세요")
    private String passwordCheck;
    @NotBlank(message="닉네임을 입력해주세요")
    private String nickname;

    MemberSecurity security;
}
