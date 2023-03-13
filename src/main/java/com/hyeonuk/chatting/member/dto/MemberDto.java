package com.hyeonuk.chatting.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
@Setter
public class MemberDto {
    private Long id;
    private String email;
    private String nickname;


}
