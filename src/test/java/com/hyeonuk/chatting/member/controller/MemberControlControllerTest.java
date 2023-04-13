package com.hyeonuk.chatting.member.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyeonuk.chatting.integ.util.ApiUtils;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.control.FriendAddDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class MemberControlControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;

    JoinDto joinDto1,joinDto2,joinDto3,joinDto4,joinDto5;

    MockHttpSession session;

    @BeforeEach
    public void init() throws Exception {
        joinDto1 = JoinDto.builder()
                .email("test1@gmail.com")
                .password("testTEST123!")
                .passwordCheck("testTEST123!")
                .nickname("test1")
                .build();
        joinDto2 = JoinDto.builder()
                .email("test2@gmail.com")
                .password("testTEST123!")
                .passwordCheck("testTEST123!")
                .nickname("test2")
                .build();
        joinDto3 = JoinDto.builder()
                .email("test3@gmail.com")
                .password("testTEST123!")
                .passwordCheck("testTEST123!")
                .nickname("test3")
                .build();
        joinDto4 = JoinDto.builder()
                .email("test4@gmail.com")
                .password("testTEST123!")
                .passwordCheck("testTEST123!")
                .nickname("test4")
                .build();
        joinDto5 = JoinDto.builder()
                .email("test5@gmail.com")
                .password("testTEST123!")
                .passwordCheck("testTEST123!")
                .nickname("test5")
                .build();
        mvc.perform(post("/auth/join").params(convertToMultiValueMap(joinDto1)));
        mvc.perform(post("/auth/join").params(convertToMultiValueMap(joinDto2)));
        mvc.perform(post("/auth/join").params(convertToMultiValueMap(joinDto3)));
        mvc.perform(post("/auth/join").params(convertToMultiValueMap(joinDto4)));
        mvc.perform(post("/auth/join").params(convertToMultiValueMap(joinDto5)));
    }

    MultiValueMap<String, String> convertToMultiValueMap(Object obj) {
        MultiValueMap parameters = new LinkedMultiValueMap<String, String>();
        Map<String, String> maps = mapper.convertValue(obj, new TypeReference<Map<String, String>>() {
        });
        parameters.setAll(maps);

        return parameters;
    }

    @Nested
    @DisplayName("FindMemberByNicknameTest")
    public class FindMemberByNicknameTest {
        @BeforeEach
        public void init() throws Exception{
            session = new MockHttpSession();
            MvcResult loginResult = mvc.perform(post("/auth/login").session(session).params(convertToMultiValueMap(LoginDto.builder()
                            .email(joinDto1.getEmail())
                            .password(joinDto1.getPassword())
                            .build())))
                    .andReturn();
        }
        @Nested
        @DisplayName("success")
        public class Success {
            @Test
            public void findByNicknameSuccess() throws Exception {
                //첫번째 사람의 닉네임 검색
                MemberSearchDto dto = MemberSearchDto.builder()
                        .nickname(joinDto2.getNickname())
                        .build();
                mvc.perform(get("/api/member").session(session)
                                .params(convertToMultiValueMap(dto)))
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.error", nullValue()))
                        .andExpect(jsonPath("$.response.length()", is(1)))
                        .andExpect(jsonPath("$.response[0].email", is(joinDto2.getEmail())));
                //두번째 사람의 닉네임 검색
                dto.setNickname(joinDto3.getNickname());
                mvc.perform(get("/api/member").session(session)
                                .params(convertToMultiValueMap(dto)))
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.error", nullValue()))
                        .andExpect(jsonPath("$.response.length()", is(1)))
                        .andExpect(jsonPath("$.response[0].email", is(joinDto3.getEmail())));
                //모든사람이 포함된 닉네임을 검색
                dto.setNickname("test");
                mvc.perform(get("/api/member").session(session)
                                .params(convertToMultiValueMap(dto)))
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.error", nullValue()))
                        .andExpect(jsonPath("$.response.length()", is(5)));

                //아무도 포함되지않는 닉네임 검색
                dto.setNickname("unknownNickname");
                mvc.perform(get("/api/member").session(session)
                                .params(convertToMultiValueMap(dto)))
                        .andExpect(jsonPath("$.success", is(true)))
                        .andExpect(jsonPath("$.error", nullValue()))
                        .andExpect(jsonPath("$.response.length()", is(0)));
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure {
            @Test
            @DisplayName("nickname blank exception")
            public void nicknameBlankExceptionTest() throws Exception {
                //검색시에 아무것도 입력x
                MemberSearchDto dto = MemberSearchDto.builder()
                        .build();
                mvc.perform(get("/api/member").session(session)
                                .params(convertToMultiValueMap(dto)))
                        .andExpect(jsonPath("$.success", is(false)))
                        .andExpect(jsonPath("$.response", nullValue()))
                        .andExpect(jsonPath("$.error.message", is("검색할 이름을 입력해주세요")))
                        .andExpect(jsonPath("$.error.status", is(HttpStatus.BAD_REQUEST.value())));
            }
        }
    }

    @Nested
    @DisplayName("addFriends Test")
    public class AddFriendsTest{
        ApiUtils.ApiResult<List<MemberDto>> friendsList;
        @BeforeEach
        public void init() throws Exception{
            session = new MockHttpSession();
            MvcResult loginResult = mvc.perform(post("/auth/login").session(session).params(convertToMultiValueMap(LoginDto.builder()
                            .email(joinDto1.getEmail())
                            .password(joinDto1.getPassword())
                            .build())))
                    .andReturn();

            MvcResult findResult = mvc.perform(get("/api/member").session(session)
                    .params(convertToMultiValueMap(
                            MemberSearchDto.builder()
                                    .nickname("test")
                                    .build()
                    ))).andReturn();
            friendsList = mapper.readValue(findResult.getResponse().getContentAsString(), new TypeReference<ApiUtils.ApiResult<List<MemberDto>>>() {});
        }
        @Nested
        @DisplayName("success")
        public class Success{
            @Test
            @DisplayName("add friend success code")
            public void successTest() throws Exception{
                FriendAddDto dto = FriendAddDto.builder()
                                .id(friendsList.getResponse().get(3).getId())
                                        .build();
                mvc.perform(post("/api/member").session(session)
                                .contentType("application/json")
                        .content(mapper.writeValueAsString(dto)))
                        .andExpect(jsonPath("$.success",is(true)))
                        .andExpect(jsonPath("$.error",nullValue()))
                        .andExpect(jsonPath("$.response",is(true)));

                assertThat(((MemberDto) session.getAttribute("member")).getFriends().size()).isEqualTo(1);

                dto = FriendAddDto.builder()
                        .id(friendsList.getResponse().get(4).getId())
                        .build();
                mvc.perform(post("/api/member").session(session)
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(dto)))
                        .andExpect(jsonPath("$.success",is(true)))
                        .andExpect(jsonPath("$.error",nullValue()))
                        .andExpect(jsonPath("$.response",is(true)));
                assertThat(((MemberDto) session.getAttribute("member")).getFriends().size()).isEqualTo(2);
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure{
            @Test
            @DisplayName("self add exception")
            public void selfAddException() throws Exception{
                //자기 자신을 추가하기위해 준비
                FriendAddDto dto = FriendAddDto.builder()
                        .id(friendsList.getResponse().get(0).getId())
                        .build();
                mvc.perform(post("/api/member").session(session)
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(dto)))
                        .andExpect(jsonPath("$.success",is(false)))
                        .andExpect(jsonPath("$.error.message",is("자기 자신을 친구추가할 수 없습니다.")))
                        .andExpect(jsonPath("$.error.status",is(400)))
                        .andExpect(jsonPath("$.response",nullValue()))
                        .andExpect(status().isBadRequest());
            }

            @Test
            @DisplayName("friend duplicated exception")
            public void friendDuplicatedException() throws Exception{
                //자기 자신을 추가하기위해 준비
                FriendAddDto dto = FriendAddDto.builder()
                        .id(friendsList.getResponse().get(1).getId())
                        .build();
                mvc.perform(post("/api/member").session(session)
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(dto)))
                        .andExpect(jsonPath("$.success",is(true)))
                        .andExpect(jsonPath("$.error",nullValue()))
                        .andExpect(jsonPath("$.response",is(true)))
                        .andExpect(status().isOk());
                //중복이면 예외
                mvc.perform(post("/api/member").session(session)
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(dto)))
                        .andExpect(jsonPath("$.success",is(false)))
                        .andExpect(jsonPath("$.error.message",is("이미 친구입니다.")))
                        .andExpect(jsonPath("$.error.status",is(400)))
                        .andExpect(jsonPath("$.response",nullValue()))
                        .andExpect(status().isBadRequest());
            }
        }
    }
}
