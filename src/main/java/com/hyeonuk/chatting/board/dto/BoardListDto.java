package com.hyeonuk.chatting.board.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BoardListDto {
    private boolean prev,next;
    private List<BoardDto> contents;
}
