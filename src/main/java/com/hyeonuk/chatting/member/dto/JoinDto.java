package com.hyeonuk.chatting.member.dto;


import com.hyeonuk.chatting.integ.dto.BaseDto;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JoinDto implements BaseDto {
    @NotBlank(message="이메일을 입력해주세요")
    private String email;
    @NotBlank(message="비밀번호를 입력해주세요")
    private String password;
    @NotBlank(message="비밀번호확인을 입력해주세요")
    private String passwordCheck;
    @NotBlank(message="닉네임을 입력해주세요")
    private String nickname;

    @Override
    public boolean validate() {
        return email != null && StringUtils.isNotBlank(email)
                && password != null && StringUtils.isNotBlank(password)
                && passwordCheck != null && StringUtils.isNotBlank(password)
                && nickname != null && StringUtils.isNotBlank(nickname);
    }
}
