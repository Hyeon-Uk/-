package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardDto;
import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.BoardRegisterDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.board.exception.BoardNotFoundException;
import com.hyeonuk.chatting.board.exception.CanNotBeNullException;
import com.hyeonuk.chatting.board.repository.BoardRepository;
import com.hyeonuk.chatting.integ.service.xss.XssFilterService;
import com.hyeonuk.chatting.member.entity.Member;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
    public BoardDto save(BoardRegisterDto dto) throws CanNotBeNullException {
        String title = dto.getTitle();
        String content = dto.getContent();
        Long memberId = dto.getMemberId();

        if(title == null || title.equals("")
        || content == null || content.equals("")
        || memberId == null){
            throw new CanNotBeNullException("모든 값을 입력해주세요");
        }

        dto.setTitle(xssFilterService.filter(title));
        dto.setContent(xssFilterService.filter(content));

        try {
            return entityToDto(boardRepository.save(dtoToEntity(dto)));
        }catch(DataIntegrityViolationException e){
            throw new CanNotBeNullException("등록 오류");
        }
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
        Pageable pageable = pageRequestDto.getPageable(Sort.by("createdAt").descending());
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
        Pageable pageable = pageRequestDto.getPageable(Sort.by("createdAt").descending());
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

    @Override
    public BoardDto findById(Long boardId) throws BoardNotFoundException {
        return entityToDto(boardRepository.findById(boardId).orElseThrow(()-> new BoardNotFoundException("해당 게시물이 존재하지 않습니다.")));
    }
}
