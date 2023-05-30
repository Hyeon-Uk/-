package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardDto;
import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.BoardRegisterDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.board.exception.BoardNotFoundException;
import com.hyeonuk.chatting.board.exception.CanNotBeNullException;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;

public interface BoardService {

    BoardDto save(BoardRegisterDto dto) throws CanNotBeNullException;

    BoardListDto findAll();
    BoardListDto findAll(PageRequestDto pageRequestDto);

    BoardListDto findAll(Long memberId);
    BoardListDto findAll(Long memberId,PageRequestDto pageRequestDto);

    BoardDto findById(Long boardId) throws BoardNotFoundException;

    default BoardDto entityToDto(Board entity){
        return BoardDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .hit(entity.getHit())
                .createdAt(entity.getCreatedAt())
                .member(MemberDto.builder()
                        .email(entity.getMember().getEmail())
                        .nickname(entity.getMember().getNickname())
                        .id(entity.getMember().getId())
                        .build())
                .build();
    }

    default Board dtoToEntity(BoardRegisterDto dto){
        return Board.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .member(Member.builder()
                        .id(dto.getMemberId())
                        .build())
                .hit(0)
                .build();
    }
}
