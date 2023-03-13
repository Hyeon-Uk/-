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
import java.util.Optional;

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

    @Nested
    @DisplayName("Member update test")
    class MemberUpdateTest{
        @BeforeEach
        public void init(){
            repository.save(member1);
            repository.save(member2);
            repository.save(member3);
        }
        @Nested
        @DisplayName("Success")
        class Success{
            @Test
            @DisplayName("emailUpdate")
            public void success(){
                ///given
                Member forUpdate = Member.builder()
                        .id(member1.getId())
                        .email("updateEmail@gmail.com")
                        .password(member1.getPassword())
                        .nickname(member1.getNickname())
                        .build();

                //when
                repository.save(forUpdate);

                //then
                Member changed = repository.findById(member1.getId()).get();
                assertThat(changed).isSameAs(changed);
            }

            @Test
            @DisplayName("nickname Update")
            public void successNicknameUpdateTest(){
                ///given
                Member forUpdate = Member.builder()
                        .id(member1.getId())
                        .email(member1.getEmail())
                        .password(member1.getPassword())
                        .nickname("update nickname")
                        .build();

                //when
                repository.save(forUpdate);

                //then
                Member changed = repository.findById(member1.getId()).get();
                assertThat(changed).isSameAs(changed);
            }
        }

        @Nested
        @DisplayName("Failure")
        class Failure{
            @Test
            @DisplayName("Email, Nickname duplication exception")
            public void eMailNicknameDuplicationException(){
                ///given
                Member forUpdate = Member.builder()
                        .id(member1.getId())
                        .email(member2.getEmail())
                        .password(member1.getPassword())
                        .nickname(member1.getNickname())
                        .build();
                //when & then
                repository.save(forUpdate);
                assertThrows(Exception.class,()->em.flush());

                forUpdate = Member.builder()
                        .id(member1.getId())
                        .email(member1.getEmail())
                        .password(member1.getPassword())
                        .nickname(member2.getNickname())
                        .build();

                assertThrows(Exception.class,()->em.flush());
            }
        }
    }

    @Nested
    @DisplayName("Member delete test")
    class MemberDeleteTest{
        @BeforeEach
        public void init(){
            repository.save(member1);
            repository.save(member2);
            repository.save(member3);
        }
        @Nested
        @DisplayName("Success")
        class Success{
            @Test
            @DisplayName("deleteById success")
            public void deleteByIdTest(){
                //given
                Long deletedId = member1.getId();

                //when
                repository.deleteById(deletedId);

                //then
                List<Member> members = repository.findAll();
                assertThat(members.size()).isEqualTo(2);
                assertThat(members).doesNotContain(member1);

                //given
                Long deletedId2 = member3.getId();

                //when
                repository.deleteById(deletedId2);

                //then
                members = repository.findAll();
                assertThat(members.size()).isEqualTo(1);
                assertThat(members).doesNotContain(member1).doesNotContain(member3);
                assertThat(members).contains(member2);
            }

            @Test
            @DisplayName("delete by object test")
            public void deleteByObjectTest(){
                //given
                Member deletedMember = member1;

                //when
                repository.delete(deletedMember);

                //then
                List<Member> members = repository.findAll();
                assertThat(members).hasSize(2);
                assertThat(members).doesNotContain(member1);

                //given
                deletedMember = member3;

                //when
                repository.delete(deletedMember);

                //then
                members = repository.findAll();
                assertThat(members).hasSize(1).doesNotContain(member3);
            }
        }

        @Nested
        @DisplayName("Failure")
        class Failure{
            @Test
            @DisplayName("member id not found exception")
            public void memberIdNotFoundExceptionTest(){
                //given
                Long deletedId = Long.MAX_VALUE;

                List<Member> beforeDelete = repository.findAll();

                //when
                repository.deleteById(deletedId);
                List<Member> afterDelete = repository.findAll();

                assertThat(beforeDelete.size()).isEqualTo(afterDelete.size());
            }

            @Test
            @DisplayName("member not found exception")
            public void memberNotFoundExceptionTest(){
                //given
                Member notExistMember = Member.builder()
                        .id(Long.MAX_VALUE)
                        .build();

                List<Member> beforeDelete = repository.findAll();

                //when
                repository.delete(notExistMember);
                List<Member> afterDelete = repository.findAll();

                assertThat(beforeDelete.size()).isEqualTo(afterDelete.size());
            }
        }
    }

    @Nested
    @DisplayName("findByEmail Test")
    class FindByEmailTest{
        @BeforeEach
        public void init(){
            repository.save(member1);
            repository.save(member2);
            repository.save(member3);
        }
        @Nested
        @DisplayName("Success")
        class Success{
            @Test
            public void successTest(){
                //given
                String findEmail1 = member1.getEmail();
                String findEmail2 = member2.getEmail();
                String findEmail3 = member3.getEmail();

                //when
                Optional<Member> finded1 = repository.findByEmail(findEmail1);
                Optional<Member> finded2 = repository.findByEmail(findEmail2);
                Optional<Member> finded3 = repository.findByEmail(findEmail3);

                //then
                assertThat(finded1).isNotEmpty();
                assertThat(finded1.get()).isSameAs(member1);
                assertThat(finded2).isNotEmpty();
                assertThat(finded2.get()).isSameAs(member2);
                assertThat(finded3).isNotEmpty();
                assertThat(finded3.get()).isSameAs(member3);
            }
        }

        @Nested
        @DisplayName("Failure")
        class Failure{
            @Test
            @DisplayName("Email Not Found")
            public void emailNotFoundException(){
                //given
                String notExistEmail = "NotExistEmail";
                //when
                Optional<Member> notExist = repository.findByEmail(notExistEmail);
                //then
                assertThat(notExist).isEmpty();
            }
        }
    }

    @Nested
    @DisplayName("findByNickname Test")
    class FindByNicknameTest{
        @BeforeEach
        public void init(){
            repository.save(member1);
            repository.save(member2);
            repository.save(member3);
        }
        @Nested
        @DisplayName("Success")
        class Success{
            @Test
            public void successTest(){
                //given
                String findNickname1 = member1.getNickname();
                String findNickname2 = member2.getNickname();
                String findNickname3 = member3.getNickname();

                //when
                Optional<Member> finded1 = repository.findByNickname(findNickname1);
                Optional<Member> finded2 = repository.findByNickname(findNickname2);
                Optional<Member> finded3 = repository.findByNickname(findNickname3);

                //then
                assertThat(finded1).isNotEmpty();
                assertThat(finded1.get()).isSameAs(member1);
                assertThat(finded2).isNotEmpty();
                assertThat(finded2.get()).isSameAs(member2);
                assertThat(finded3).isNotEmpty();
                assertThat(finded3.get()).isSameAs(member3);
            }
        }

        @Nested
        @DisplayName("Failure")
        class Failure{
            @Test
            @DisplayName("Nickname Not Found")
            public void emailNotFoundException(){
                //given
                String notExistNickname = "NotExistNickname";
                //when
                Optional<Member> notExist = repository.findByNickname(notExistNickname);
                //then
                assertThat(notExist).isEmpty();
            }
        }
    }
}