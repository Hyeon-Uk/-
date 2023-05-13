package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
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
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
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

    @Nested
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
                int beforeFriends = me.getFriends().size();
                MemberDto myDto = MemberDto.builder()
                        .id(me.getId())
                        .build();

                Member target1 = memberList.get(1);
                MemberDto target1Dto = MemberDto.builder()
                        .id(target1.getId())
                        .build();
                Member target2 = memberList.get(2);
                MemberDto target2Dto = MemberDto.builder()
                        .id(target2.getId())
                        .build();

                //when & then
                memberControlService.addFriend(myDto,target1Dto);
                assertThat(myDto.getFriends().size()).isEqualTo(beforeFriends+1);
                assertThat(me.getFriends().size()).isEqualTo(beforeFriends+1);

                memberControlService.addFriend(myDto,target2Dto);
                assertThat(myDto.getFriends().size()).isEqualTo(beforeFriends+2);
                assertThat(me.getFriends().size()).isEqualTo(beforeFriends+2);
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure {
            @DisplayName("memberId == targetId")
            @Test
            public void selfAddFriendException() {
                //given
                Member me = memberList.get(0);
                int beforeFriends = me.getFriends().size();
                MemberDto myDto = MemberDto.builder()
                        .id(me.getId())
                        .build();

                //when & then
                String message = assertThrows(IllegalArgumentException.class, () -> {
                    memberControlService.addFriend(myDto, myDto);
                }).getMessage();
                assertThat(message).isEqualTo("자기 자신을 친구추가할 수 없습니다.");
                assertThat(me.getFriends().size()).isEqualTo(beforeFriends);
                assertThat(myDto.getFriends().size()).isEqualTo(beforeFriends);
            }

            @DisplayName("same friendship insert")
            @Test
            public void sameFriendshipInsertExceptionTest() {
                //given
                Member me = memberList.get(0);
                int beforeFriends = me.getFriends().size();
                MemberDto myDto = MemberDto.builder()
                        .id(me.getId())
                        .build();

                Member target1 = memberList.get(1);
                MemberDto target1Dto = MemberDto.builder()
                        .id(target1.getId())
                        .build();

                memberControlService.addFriend(myDto,target1Dto);

                //when & then
                String message = assertThrows(AlreadyExistException.class, () -> {
                    memberControlService.addFriend(myDto, target1Dto);
                }).getMessage();
                assertThat(message).isEqualTo("이미 친구입니다.");
                assertThat(me.getFriends().size()).isEqualTo(beforeFriends+1);
                assertThat(myDto.getFriends().size()).isEqualTo(beforeFriends+1);
            }

            @DisplayName("not found user")
            @Test
            public void notFoundExceptionTest() {
                //given
                Member me = memberList.get(0);
                int beforeFriends = me.getFriends().size();
                MemberDto myDto = MemberDto.builder()
                        .id(me.getId())
                        .build();

                MemberDto notExistUser = MemberDto.builder()
                        .id(Long.MAX_VALUE)
                        .build();

                //when & then
                String message = assertThrows(UserNotFoundException.class, () -> {
                    memberControlService.addFriend(myDto, notExistUser);
                }).getMessage();
                assertThat(message).isEqualTo("유저를 찾을 수 없습니다.");
                assertThat(me.getFriends().size()).isEqualTo(beforeFriends);
                assertThat(myDto.getFriends().size()).isEqualTo(beforeFriends);
            }

            @DisplayName("not found my info")
            @Test
            public void notFoundMyInfoExceptionTest(){
                //given
                MemberDto myDto = MemberDto.builder()
                        .id(Long.MAX_VALUE)
                        .build();

                MemberDto notExistUser = MemberDto.builder()
                        .id(memberList.get(1).getId())
                        .build();

                //when & then
                String message = assertThrows(UserNotFoundException.class, () -> {
                    memberControlService.addFriend(myDto, notExistUser);
                }).getMessage();
                assertThat(message).isEqualTo("유저를 찾을 수 없습니다.");
            }
        }
    }
    @Nested
    @DisplayName("find by nickname like")
    class FindByNicknameLike{
        @Nested
        @DisplayName("Success")
        public class Success{
            @Test
            @DisplayName("successful search")
            public void successfullFindTest(){
                //given
                String targetNickname = "test";//모든 유저를 발견해야함
                MemberSearchDto dto = MemberSearchDto.builder()
                        .nickname(targetNickname)
                        .build();

                //when
                List<MemberDto> result = memberControlService.findAllByNickname(dto);

                //then
                assertThat(result.size()).isEqualTo(memberList.size());
            }

            @Test
            @DisplayName("successful search 2")
            public void successfulL2FindTest(){
                //given
                String targetNickname = "1";//1이 포함된 유저를 찾아야함
                MemberSearchDto dto = MemberSearchDto.builder()
                        .nickname(targetNickname)
                        .build();

                List<Member> contains1Members = memberList.stream().filter(e->e.getNickname().contains(targetNickname)).toList();

                //when
                List<MemberDto> result = memberControlService.findAllByNickname(dto);

                //then
                assertThat(result.size()).isEqualTo(result.size());
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure{
            @Test
            @DisplayName("unknown user search")
            public void unknownMemberSearch(){
                //given
                String targetNickname = "unknown";//모든 유저를 발견해야함
                MemberSearchDto dto = MemberSearchDto.builder()
                        .nickname(targetNickname)
                        .build();

                //when
                List<MemberDto> result = memberControlService.findAllByNickname(dto);

                //then
                assertThat(result.size()).isEqualTo(0);
            }
        }
    }

    @Nested
    @DisplayName("Find ById Test")
    public class FindById{
        @Nested
        @DisplayName("success")
        public class Success{
            @Test
            public void findByIdSuccessTest(){
                for(int i=0;i<memberList.size();i++){
                    //given
                    Member member = memberList.get(i);

                    //when
                    MemberDto find = memberControlService.findById(member.getId());

                    //then
                    assertThat(find.getId()).isEqualTo(member.getId());
                    assertThat(find.getNickname()).isEqualTo(member.getNickname());
                    assertThat(find.getEmail()).isEqualTo(member.getEmail());
                    assertThat(find.getFriends().size()).isEqualTo(member.getFriends().size());
                }
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure{
            @Test
            @DisplayName("Not Found Member ById")
            public void findByIdNotFoundUserTest(){
                //given
                Long unknownId = Long.MAX_VALUE;

                //when & then
                String message = assertThrows(UserNotFoundException.class, () -> {
                    memberControlService.findById(unknownId);
                }).getMessage();
                assertThat(message).isEqualTo("해당 유저를 찾을 수 없습니다.");
            }
        }
    }
}
