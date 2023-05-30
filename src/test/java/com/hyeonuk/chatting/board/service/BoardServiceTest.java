package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardDto;
import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.BoardRegisterDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.board.repository.BoardRepository;
import com.hyeonuk.chatting.integ.service.xss.XssFilterService;
import com.hyeonuk.chatting.integ.service.xss.XssFilterServiceImpl;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;

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

    @Spy
    private XssFilterService xssFilterService = new XssFilterServiceImpl();

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
            if(i<50) {
                boardList.add(Board.builder()
                        .id(Integer.toUnsignedLong(i + 1))
                        .title(String.format("title%d", i))
                        .content(String.format("content%d", i))
                        .hit(0)
                        .member(memberList.get(i % memberList.size()))
                        .build());
            }
            else{//script가 들어간 title과 content
                boardList.add(Board.builder()
                        .id(Integer.toUnsignedLong(i + 1))
                        .title(String.format("title%d <script>alert('hello world');</script>", i))
                        .content(String.format("content%d <script>alert('hello world');</script>", i))
                        .hit(0)
                        .member(memberList.get(i % memberList.size()))
                        .build());
            }
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
                List<BoardDto> contents = all.getContents();
                assertThat(contents.size()).isEqualTo(boardList.size());
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isFalse();
                contents.stream().forEach(content->{
                    assertAll("xssTest",
                            ()->assertThat(content.getTitle().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getTitle().indexOf(">")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf(">")).isEqualTo(-1)
                    );
                });
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

                result.getContents().stream().forEach(content->{
                    assertAll("xssTest",
                            ()->assertThat(content.getTitle().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getTitle().indexOf(">")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf(">")).isEqualTo(-1)
                    );
                });
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

                all.getContents().stream().forEach(content->{
                    assertAll("xssTest",
                            ()->assertThat(content.getTitle().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getTitle().indexOf(">")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf(">")).isEqualTo(-1)
                    );
                });
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
                        .filter(b -> b.getMember().getId() == memberList.get(0).getId()).sorted(new Comparator<Board>() {
                            @Override
                            public int compare(Board o1, Board o2) {
                                return Long.compare(o2.getId(), o1.getId());
                            }
                        }).limit(size).collect(Collectors.toList()), pageable, boardList.size());
                when(boardRepository.findByMember(any(), any()))
                        .thenReturn(mockResult);


                BoardListDto all = boardService.findAll(memberList.get(0).getId(), pageRequestDto);

                assertThat(all.getContents().size()).isEqualTo(size);
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isTrue();

                all.getContents().stream().forEach(content->{
                    assertAll("xssTest",
                            ()->assertThat(content.getTitle().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getTitle().indexOf(">")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf("<")).isEqualTo(-1),
                            ()->assertThat(content.getContent().indexOf(">")).isEqualTo(-1)
                    );
                });
            }
        }

        @Nested
        @DisplayName("failure case")
        class Failure {
            @Test
            @DisplayName("findAll with Pageable failure without content")
            public void findAllWithPageableSuccess() {
                int page = boardList.size() / 10 + 1;
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
                int page = boardList.size() / 10 + 1;
                int size = 10;
                PageRequestDto pageRequestDto = PageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build();

                //given
                Pageable pageable = pageRequestDto.getPageable(Sort.by("created_at").descending());
                Page<Board> mockResult = new PageImpl<Board>(new ArrayList<>(), pageable, boardList.size());
                when(boardRepository.findByMember(any(), any()))
                        .thenReturn(mockResult);

                BoardListDto all = boardService.findAll(memberList.get(0).getId(), pageRequestDto);

                assertThat(all.getContents().size()).isEqualTo(0);
                assertThat(all.isPrev()).isTrue();
                assertThat(all.isNext()).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("save test")
    class SaveTest {
        @Nested
        @DisplayName("success")
        class Success {

            @Test
            @DisplayName("save success")
            public void saveSuccessTest() {
                Member writer = memberList.get(0);

                BoardRegisterDto dto = BoardRegisterDto.builder()
                        .memberId(writer.getId())
                        .title("title!")
                        .content("content!!!")
                        .build();

                Board board = Board.builder()
                        .id(Integer.toUnsignedLong(boardList.size() + 1))
                        .title(dto.getTitle())
                        .content(dto.getContent())
                        .hit(0)
                        .member(writer)
                        .build();
                when(boardRepository.save(any(Board.class))).thenReturn(board);

                BoardDto savedBoard = boardService.save(dto);

                assertThat(savedBoard.getMember().getId()).isEqualTo(writer.getId());
                assertThat(savedBoard.getHit()).isEqualTo(0);
                assertThat(savedBoard.getTitle()).isEqualTo(dto.getTitle());
                assertThat(savedBoard.getContent()).isEqualTo(dto.getContent());

                assertAll("xssTest",
                        ()->assertThat(savedBoard.getTitle().indexOf("<")).isEqualTo(-1),
                        ()->assertThat(savedBoard.getTitle().indexOf(">")).isEqualTo(-1),
                        ()->assertThat(savedBoard.getContent().indexOf("<")).isEqualTo(-1),
                        ()->assertThat(savedBoard.getContent().indexOf(">")).isEqualTo(-1)
                );
            }
        }

        @Nested
        @DisplayName("failure")
        class Failure {
            @Test
            @DisplayName("title can not be null")
            public void titleNullTest(){
                Member writer = memberList.get(0);
                BoardRegisterDto dto = BoardRegisterDto.builder()
                                .memberId(writer.getId())
                                        .content("content!")
                                                .build();

                when(boardRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

                assertThrows(DataIntegrityViolationException.class,()->boardService.save(dto));
            }

            @Test
            @DisplayName("content can not be null")
            public void contentNullTest(){
                Member writer = memberList.get(0);
                BoardRegisterDto dto = BoardRegisterDto.builder()
                        .memberId(writer.getId())
                        .title("title!")
                        .build();

                when(boardRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

                assertThrows(DataIntegrityViolationException.class,()->boardService.save(dto));
            }

            @Test
            @DisplayName("member can not be null")
            public void memberNullTest(){
                BoardRegisterDto dto = BoardRegisterDto.builder()
                        .title("title!")
                        .content("content!")
                        .build();

                when(boardRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

                assertThrows(DataIntegrityViolationException.class,()->boardService.save(dto));
            }

        }
    }
}