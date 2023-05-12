package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.entity.MemberSecurity;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class MemberControlServiceIntegrationTest {
    @Autowired
    private MemberControlService memberControlService;

    @Autowired
    private MemberRepository repository;

    List<Member> memberList = new ArrayList<>();

    @BeforeEach
    public void init() {
        for(int i=0;i<50;i++){
            Member member = Member.builder()
                    .email(String.format("test%d@gmail.com",i))
                    .password(String.format("test%d",i))
                    .nickname(String.format("test%d",i))
                    .build();

            MemberSecurity security = MemberSecurity.builder()
                    .salt("salt")
                    .build();

            member.memberSecurityInit(security);
            memberList.add(member);
        }

        repository.saveAll(memberList);
    }

}
