package com.hyeonuk.chatting.board.service;

import com.hyeonuk.chatting.board.dto.BoardDto;
import com.hyeonuk.chatting.board.dto.BoardListDto;
import com.hyeonuk.chatting.board.dto.BoardRegisterDto;
import com.hyeonuk.chatting.board.dto.PageRequestDto;
import com.hyeonuk.chatting.board.entity.Board;
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
                for(int page = 0;page*size/size<boardList.size();page++){

                }
                PageRequestDto pageRequestDto = PageRequestDto.builder()
                        .page(page)
                        .size(size)
                        .build();

                BoardListDto all = boardService.findAll(pageRequestDto);

                assertThat()
            }

            @Test
            @DisplayName("findAllByMemberId")
            public void findAllByMemberIdSuccess() {

            }

            @Test
            @DisplayName("findAllByMemberId With PageRequestDto")
            public void findAllByMemberIdWithPageRequestDtoSuccess() {

            }
        }

        @Nested
        @DisplayName("failure")
        class Failure {
            @Test
            @DisplayName("findAll with Pageable failure without content")
            public void findAllWithPageableSuccess() {

            }

            @Test
            @DisplayName("findAllByMemberId failure")
            public void findAllByMemberIdFail() {

            }

            @Test
            @DisplayName("findAllByMemberId With PageRequestDto failure without content")
            public void findAllByMemberIdWithPageRequestDtoFail() {

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

            }
        }

        @Nested
        @DisplayName("failure")
        class Failure {
            @Test
            @DisplayName("title can not be null")
            public void titleNullTest() {

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
