package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.control.UserNotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
public class MemberControlServiceIntegrationTest {
    @Autowired
    private MemberControlService memberControlService;

    @Autowired
    private MemberRepository repository;

    List<Member> memberList = new ArrayList<>();

    @BeforeEach
    public void init() {
        for (int i = 0; i < 50; i++) {
            Member member = Member.builder()
                    .email(String.format("test%d@gmail.com", i))
                    .password(String.format("test%d", i))
                    .nickname(String.format("test%d", i))
                    .build();

            MemberSecurity security = MemberSecurity.builder()
                    .salt("salt")
                    .build();

            member.memberSecurityInit(security);
            memberList.add(member);
        }

        repository.saveAll(memberList);
    }

    @DisplayName("add friends Tests")
    public class AddFriendsTest {
        @Nested
        @DisplayName("Success")
        public class SuccessTest {
            @DisplayName("addFriend Success")
            @Test
            public void addFriendSuccessTest() {
                //given
                Member me = memberList.get(0);
                MemberDto dto = MemberDto.builder()
                        .
                Member target1 = memberList.get(1);
                Member target2 = memberList.get(2);

                //when

                memberControlService.addFriend();
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure {
            @DisplayName("memberId == targetId")
            @Test
            public void selfAddFriendException() {

            }

            @DisplayName("same friendship insert")
            @Test
            public void sameFriendshipInsertExceptionTest() {

            }

            @DisplayName("not found user")
            @Test
            public void notFoundExceptionTest() {

            }
        }
    }
}
