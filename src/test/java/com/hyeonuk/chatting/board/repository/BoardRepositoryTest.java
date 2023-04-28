package com.hyeonuk.chatting.board.repository;

import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.ClassBasedNavigableIterableAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
public class BoardRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BoardRepository boardRepository;

    Member m1, m2, m3;

    @BeforeEach
    public void memberInit() {
        m1 = Member.builder()
                .email("test1@gmail.com")
                .password("test1")
                .nickname("tester1")
                .memberSecurity(MemberSecurity.builder()
                        .salt("salt")
                        .build())
                .build();

        m2 = Member.builder()
                .email("test2@gmail.com")
                .password("test2")
                .nickname("tester2")
                .memberSecurity(MemberSecurity.builder()
                        .salt("salt")
                        .build())
                .build();

        m3 = Member.builder()
                .email("test3@gmail.com")
                .password("test3")
                .nickname("tester3")
                .memberSecurity(MemberSecurity.builder()
                        .salt("salt")
                        .build())
                .build();

        memberRepository.save(m1);
        memberRepository.save(m2);
        memberRepository.save(m3);
    }

    @Nested
    @DisplayName("save test")
    public class SaveTest {
        @Nested
        @DisplayName("success")
        public class Success {
            @Test
            @DisplayName("board save success")
            public void boardSaveSuccessTest() {
                Board board = Board.builder()
                        .title("title1")
                        .content("content1")
                        .member(m1)
                        .build();

                boardRepository.save(board);
                assertThat(boardRepository.findAll().size()).isEqualTo(1);

                Board board2 = Board.builder()
                        .title("title2")
                        .content("content2")
                        .member(m1)
                        .build();

                boardRepository.save(board2);
                assertThat(boardRepository.findAll().size()).isEqualTo(2);

                Board board3 = Board.builder()
                        .title("title3")
                        .content("content3")
                        .member(m3)
                        .build();

                boardRepository.save(board3);
                assertThat(boardRepository.findAll().size()).isEqualTo(3);

                List<Board> boards = boardRepository.findAll();

                assertThat(boards).contains(board);
                assertThat(boards).contains(board2);
                assertThat(boards).contains(board3);
            }
        }

        @Nested
        @DisplayName("failure test")
        public class FailureTest {
            @Test
            @DisplayName("save failure because NullValue")
            public void NullTest() {
                Board titleNull = Board.builder()
                        .content("content")
                        .member(m1)
                        .build();

                assertThrows(DataIntegrityViolationException.class, () -> boardRepository.save(titleNull));

                Board contentNull = Board.builder()
                        .title("title")
                        .member(m2)
                        .build();
                assertThrows(DataIntegrityViolationException.class, () -> boardRepository.save(contentNull));

                Board memberNull = Board.builder()
                        .title("title")
                        .content("content")
                        .build();
                assertThrows(DataIntegrityViolationException.class, () -> boardRepository.save(memberNull));
            }
        }
    }

    @Nested
    @DisplayName("find Test")
    public class FindTest {
        Board b1, b2, b3, b4, b5;

        @BeforeEach
        public void init() {
            b1 = Board.builder()
                    .title("title1")
                    .content("content1")
                    .member(m1)
                    .build();
            b2 = Board.builder()
                    .title("title2")
                    .content("content2")
                    .member(m1)
                    .build();
            b3 = Board.builder()
                    .title("title3")
                    .content("content3")
                    .member(m1)
                    .build();

            b4 = Board.builder()
                    .title("title4")
                    .content("content4")
                    .member(m2)
                    .build();
            b5 = Board.builder()
                    .title("title5")
                    .content("content5")
                    .member(m3)
                    .build();

            boardRepository.save(b1);
            boardRepository.save(b2);
            boardRepository.save(b3);
            boardRepository.save(b4);
            boardRepository.save(b5);
        }

        @Nested
        @DisplayName("success")
        public class Success {
            @Test
            @DisplayName("findByMemberSuccess")
            public void findByMemberSuccess() {
                List<Board> m1Boards = boardRepository.findByMember(m1);
                assertThat(m1Boards.size()).isEqualTo(3);
                assertThat(m1Boards).contains(b1);
                assertThat(m1Boards).contains(b2);
                assertThat(m1Boards).contains(b3);

                List<Board> m2Boards = boardRepository.findByMember(m2);
                assertThat(m2Boards.size()).isEqualTo(1);
                assertThat(m2Boards).contains(b4);

                List<Board> m3Boards = boardRepository.findByMember(m3);
                assertThat(m3Boards.size()).isEqualTo(1);
                assertThat(m3Boards).contains(b4);
            }

            @Test
            @DisplayName("findByBoardId Success")
            public void findByIdSuccess() {
                Optional<Board> finded = boardRepository.findById(b1.getId());
                assertThat(finded).isNotEmpty();
                Board board1 = finded.get();
                assertThat(board1).isEqualTo(b1);
                assertThat(board1.getMember()).isEqualTo(m1);

                Optional<Board> finded2 = boardRepository.findById(b2.getId());
                assertThat(finded2).isNotEmpty();
                Board board2 = finded2.get();
                assertThat(board2).isEqualTo(b1);
                assertThat(board2.getMember()).isEqualTo(m2);
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure {
            @Test
            @DisplayName("findByMemberfailure because Not Found Member")
            public void failBecauseOfNotFoundMember() {
                assertThrows(DataIntegrityViolationException.class, () -> {
                    List<Board> notFoundMemberBoards = boardRepository.findByMember(Member.builder()
                            .id(Long.MAX_VALUE)
                            .build());
                });
            }

            @Test
            @DisplayName("findById failure because of not exist id")
            public void failBecauseOfNotExistId() {
                Optional<Board> notExist = boardRepository.findById(Long.MAX_VALUE);
                assertThat(notExist).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("update test")
    public class UpdateTest {
        Board b;

        @BeforeEach
        public void init() {
            b = Board.builder()
                    .title("title1")
                    .content("content1")
                    .member(m1)
                    .build();

            boardRepository.save(b);
        }

        @Nested
        @DisplayName("update success")
        public class success {
            @Test
            public void success() {
                b = Board.builder()
                        .id(b.getId())
                        .title("change")
                        .content(b.getContent())
                        .member(m1)
                        .build();

                boardRepository.save(b);

                Board updated = boardRepository.findById(b.getId()).get();
                assertThat(updated.getTitle()).isEqualTo("change");
            }

            @Test
            @DisplayName("update hit success")
            public void updateHitSuccess() {
                int beforeHit = b.getHit();
                b.updateHit();

                boardRepository.save(b);
                Board updated = boardRepository.findById(b.getId()).get();
                assertThat(updated.getHit()).isEqualTo(beforeHit + 1);
            }
        }

        @Nested
        @DisplayName("update failure")
        public class failure {
            @Test
            public void nullUpdate() {
                Board contentNull = Board.builder()
                        .id(b.getId())
                        .title(b.getTitle())
                        .content(null)
                        .member(m1)
                        .build();

                assertThrows(DataIntegrityViolationException.class,()->boardRepository.save(contentNull));

                Board titleNull = Board.builder()
                        .id(b.getId())
                        .title(null)
                        .content(b.getContent())
                        .member(m1)
                        .build();

                assertThrows(DataIntegrityViolationException.class,()->boardRepository.save(titleNull));

            }
        }
    }
}
