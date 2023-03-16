package com.hyeonuk.chatting.member.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyeonuk.chatting.member.dto.JoinDto;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
        Map<String, String> maps = mapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
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
                mvc.perform(get("/auth/join"))
                        .andDo(print())
                        .andExpect(status().isOk());

                JoinDto dto = JoinDto.builder()
                        .email("a@gmail.com")
                        .password("test")
                        .passwordCheck("test")
                        .nickname("testuser")
                        .build();
                MultiValueMap multiValueMap = convertToMultiValueMap(dto);
                mvc.perform(post("/auth/join")
                                .params(multiValueMap))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/auth/login"));

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
                        .password("test")
                        .passwordCheck("test")
                        .nickname("testUser")
                        .build();

                MultiValueMap<String,String> params = convertToMultiValueMap(dto);

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
                        .password("test")
                        .passwordCheck("test")
                        .nickname("testUser2")
                        .build();

                int beforeSize = memberRepository.findAll().size();

                //중복유저 가입
                MultiValueMap<String,String> params = convertToMultiValueMap(emailDuplicatedExceptionDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk());

                assertThat(memberRepository.findAll().size()).isEqualTo(beforeSize);
            }

            @Test
            @DisplayName("Email Blank Exception")
            public void emailBlankExceptionTest() throws Exception {
                JoinDto emailBlankDto = JoinDto.builder()
                        .password("test")
                        .passwordCheck("test")
                        .nickname("testUser2")
                        .build();

                //빈칸의 이메일 가입
                MultiValueMap<String,String> params = convertToMultiValueMap(emailBlankDto);
                assertThrows(ServletException.class,()->mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().isOk()));
            }

            @Test
            @DisplayName("Email Type Not Match Exception")
            public void emailTypeNotMatchExcpetionTest() throws Exception {
                JoinDto emailNotMatchDto = JoinDto.builder()
                        .email("aaaaa")
                        .password("test")
                        .passwordCheck("test")
                        .nickname("testUser2")
                        .build();

                //이메일 타입이 아닌것
                MultiValueMap<String,String> params = convertToMultiValueMap(emailNotMatchDto);
                mvc.perform(post("/auth/join").params(params))
                        .andDo(print())
                        .andExpect(status().is3xxRedirection());
            }
        }
    }



}