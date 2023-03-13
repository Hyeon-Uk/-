package com.hyeonuk.chatting.member.service;

import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @InjectMocks
    private MemberAuthService memberAuthService;

    @Mock
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("save test")
    public class SaveTest{
        @Nested
        @DisplayName("save test success")
        public class Success{
            @Test
            public void successTest(){
            }
        }

        @Nested
        @DisplayName("save test failure")
        public class Failure{

        }
    }
}