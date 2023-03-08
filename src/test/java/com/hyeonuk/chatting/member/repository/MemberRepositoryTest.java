package com.hyeonuk.chatting.member.repository;

import com.hyeonuk.chatting.member.entity.Member;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository repository;

    @Autowired
    private TestEntityManager em;

    /*
    * 베이스 멤버
    * */

    private Member member1,member2,member3;
    @BeforeEach
    public void initMembers(){
        member1 = Member.builder()
                .email("test1@gmail.com")
                .password("password1")
                .nickname("nickname1")
                .build();
        member2 = Member.builder()
                .email("test2@gmail.com")
                .password("password2")
                .nickname("nickname2")
                .build();
        member3 = Member.builder()
                .email("test3@gmail.com")
                .password("password3")
                .nickname("nickname3")
                .build();
    }
    @Nested
    @DisplayName("Member Save Test")
    public class MemberSaveTest{
        @Nested
        @DisplayName("Success")
        class Success{
            @Test
            public void saveTest(){
                //when
                repository.save(member1);

                //then
                List<Member> members = repository.findAll();
                assertThat(members.size()).isEqualTo(1);
                assertThat(members).contains(member1);

                //when
                repository.save(member2);

                //then
                members = repository.findAll();
                assertThat(members.size()).isEqualTo(2);
                assertThat(members).contains(member2);

                //when
                repository.save(member3);

                //then
                members = repository.findAll();
                assertThat(members.size()).isEqualTo(3);
                assertThat(members).contains(member3);
            }
        }

        @Nested
        @DisplayName("Failure")
        class Failure{
            @BeforeEach
            void initFailureCase(){
                repository.save(member1);
                repository.save(member2);
                repository.save(member3);
            }
            @Test
            @DisplayName("email duplication exception")
            public void emailDuplicationExceptionTest(){
                //given
                Member duplicationMember = Member.builder()
                        .email(member1.getEmail())
                        .password("duplication")
                        .nickname("duplicationMember")
                        .build();

                //when & then
                assertThrows(DataIntegrityViolationException.class,()->repository.save(duplicationMember));
                em.clear();
                List<Member> members = repository.findAll();

                assertThat(members.size()).isEqualTo(3);
                assertThat(members).doesNotContain(duplicationMember);
            }

            @Test
            @DisplayName("nickname duplication exception")
            public void nicknameDuplicationExceptionTest(){
                //given
                Member nicknameDuplicationMember = Member.builder()
                        .nickname(member1.getNickname())
                        .email("test4fsdafdsf@gmail.com")
                        .password("password")
                        .build();

                //when&then
                assertThrows(DataIntegrityViolationException.class,()->repository.save(nicknameDuplicationMember));

                em.clear();

                List<Member> members = repository.findAll();
                assertThat(members.size()).isEqualTo(3);
                assertThat(members).doesNotContain(nicknameDuplicationMember);
            }

            @Test
            @DisplayName("email, password, nickname blank exception")
            public void blankExceptionTest(){
                Member emailBlank = Member.builder()
                        .password("password")
                        .nickname("emailBlank")
                        .build();

                Member passwordBlank = Member.builder()
                        .email("passwordBlank@gmail.com")
                        .nickname("passwordBlank")
                        .build();

                Member nicknameBlank = Member.builder()
                        .email("nicknameBlank@gmail.com")
                        .password("nicknameBlank")
                        .build();

                //when & then
                assertThrows(DataIntegrityViolationException.class,()->repository.save(emailBlank));
                assertThrows(DataIntegrityViolationException.class,()->repository.save(passwordBlank));
                assertThrows(DataIntegrityViolationException.class,()->repository.save(nicknameBlank));
            }
        }
    }
}