package com.hyeonuk.chatting.board.dto;

import com.hyeonuk.chatting.member.dto.MemberDto;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardDto {
    private Long id;
    private String title,content;
    private int hit;
    private MemberDto member;

    private LocalDateTime createdAt;
}
