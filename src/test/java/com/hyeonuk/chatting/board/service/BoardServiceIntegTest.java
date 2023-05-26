package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardDto;
import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.BoardRegisterDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.board.exception.BoardNotFoundException;
import com.hyeonuk.chatting.board.repository.BoardRepository;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class BoardServiceIntegTest {
    @Autowired
    BoardService boardService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    MemberRepository memberRepository;

    List<Member> memberList = new ArrayList<>();
    List<Board> boardList = new ArrayList<>();

    public void xssAssert(BoardDto content) {
        assertAll("xssTest",
                () -> assertThat(content.getTitle().indexOf("<")).isEqualTo(-1),
                () -> assertThat(content.getTitle().indexOf(">")).isEqualTo(-1),
                () -> assertThat(content.getContent().indexOf("<")).isEqualTo(-1),
                () -> assertThat(content.getContent().indexOf(">")).isEqualTo(-1)
        );
    }

    @BeforeEach
    public void init() {
        MemberSecurity memberSecurity = MemberSecurity.builder()
                .salt("salt")
                .build();
        for (int i = 1; i <= 30; i++) {
            Member member = Member.builder()
                    .email(String.format("test%d@gmail.com", i))
                    .password("test")
                    .nickname(String.format("test%d", i))
                    .memberSecurity(memberSecurity)
                    .build();

            memberList.add(member);
        }
        memberRepository.saveAll(memberList);

        for (int i = 0; i < 111; i++) {
            Board board = Board.builder()
                    .title(String.format("title %d", i))
                    .content(String.format("content %d", i))
                    .member(memberList.get(i % memberList.size()))
                    .build();

            boardList.add(board);
        }
        boardRepository.saveAll(boardList);
    }

    @Nested
    @DisplayName("findAll Test")
    class FindAllTest {
        @Nested
        @DisplayName("success")
        class Success {
            @Test
            @DisplayName("findAll success")
            public void findAllSuccessTest() {
                BoardListDto all = boardService.findAll();

                assertThat(all.getContents().size()).isEqualTo(boardList.size());
                assertThat(all.isPrev()).isFalse();
                assertThat(all.isNext()).isFalse();
                all.getContents().forEach(content -> {
                    xssAssert(content);
                });
            }

            @Test
            @DisplayName("findAll with Pageable success")
            public void findAllWithPageableSuccess() {
                int size = 10;
                for(int page = 0;page<(int)Math.ceil(boardList.size()/(double)size);page++){
                    PageRequestDto pageRequestDto = PageRequestDto.builder()
                            .page(page)
                            .size(size)
                            .build();

                    BoardListDto all = boardService.findAll(pageRequestDto);

                    int start = page*size;
                    int end = Math.min(start+size-1,boardList.size()-1);

                    assertAll("pageable",
                            ()->assertThat(all.isPrev()).isEqualTo(start!=0),
                            ()->assertThat(all.isNext()).isEqualTo(end != boardList.size()-1),
                            ()->assertThat(all.getContents().size()).isEqualTo(end-start+1));

                    all.getContents().stream().forEach(content->xssAssert(content));
                }
            }

            @Test
            @DisplayName("findAllByMemberId")
            public void findAllByMemberIdSuccess() {
                for(Member member : memberList){
                    BoardListDto all = boardService.findAll(member.getId());

                    assertThat(all.getContents().size())
                            .isEqualTo(boardList.stream().filter(b->b.getMember().getId()==member.getId()).collect(Collectors.toList()).size());

                    all.getContents().stream().forEach(content->xssAssert(content));
                }
            }

            @Test
            @DisplayName("findAllByMemberId With PageRequestDto")
            public void findAllByMemberIdWithPageRequestDtoSuccess() {
                for(Member member : memberList){
                    List<Board> targetBoardList = boardList.stream()
                            .filter(b->b.getMember().getId()==member.getId())
                            .toList();

                    int size = 10;
                    for(int page = 0;page<(int)Math.ceil(targetBoardList.size()/(double)size);page++){
                        PageRequestDto pageRequestDto = PageRequestDto.builder()
                                .page(page)
                                .size(size)
                                .build();

                        BoardListDto all = boardService.findAll(member.getId(),pageRequestDto);

                        int start = page*size;
                        int end = Math.min(start+size-1,targetBoardList.size()-1);

                        assertAll("pageable",
                                ()->assertThat(all.isPrev()).isEqualTo(start!=0),
                                ()->assertThat(all.isNext()).isEqualTo(end != targetBoardList.size()-1),
                                ()->assertThat(all.getContents().size()).isEqualTo(end-start+1));

                        all.getContents().stream().forEach(content->xssAssert(content));
                    }
                }
            }
        }

        @Nested
        @DisplayName("failure")
        class Failure {
            @Test
            @DisplayName("findAll with Pageable failure without content")
            public void findAllWithPageableSuccess() {
                int size = 10;
                int page = (int)Math.ceil(boardList.size()/(double)size) +1;
                PageRequestDto dto = PageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build();

                BoardListDto all = boardService
                        .findAll(dto);

                assertThat(all.getContents().size()).isEqualTo(0);
            }

            @Test
            @DisplayName("findAllByMemberId failure")
            public void findAllByMemberIdFail() {
                BoardListDto all = boardService.findAll(Long.MAX_VALUE);
                assertAll("notFound",
                        ()->assertThat(all.isNext()).isFalse(),
                        ()->assertThat(all.isPrev()).isFalse(),
                        ()->assertThat(all.getContents().isEmpty()).isTrue());
            }

            @Test
            @DisplayName("findAllByMemberId With PageRequestDto failure without content")
            public void findAllByMemberIdWithPageRequestDtoFail() {
                int size = 10;
                int page = 0;
                PageRequestDto dto = PageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build();

                BoardListDto all = boardService.findAll(Long.MAX_VALUE, dto);
                assertAll("notFound",
                        ()->assertThat(all.isNext()).isFalse(),
                        ()->assertThat(all.isPrev()).isFalse(),
                        ()->assertThat(all.getContents().isEmpty()).isTrue());
            }
        }
    }

    @Nested
    @DisplayName("findById test")
    class FindByIdTest{
        @Nested
        @DisplayName("success")
        class Success{
            @Test
            public void success() throws BoardNotFoundException {
                for(Board board : boardList){
                    BoardDto result = boardService.findById(board.getId());
                    assertAll("board",
                            ()->assertThat(result.getTitle()).isEqualTo(board.getTitle()),
                            ()->assertThat(result.getContent()).isEqualTo(board.getContent()));

                    xssAssert(result);
                }
            }
        }

        @Nested
        @DisplayName("failure")
        class Failure{
            @Test
            public void boardNotFoundException(){
                Long target = Long.MAX_VALUE;

                assertThrows(BoardNotFoundException.class,()->boardService.findById(target));
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
                Long memberId = memberList.get(0).getId();
                int beforeSize = boardRepository.findAll().size();
                int member0Size = boardRepository.findByMember(Member.builder().id(memberId).build()).size();

                BoardRegisterDto dto = BoardRegisterDto.builder()
                        .title("hello world")
                        .content("hello everybody")
                        .memberId(memberId)
                        .build();

                BoardDto save = boardService.save(dto);

                assertAll("save",
                        ()->assertThat(save.getTitle()).isEqualTo(dto.getTitle()),
                        ()->assertThat(save.getContent()).isEqualTo(dto.getContent()),
                        ()->assertThat(boardRepository.findAll().size()).isEqualTo(beforeSize+1),
                        ()->assertThat(boardRepository.findByMember(Member.builder().id(memberId).build()).size()).isEqualTo(member0Size+1));
                xssAssert(save);
            }

            @Test
            @DisplayName("xss filter test")
            public void xssTest(){
                Long memberId = memberList.get(0).getId();
                int beforeSize = boardRepository.findAll().size();
                int member0Size = boardRepository.findByMember(Member.builder().id(memberId).build()).size();

                BoardRegisterDto dto = BoardRegisterDto.builder()
                        .title("hello<script>alert('hello');</script> world")
                        .content("hello every<script>location.href='/'</script>body")
                        .memberId(memberId)
                        .build();

                BoardDto save = boardService.save(dto);

                assertAll("save",
                        ()->assertThat(save.getTitle()).isEqualTo(dto.getTitle()),
                        ()->assertThat(save.getContent()).isEqualTo(dto.getContent()),
                        ()->assertThat(boardRepository.findAll().size()).isEqualTo(beforeSize+1),
                        ()->assertThat(boardRepository.findByMember(Member.builder().id(memberId).build()).size()).isEqualTo(member0Size+1));
                xssAssert(save);
            }
        }

        @Nested
        @DisplayName("failure")
        class Failure {
            @Test
            @DisplayName("title can not be null")
            public void titleNullTest() {
                Long memberId = memberList.get(0).getId();
                int beforeSize = boardRepository.findAll().size();
                int member0Size = boardRepository.findByMember(Member.builder().id(memberId).build()).size();

                BoardRegisterDto dto = BoardRegisterDto.builder()
                        .title("hello world")
                        .content("hello everybody")
                        .memberId(memberId)
                        .build();

                BoardDto save = boardService.save(dto);

                assertAll("save",
                        ()->assertThat(save.getTitle()).isEqualTo(dto.getTitle()),
                        ()->assertThat(save.getContent()).isEqualTo(dto.getContent()),
                        ()->assertThat(boardRepository.findAll().size()).isEqualTo(beforeSize+1),
                        ()->assertThat(boardRepository.findByMember(Member.builder().id(memberId).build()).size()).isEqualTo(member0Size+1));
                xssAssert(save);
            }

            @Test
            @DisplayName("content can not be null")
            public void contentNullTest() {

            }

            @Test
            @DisplayName("member can not be null")
            public void memberNullTest() {

            }

        }
    }
}
