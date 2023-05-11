package com.hyeonuk.chatting.member.service.auth;

import com.hyeonuk.chatting.integ.service.encrypt.PasswordEncoder;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.auth.join.PasswordNotMatchException;
import com.hyeonuk.chatting.member.exception.auth.login.InfoNotMatchException;
import com.hyeonuk.chatting.member.exception.auth.login.RestrictionException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
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
    @DisplayName("save test")
    public class SaveTest{
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

    @Nested
    @DisplayName("login test")
    public class LoginTest{
        String email,password;

        @BeforeEach
        public void initRegist(){
            email = joinDto1.getEmail();
            password = joinDto1.getPassword();

            memberAuthService.save(joinDto1);
            memberAuthService.save(joinDto2);
            memberAuthService.save(joinDto3);
        }


        @Nested
        @DisplayName("login test success")
        public class Success{
            @Test
            public void successTest(){
                //given
                LoginDto dto = LoginDto.builder()
                        .email(email)
                        .password(password)
                        .build();


                //when
                MemberDto login = memberAuthService.login(dto);

                //then
                Member target = memberRepository.findByEmail(email).get();//로그인 대상자의 비교 정보

                assertAll("login",
                        ()->assertThat(login.getEmail()).isEqualTo(target.getEmail()),
                        ()->assertThat(login.getNickname()).isEqualTo(target.getNickname()),
                        ()->assertThat(login.getFriends().size()).isEqualTo(target.getFriends().size())
                        );
            }

            @Test
            public void tryCountInitWhenLoginSuccess(){
                //given
                LoginDto dto = LoginDto.builder()
                        .email(email)
                        .password("wrong")
                        .build();

                //when & then
                Member target = memberRepository.findByEmail(email).get();
                String message = assertThrows(InfoNotMatchException.class, () -> memberAuthService.login(dto)).getMessage();
                assertThat(message).isEqualTo("비밀번호가 일치하지 않습니다.");
                assertThat(target.getMemberSecurity().getTryCount()).isEqualTo(1);

                //login success
                dto.setPassword(password);
                MemberDto login = memberAuthService.login(dto);

                assertThat(target.getMemberSecurity().getTryCount()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("login test failure")
        public class Failure{
            @Test
            @DisplayName("password not match Exception")
            public void passwordNotMatchExceptionTest(){
                //given
                LoginDto dto = LoginDto.builder()
                        .email(email)
                        .password("wrong password")
                        .build();

                //when & then
                Member target = memberRepository.findByEmail(email).get();

                String message = assertThrows(InfoNotMatchException.class, () -> memberAuthService.login(dto)).getMessage();
                assertThat(message).isEqualTo("비밀번호가 일치하지 않습니다.");
                assertThat(target.getMemberSecurity().getTryCount()).isEqualTo(1);

                assertThrows(InfoNotMatchException.class, () -> memberAuthService.login(dto));
                assertThat(target.getMemberSecurity().getTryCount()).isEqualTo(2);

                //비밀번호 시도 3회 시 block됨.
                assertThrows(InfoNotMatchException.class, () -> memberAuthService.login(dto));
                assertThat(target.getMemberSecurity().getTryCount()).isEqualTo(0);
                assertThat(target.getMemberSecurity().getBlockedTime()).isNotNull();

                assertThrows(RestrictionException.class, () -> memberAuthService.login(dto));
            }
            @Test
            @DisplayName("email not found Exception")
            public void emailNotFoundExceptionTest(){
                //given
                LoginDto dto = LoginDto.builder()
                        .email("wrong email")
                        .password(password)
                        .build();

                //when & then
                String message = assertThrows(InfoNotMatchException.class, () -> memberAuthService.login(dto)).getMessage();
                assertThat(message).isEqualTo("해당하는 유저가 존재하지 않습니다.");
            }
        }
    }
}
