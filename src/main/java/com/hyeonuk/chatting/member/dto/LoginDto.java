package com.hyeonuk.chatting.member.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
    private String email;
    private String password;
}
