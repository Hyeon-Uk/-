package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardDto;
import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.BoardRegisterDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.board.repository.BoardRepository;
import com.hyeonuk.chatting.integ.service.xss.XssFilterService;
import com.hyeonuk.chatting.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


/*
* To Do :
* 1. 전체적으로 XSS 방어 코드 적용하기
* - 들어오고 나가는 content와 title에 xssFilter를 적용
* 2. update 기능 구현
 */

@Service
@AllArgsConstructor
public class BoardServiceImpl implements BoardService{
    private final BoardRepository boardRepository;
    private final XssFilterService xssFilterService;

    @Override
    public BoardDto save(BoardRegisterDto dto) {
        String title = dto.getTitle();
        String content = dto.getContent();

        dto.setTitle(xssFilterService.filter(title));
        dto.setContent(xssFilterService.filter(content));

        return entityToDto(boardRepository.save(dtoToEntity(dto)));
    }

    @Override
    public BoardListDto findAll() {
        List<Board> list = boardRepository.findAll();
        return BoardListDto.builder()
                .next(false)
                .prev(false)
                .contents(list.stream().map(this::entityToDto)
                .map(dto->{
                    dto.setTitle(xssFilterService.filter(dto.getTitle()));
                    dto.setContent(xssFilterService.filter(dto.getContent()));
                    return dto;
                })
                .toList())
                .build();
    }

    @Override
    public BoardListDto findAll(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.getPageable(Sort.by("created_at").descending());
        Page<Board> list = boardRepository.findAll(pageable);
        return BoardListDto.builder()
                .next(list.hasNext())
                .prev(list.hasPrevious())
                .contents(list.getContent().stream().map(this::entityToDto)
                .map(dto->{
                    dto.setTitle(xssFilterService.filter(dto.getTitle()));
                    dto.setContent(xssFilterService.filter(dto.getContent()));
                    return dto;
                }).toList())
                .build();
    }

    @Override
    public BoardListDto findAll(Long memberId) {
        List<Board> list = boardRepository.findByMember(Member.builder().id(memberId).build());
        return BoardListDto.builder()
                .next(false)
                .prev(false)
                .contents(list.stream().map(this::entityToDto)
                .map(dto->{
                    dto.setTitle(xssFilterService.filter(dto.getTitle()));
                    dto.setContent(xssFilterService.filter(dto.getContent()));
                    return dto;
                })
                .toList())
                .build();
    }

    @Override
    public BoardListDto findAll(Long memberId, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.getPageable(Sort.by("created_at").descending());
        Page<Board> list = boardRepository.findByMember(Member.builder()
                .id(memberId)
                .build()
                ,pageable);
        return BoardListDto.builder()
                .next(list.hasNext())
                .prev(list.hasPrevious())
                .contents(list.getContent().stream().map(this::entityToDto)
                .map(dto->{
                    dto.setTitle(xssFilterService.filter(dto.getTitle()));
                    dto.setContent(xssFilterService.filter(dto.getContent()));
                    return dto;
                })
                .toList())
                .build();
    }
}
