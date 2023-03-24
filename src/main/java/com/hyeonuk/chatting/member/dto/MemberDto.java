package com.hyeonuk.chatting.member.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Setter
@EqualsAndHashCode
public class MemberDto {
    private Long id;
    private String email;
    private String nickname;
    @Builder.Default
    private List<MemberDto> friends = new ArrayList<>();
}
