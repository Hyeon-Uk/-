package com.hyeonuk.chatting.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinDto {
    private String email;
    private String password;
    private String passwordCheck;
    private String nickname;
}
