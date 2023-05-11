package com.hyeonuk.chatting.member.service.auth;

import com.hyeonuk.chatting.integ.service.encrypt.PasswordEncoder;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.auth.join.PasswordNotMatchException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
public class MemberAuthServiceIntegrationTest {
    @Autowired
    private MemberAuthService memberAuthService;

    @Autowired
    private MemberRepository memberRepository;//테스트 결과를 확인하기 위한 repository

    @Nested
    @DisplayName("save test")
    public class SaveTest{
        Member m1,m2,m3;
        JoinDto joinDto1,joinDto2,joinDto3;

        @BeforeEach
        public void init(){
            m1 = Member.builder()
                    .email("member1")
                    .password("password1")
                    .nickname("m1")
                    .memberSecurity(MemberSecurity.builder()
                            .salt("salt")
                            .build())
                    .build();
            m2 = Member.builder()
                    .email("member2")
                    .password("password2")
                    .nickname("m2")
                    .memberSecurity(MemberSecurity.builder()
                            .salt("salt")
                            .build())
                    .build();
            m3 = Member.builder()
                    .email("member3")
                    .password("password3")
                    .nickname("m3")
                    .memberSecurity(MemberSecurity.builder()
                            .salt("salt")
                            .build())
                    .build();
            memberRepository.save(m1);
            memberRepository.save(m2);
            memberRepository.save(m3);


            joinDto1 = JoinDto.builder()
                    .email("user1@gmail.com")
                    .password("user1Password")
                    .passwordCheck("user1Password")
                    .nickname("user1")
                    .build();

            joinDto2 = JoinDto.builder()
                    .email("user2@gmail.com")
                    .password("user2Password")
                    .passwordCheck("user2Password")
                    .nickname("user2")
                    .build();

            joinDto3 = JoinDto.builder()
                    .email("user3@gmail.com")
                    .password("user3Password")
                    .passwordCheck("user3Password")
                    .nickname("user3")
                    .build();
        }
        @Nested
        @DisplayName("save test success")
        public class Success{
            @Test
            public void successTest(){
                int beforeSize = memberRepository.findAll().size();

                //given & when
                String inputPassword = joinDto1.getPassword();
                MemberDto save = memberAuthService.save(joinDto1);

                //then
                assertThat(save.getEmail()).isEqualTo(joinDto1.getEmail());
                assertThat(save.getNickname()).isEqualTo(joinDto1.getNickname());

                //in repository
                Optional<Member> result = memberRepository.findById(save.getId());
                assertThat(result).isNotEmpty();
                assertThat(memberRepository.findAll().size()).isEqualTo(beforeSize+1);

                Member saved = result.get();
                assertAll("in db",
                        ()->assertThat(saved.getId()).isEqualTo(save.getId()),
                        ()->assertThat(saved.getEmail()).isEqualTo(save.getEmail()),
                        ()->assertThat(saved.getNickname()).isEqualTo(save.getNickname()),
                        ()->assertThat(saved.getPassword()).isNotEqualTo(inputPassword),//join으로 들어온 패스워드를 암호화해서 저장해야 하기때문에 같으면 안됨
                        ()->assertThat(saved.getFriends().size()).isEqualTo(0),
                        ()->assertThat(saved.getCreatedAt()).isNotNull(),
                        ()->assertThat(saved.getCreatedAt()).isNotNull(),
                        ()->assertThat(saved.getMemberSecurity().getSalt()).isNotNull(),
                        ()->assertThat(saved.getMemberSecurity().getBlockedTime()).isNull(),
                        ()->assertThat(saved.getMemberSecurity().getTryCount()).isEqualTo(0),
                        ()->assertThat(saved.getMemberSecurity().getCreatedAt()).isNotNull(),
                        ()->assertThat(saved.getMemberSecurity().getUpdatedAt()).isNotNull()
                        );
            }
        }

        @Nested
        @DisplayName("save test failure")
        public class Failure{
            @Test
            @DisplayName("password not match Exception")
            public void passwordNotMatchExceptionTest(){
                //given
                joinDto1.setPasswordCheck(joinDto1.getPassword().concat(joinDto1.getPassword()));

                //when & then
                String message = assertThrows(PasswordNotMatchException.class, () -> memberAuthService.save(joinDto1)).getMessage();

                assertThat(message).isEqualTo("비밀번호가 일치하지 않습니다.");
            }
            @Test
            @DisplayName("email duplication Exception")
            public void emailDuplicationExceptionTest(){
                //given
                Member already = memberRepository.findAll().get(0);
                joinDto1.setEmail(already.getEmail());

                //when & then
                String message = assertThrows(AlreadyExistException.class, () -> memberAuthService.save(joinDto1)).getMessage();
                StringBuilder sb = new StringBuilder();

                sb.append(joinDto1.getEmail()).append("은 이미 존재하는 이메일입니다.");
                assertThat(message).isEqualTo(sb.toString());
            }

            @Test
            @DisplayName("nickname duplication Exception")
            public void nicknameDuplicationExceptionTest(){
                //given
                Member already = memberRepository.findAll().get(0);
                joinDto1.setNickname(already.getNickname());

                //when & then
                String message = assertThrows(AlreadyExistException.class, () -> memberAuthService.save(joinDto1)).getMessage();
                StringBuilder sb = new StringBuilder();

                sb.append(joinDto1.getNickname()).append("은 이미 존재하는 닉네임입니다.");
                assertThat(message).isEqualTo(sb.toString());
            }
        }
    }
}
