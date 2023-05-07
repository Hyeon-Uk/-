package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.board.repository.BoardRepository;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {
    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private BoardRepository boardRepository;

    List<Member> memberList;
    List<Board> boardList;

    @BeforeEach
    public void init() {
        memberList = new ArrayList<>();
        boardList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            memberList.add(Member.builder()
                    .id(Integer.toUnsignedLong(i))
                    .email(String.format("test%d@gmail.com", i))
                    .password(String.format("test%d", i))
                    .nickname(String.format("test%d", i))
                    .memberSecurity(MemberSecurity.builder()
                            .salt("salt")
                            .build())
                    .build());
        }

        for (int i = 0; i < 55; i++) {
            boardList.add(Board.builder()
                    .id(Integer.toUnsignedLong(i))
                    .title(String.format("title%d", i))
                    .content(String.format("content%d", i))
                    .hit(0)
                    .member(memberList.get(i % memberList.size()))
                    .build());
        }
    }

    @Nested
    @DisplayName("findAll Test")
    public class FindAllTests {
        @Nested
        @DisplayName("success case")
        class Success {
            @Test
            @DisplayName("findAll success")
            public void findAllSuccessTest() {
                when(boardRepository.findAll()).thenReturn(boardList);

                //when
                BoardListDto all = boardService.findAll();
                //then
                assertThat(all.getContents().size()).isEqualTo(boardList
                        .size());
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isFalse();
            }

            @Test
            @DisplayName("findAll with Pageable success")
            public void findAllWithPageableSuccess() {
                int page = 0;
                int size = 10;
                //given
                Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
                Page<Board> mockResult = new PageImpl<Board>(boardList.stream().sorted(new Comparator<Board>() {
                    @Override
                    public int compare(Board o1, Board o2) {
                        return Long.compare(o2.getId(), o1.getId());
                    }
                }).limit(size).collect(Collectors.toList()), pageable, boardList.size());
                when(boardRepository.findAll(pageable)).thenReturn(mockResult);

                //when
                BoardListDto result = boardService.findAll(PageRequestDto.builder()
                        .size(size)
                        .page(page)
                        .build());

                //then
                assertThat(result.getContents().size()).isEqualTo(size);
                assertThat(result.isNext()).isTrue();
                assertThat(result.isPrev()).isFalse();

                for (int i = 0; i < 10; i++) {
                    assertThat(result.getContents().get(i).getId()).isEqualTo(boardList.get(boardList.size() - 1 - i).getId());
                }
            }

            @Test
            @DisplayName("findAllByMemberId")
            public void findAllByMemberIdSuccess() {
                when(boardRepository.findByMember(any()))
                        .thenReturn(
                                boardList
                                        .stream()
                                        .filter(b -> b.getMember().getId() == memberList.get(0).getId())
                                        .collect(Collectors.toList()));

                //when
                BoardListDto all = boardService.findAll(memberList.get(0).getId());
                //then
                assertThat(all.getContents().size()).isEqualTo(boardList
                        .stream().filter(b -> b.getMember().getId() == memberList.get(0).getId())
                        .collect(Collectors.toList())
                        .size());
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isFalse();
            }

            @Test
            @DisplayName("findAllByMemberId With PageRequestDto")
            public void findAllByMemberIdWithPageRequestDtoSuccess() {
                int page = 0;
                int size = 10;
                PageRequestDto pageRequestDto = PageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build();

                //given
                Pageable pageable = pageRequestDto.getPageable(Sort.by("created_at").descending());
                Page<Board> mockResult = new PageImpl<Board>(boardList.stream()
                        .filter(b->b.getMember().getId()==memberList.get(0).getId()).sorted(new Comparator<Board>() {
                    @Override
                    public int compare(Board o1, Board o2) {
                        return Long.compare(o2.getId(), o1.getId());
                    }
                }).limit(size).collect(Collectors.toList()), pageable, boardList.size());
                when(boardRepository.findByMember(any(),any()))
                        .thenReturn(mockResult);



                BoardListDto all = boardService.findAll(memberList.get(0).getId(), pageRequestDto);

                assertThat(all.getContents().size()).isEqualTo(size);
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isTrue();
            }
        }

        @Nested
        @DisplayName("failure case")
        class Failure {
            @Test
            @DisplayName("findAll with Pageable failure without content")
            public void findAllWithPageableSuccess() {
                int page = boardList.size()/10 + 1;
                int size = 10;
                //given
                Pageable pageable = PageRequest.of(page, size, Sort.by("created_at").descending());
                Page<Board> mockResult = new PageImpl<Board>(new ArrayList<Board>(), pageable, boardList.size());
                when(boardRepository.findAll(pageable)).thenReturn(mockResult);

                //when
                BoardListDto result = boardService.findAll(PageRequestDto.builder()
                        .size(size)
                        .page(page)
                        .build());

                //then
                assertThat(result.getContents().size()).isEqualTo(0);
                assertThat(result.isNext()).isFalse();
                assertThat(result.isPrev()).isTrue();
            }

            @Test
            @DisplayName("findAllByMemberId failure")
            public void findAllByMemberIdFail() {
                when(boardRepository.findByMember(any()))
                        .thenReturn(
                                boardList
                                        .stream()
                                        .filter(b -> b.getMember().getId() == Long.MAX_VALUE)
                                        .collect(Collectors.toList()));

                //when
                BoardListDto all = boardService.findAll(memberList.get(0).getId());
                //then
                assertThat(all.getContents().size()).isEqualTo(0);
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isFalse();
            }

            @Test
            @DisplayName("findAllByMemberId With PageRequestDto failure without content")
            public void findAllByMemberIdWithPageRequestDtoFail() {
                int page = boardList.size()/10 + 1;
                int size = 10;
                PageRequestDto pageRequestDto = PageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build();

                //given
                Pageable pageable = pageRequestDto.getPageable(Sort.by("created_at").descending());
                Page<Board> mockResult = new PageImpl<Board>(new ArrayList<>(), pageable, boardList.size());
                when(boardRepository.findByMember(any(),any()))
                        .thenReturn(mockResult);

                BoardListDto all = boardService.findAll(memberList.get(0).getId(), pageRequestDto);

                assertThat(all.getContents().size()).isEqualTo(0);
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isTrue();
            }
        }
    }
}