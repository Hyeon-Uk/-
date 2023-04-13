package com.hyeonuk.chatting.member.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

import javax.naming.Binding;
import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
class MemberAuthControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ObjectMapper mapper;

    MultiValueMap<String, String> convertToMultiValueMap(Object obj) {
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        Map<String, String> maps = mapper.convertValue(obj, new TypeReference<Map<String, String>>() {
        });
        parameters.setAll(maps);

        return parameters;
    }

    @Nested
    @DisplayName("Join Test")
    class JoinTest {
        @Nested
        @DisplayName("success")
        class SuccessTest {

            @Test
            @DisplayName("join page success")
            public void joinPageLoadingSuccessTest() throws Exception {
                MvcResult mvcResult1 = mvc.perform(get("/auth/join"))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(view().name("auth/join"))
                        .andExpect(model().attributeExists("dto"))
                        .andReturn();
            }

            @Test
            @DisplayName("join Process Success")
            public void joinProcessSuccessTest() throws Exception {
                JoinDto dto = JoinDto.builder()
                        .email("a@gmail.com")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("testuser")
                        .build();
                MultiValueMap multiValueMap = convertToMultiValueMap(dto);
                MvcResult mvcResult = mvc.perform(post("/auth/join")
                                .params(multiValueMap))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/auth/login"))
                        .andExpect(view().name("redirect:/auth/login"))
                        .andReturn();

                assertThat(memberRepository.findAll().size()).isEqualTo(1);
            }
        }

        @Nested
        @DisplayName("failure")
        class FailureTest {
            JoinDto dto;

            @BeforeEach
            public void init() throws Exception {
                dto = JoinDto.builder()
                        .email("a@gmail.com")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("testUser")
                        .build();

                MultiValueMap<String, String> params = convertToMultiValueMap(dto);

                //기본 유저 가입
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection());
            }

            @Test
            @DisplayName("Email DuplicatedException")
            public void emailDuplicatedExceptionTest() throws Exception {
                JoinDto emailDuplicatedExceptionDto = JoinDto.builder()
                        .email(dto.getEmail())
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("testUser2")
                        .build();

                int beforeSize = memberRepository.findAll().size();
                //중복유저 가입
                MultiValueMap<String, String> params = convertToMultiValueMap(emailDuplicatedExceptionDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasErrors("dto"))
                        .andExpect(view().name("auth/join"));

                assertThat(memberRepository.findAll().size()).isEqualTo(beforeSize);
            }

            @Test
            @DisplayName("Email Blank Exception")
            public void emailBlankExceptionTest() throws Exception {
                JoinDto emailBlankDto = JoinDto.builder()
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("testUser2")
                        .build();
                //빈칸의 이메일 가입
                MultiValueMap<String, String> params = convertToMultiValueMap(emailBlankDto);
                mvc.perform(post("/auth/join")
                                .params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "email"))
                        .andExpect(view().name("auth/join"));

                emailBlankDto = JoinDto.builder()
                        .email("                 ")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("testUser2")
                        .build();

                params = convertToMultiValueMap(emailBlankDto);
                mvc.perform(post("/auth/join")
                                .params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "email"))
                        .andExpect(view().name("auth/join"));
            }

            @Test
            @DisplayName("Email Type Not Match Exception")
            public void emailTypeNotMatchExcpetionTest() throws Exception {
                JoinDto emailNotMatchDto = JoinDto.builder()
                        .email("aaaaa")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("testUser2")
                        .build();

                //이메일 타입이 아닌것
                MultiValueMap<String, String> params = convertToMultiValueMap(emailNotMatchDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attributeHasFieldErrors("dto", "email"))
                        .andExpect(view().name("auth/join"));
            }

            @Test
            @DisplayName("password Blank Exception")
            public void passwordBlankExceptionTest() throws Exception {
                JoinDto passwordBlankDto = JoinDto.builder()
                        .email("test@gmail.com")
                        .nickname("testUser2")
                        .build();
                //빈칸의 이메일 가입
                MultiValueMap<String, String> params = convertToMultiValueMap(passwordBlankDto);
                mvc.perform(post("/auth/join")
                                .params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "password"))
                        .andExpect(view().name("auth/join"));

                passwordBlankDto = JoinDto.builder()
                        .email("test@gmail.com")
                        .password("          ")
                        .passwordCheck("          ")
                        .nickname("testUser2")
                        .build();

                params = convertToMultiValueMap(passwordBlankDto);
                mvc.perform(post("/auth/join")
                                .params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "password"))
                        .andExpect(view().name("auth/join"));
            }

            @Test
            @DisplayName("Password Not Match Exception")
            public void passwordNotMatchExcpetionTest() throws Exception {
                JoinDto emailNotMatchDto = JoinDto.builder()
                        .email("aaaaa@gmail.com")
                        .password("testABC123!")
                        .passwordCheck("testABC321!")
                        .nickname("testUser2")
                        .build();

                //비밀번호 일치 x
                MultiValueMap<String, String> params = convertToMultiValueMap(emailNotMatchDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attributeHasErrors("dto"))
                        .andExpect(view().name("auth/join"));
            }

            @Test
            @DisplayName("Password Pattern Not Match Exception")
            public void passwordPatternNotMatchExcpetionTest() throws Exception {
                JoinDto emailNotMatchDto = JoinDto.builder()
                        .email("aaaaa@gmail.com")
                        .password("aaaa")
                        .passwordCheck("aaaa")
                        .nickname("testUser2")
                        .build();

                //비밀번호 패턴 일치 x
                MultiValueMap<String, String> params = convertToMultiValueMap(emailNotMatchDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().attributeHasFieldErrors("dto", "password"))
                        .andExpect(view().name("auth/join"));
            }

            @Test
            @DisplayName("nickname Blank Exception")
            public void nicknameBlankExceptionTest() throws Exception {
                JoinDto nicknameBlankDto = JoinDto.builder()
                        .email("test@gmail.com")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .build();
                //빈칸의 이메일 가입
                MultiValueMap<String, String> params = convertToMultiValueMap(nicknameBlankDto);
                mvc.perform(post("/auth/join")
                                .params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "nickname"))
                        .andExpect(view().name("auth/join"));

                nicknameBlankDto = JoinDto.builder()
                        .email("test@gmail.com")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname("                 ")
                        .build();

                params = convertToMultiValueMap(nicknameBlankDto);
                mvc.perform(post("/auth/join")
                                .params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "nickname"))
                        .andExpect(view().name("auth/join"));
            }

            @Test
            @DisplayName("Nickname DuplicatedException")
            public void nicknameDuplicatedExceptionTest() throws Exception {
                JoinDto emailDuplicatedExceptionDto = JoinDto.builder()
                        .email("absdaf@gmail.com")
                        .password("testABC123!")
                        .passwordCheck("testABC123!")
                        .nickname(dto.getNickname())
                        .build();

                int beforeSize = memberRepository.findAll().size();
                //중복유저 가입
                MultiValueMap<String, String> params = convertToMultiValueMap(emailDuplicatedExceptionDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasErrors("dto"))
                        .andExpect(view().name("auth/join"));

                assertThat(memberRepository.findAll().size()).isEqualTo(beforeSize);
            }
        }
    }

    @Nested
    @DisplayName("Login Test")
    public class LoginTest {
        JoinDto dto;

        //회원가입을 미리 해둠
        @BeforeEach
        public void init() throws Exception {
            dto = JoinDto.builder()
                    .email("test@gmail.com")
                    .password("abcdABCDE123!")
                    .passwordCheck("abcdABCDE123!")
                    .nickname("kim")
                    .build();

            mvc.perform(post("/auth/join")
                    .params(convertToMultiValueMap(dto)));
        }

        @Nested
        @DisplayName("success test")
        public class Success {
            @Test
            @DisplayName("login page success")
            public void loginPageSuccessTest() throws Exception {
                mvc.perform(get("/auth/login"))
                        .andExpect(status().isOk())
                        .andExpect(model().attributeExists("dto"))
                        .andExpect(view().name("auth/login"));
            }

            @Test
            @DisplayName("login process success")
            public void loginProcSuccessTest() throws Exception {
                LoginDto loginDto = LoginDto.builder()
                        .email(dto.getEmail())
                        .password(dto.getPassword())
                        .build();
                MvcResult mvcResult = mvc.perform(post("/auth/login")
                                .params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().is3xxRedirection())
                        .andExpect(view().name("redirect:/"))
                        .andReturn();

                //세션 확인
                HttpSession session = mvcResult.getRequest().getSession();
                MemberDto sessionMember = (MemberDto) session.getAttribute("member");
                assertThat(sessionMember.getEmail()).isEqualTo(dto.getEmail());
                assertThat(sessionMember.getNickname()).isEqualTo(dto.getNickname());
            }
        }

        @Nested
        @DisplayName("failure test")
        public class Failure {
            @Test
            @DisplayName("email blank exception test")
            public void emailBlankExceptionTest() throws Exception {
                LoginDto loginDto = LoginDto.builder()
                        .password(dto.getPassword())
                        .build();

                mvc.perform(post("/auth/login").params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "email"))
                        .andExpect(view().name("auth/login"))
                        .andDo(print());

                loginDto = LoginDto.builder()
                        .email("              ")
                        .password(dto.getPassword())
                        .build();

                mvc.perform(post("/auth/login").params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "email"))
                        .andExpect(view().name("auth/login"))
                        .andDo(print());
            }

            @Test
            @DisplayName("password blank exception test")
            public void passwordBlankExceptionTest() throws Exception {
                LoginDto loginDto = LoginDto.builder()
                        .email(dto.getEmail())
                        .build();

                mvc.perform(post("/auth/login").params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "password"))
                        .andExpect(view().name("auth/login"))
                        .andDo(print());

                loginDto = LoginDto.builder()
                        .email(dto.getEmail())
                        .password("                     ")
                        .build();

                mvc.perform(post("/auth/login").params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasFieldErrors("dto", "password"))
                        .andExpect(view().name("auth/login"))
                        .andDo(print());
            }

            @Test
            @DisplayName("email not match exception")
            public void emailNotMatchExceptionTest() throws Exception {
                LoginDto loginDto = LoginDto.builder()
                        .email("notExist@gmail.com")
                        .password("notExistPassword!")
                        .build();

                mvc.perform(post("/auth/login").params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasErrors("dto"))
                        .andExpect(view().name("auth/login"))
                        .andDo(print());
            }

            @Test
            @DisplayName("password not match exception")
            public void passwordNotMatchExceptionTest() throws Exception {
                LoginDto loginDto = LoginDto.builder()
                        .email(dto.getEmail())
                        .password("notExistPassword!")
                        .build();

                mvc.perform(post("/auth/login").params(convertToMultiValueMap(loginDto)))
                        .andExpect(status().isOk())
                        .andExpect(model().hasErrors())
                        .andExpect(model().attributeHasErrors("dto"))
                        .andExpect(view().name("auth/login"))
                        .andDo(print());
            }
        }
    }


    @Nested
    @DisplayName("Logout Test")
    class LogoutTest {
        LoginDto loginDto;
        JoinDto joinDto;

        @BeforeEach
        public void init() throws Exception {
            joinDto = JoinDto.builder()
                    .email("test@gmail.com")
                    .password("testTEST123!")
                    .passwordCheck("testTEST123!")
                    .nickname("tester")
                    .build();

            loginDto = LoginDto.builder()
                    .email(joinDto.getEmail())
                    .password(joinDto.getPassword())
                    .build();

            mvc.perform(post("/auth/join").params(convertToMultiValueMap(joinDto)));//가입
        }


        @Nested
        @DisplayName("success Test")
        public class SuccessTest {
            @Test
            @DisplayName("logout success")
            public void logoutSuccessTest() throws Exception{
                // Given
                // 로그인 세션 설정
                MemberDto memberDto = MemberDto.builder()
                                .email("test@example.com")
                                .build();

                // When
                MvcResult mvcResult = mvc.perform(post("/auth/logout").sessionAttr("member",memberDto))
                        .andExpect(redirectedUrl("/auth/login"))
                        .andReturn();

                //then
                HttpSession logoutSession = mvcResult.getRequest().getSession(false);
                assertThat(logoutSession).isNull();
            }
        }

        @Nested
        @DisplayName("failure Test")
        public class FailureTest {
            @Test
            @DisplayName("if unknown user access logout")
            public void unknownUserLogout() throws Exception{
                //아무것도 수행하지 않고 넘어감.
                //이 부분에 대해서 보안적인 처리 필요
                MvcResult mvcResult = mvc.perform(post("/auth/logout"))
                        .andReturn();
            }
        }
    }
}