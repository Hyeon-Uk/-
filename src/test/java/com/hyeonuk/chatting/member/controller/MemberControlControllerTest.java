package com.hyeonuk.chatting.member.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.control.FriendAddDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import com.hyeonuk.chatting.member.service.control.MemberControlService;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class MemberControlControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MemberRepository repository;

    List<Member> members = new ArrayList<>();

    @BeforeEach
    public void init() {
        Member member1 = Member.builder()
                .email("test1@gmail.com")
                .password("test1")
                .nickname("test1")
                .build();

        Member member2 = Member.builder()
                .email("test2@gmail.com")
                .password("test2")
                .nickname("test2")
                .build();

        Member member3 = Member.builder()
                .email("test3@gmail.com")
                .password("test3")
                .nickname("test3")
                .build();

        Member member4 = Member.builder()
                .email("test4@gmail.com")
                .password("test4")
                .nickname("test4")
                .build();

        Member member5 = Member.builder()
                .email("test5@gmail.com")
                .password("test5")
                .nickname("test5")
                .build();

        members.add(member1);
        members.add(member2);
        members.add(member3);
        members.add(member4);
        members.add(member5);

        repository.saveAllAndFlush(members);
    }

    MultiValueMap<String, String> convertToMultiValueMap(Object obj) {
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        Map<String, String> maps = mapper.convertValue(obj, new TypeReference<Map<String, String>>() {});
        parameters.setAll(maps);

        return parameters;
    }

    @Nested
    @DisplayName("FindMemberByNicknameTest")
    public class FindMemberByNicknameTest {
        MockHttpSession session;
        @Nested
        @DisplayName("success")
        public class Success {

        }

        @Nested
        @DisplayName("failure")
        public class Failure {

        }
    }
}
