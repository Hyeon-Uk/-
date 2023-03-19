package com.hyeonuk.chatting.member.service;

import com.hyeonuk.chatting.integ.service.encrypt.PasswordEncoder;
import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.NotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import com.hyeonuk.chatting.member.service.auth.MemberAuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceImplTest {
    @InjectMocks
    private MemberAuthServiceImpl memberAuthService;

    @Mock
    private MemberRepository mockMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;


    @BeforeEach
    public void init(){
        lenient().when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }



    @Nested
    @DisplayName("save test")
    public class SaveTest{
        JoinDto joinDto1,joinDto2,joinDto3;

        @BeforeEach
        public void init(){
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
                //given
                when(mockMemberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(null));
                when(mockMemberRepository.findByNickname(any())).thenReturn(Optional.ofNullable(null));
                when(mockMemberRepository.save(any())).thenReturn(
                        Member
                                .builder()
                                .email(joinDto1.getEmail())
                                .password(joinDto1.getPassword())
                                .nickname(joinDto1.getNickname())
                                .build()
                );

                //when
                MemberDto save = memberAuthService.save(joinDto1);

                //then
                assertThat(save.getEmail()).isEqualTo(joinDto1.getEmail());
                assertThat(save.getNickname()).isEqualTo(joinDto1.getNickname());

                verify(mockMemberRepository,times(1)).findByEmail(joinDto1.getEmail());
                verify(mockMemberRepository,times(1)).findByNickname(joinDto1.getNickname());
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
                String message = assertThrows(IllegalArgumentException.class, () -> memberAuthService.save(joinDto1)).getMessage();

                assertThat(message).isEqualTo("비밀번호가 일치하지 않습니다.");
            }
            @Test
            @DisplayName("email duplication Exception")
            public void emailDuplicationExceptionTest(){
                //given
                when(mockMemberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(Member.builder()
                                .email(joinDto1.getEmail())
                        .build()));


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
                when(mockMemberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(null));
                when(mockMemberRepository.findByNickname(any())).thenReturn(Optional.ofNullable(Member.builder()
                        .nickname(joinDto1.getNickname())
                        .build()));


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
        LoginDto loginDto;
        Member loginMember;

        @BeforeEach
        public void init(){
            loginDto = LoginDto.builder()
                    .email("test@gmail.com")
                    .password("test")
                    .build();
            loginMember = Member.builder()
                    .id(1l)
                    .email(loginDto.getEmail())
                    .nickname("test")
                    .password(passwordEncoder.encode(loginDto.getPassword()))
                    .build();
        }

        @Nested
        @DisplayName("login test success")
        public class Success{
            @Test
            public void successTest(){
                //given
                when(mockMemberRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.ofNullable(loginMember));

                //when
                MemberDto login = memberAuthService.login(loginDto);

                //then
                assertThat(login.getNickname()).isEqualTo(loginMember.getNickname());
                assertThat(login.getEmail()).isEqualTo(loginMember.getEmail());
                assertThat(login.getId()).isEqualTo(loginMember.getId());

                verify(mockMemberRepository,times(1)).findByEmail(loginDto.getEmail());
            }
        }

        @Nested
        @DisplayName("login test failure")
        public class Failure{
            @Test
            @DisplayName("password not match Exception")
            public void passwordNotMatchExceptionTest(){
                //given
                when(mockMemberRepository.findByEmail(any())).thenReturn(
                        Optional.ofNullable(Member.builder()
                                .password(loginMember.getPassword().concat("not matches"))
                                .build())
                );

                //when & then
                String message = assertThrows(IllegalArgumentException.class, () -> memberAuthService.login(loginDto)).getMessage();
                assertThat(message).isEqualTo("비밀번호가 일치하지 않습니다.");
            }
            @Test
            @DisplayName("email not found Exception")
            public void emailNotFoundExceptionTest(){
                //given
                when(mockMemberRepository.findByEmail(any())).thenReturn(Optional.ofNullable(null));

                //when & then
                String message = assertThrows(NotFoundException.class,()->memberAuthService.login(loginDto)).getMessage();
                assertThat(message).isEqualTo("해당하는 유저가 존재하지 않습니다.");
            }
        }
    }
}