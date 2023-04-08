package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.control.UserNotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberControlServiceImplTest {
    @InjectMocks
    private MemberControlServiceImpl memberControlService;

    @Mock
    private MemberRepository repository;

    Member member1, member2, member3, member4;

    @BeforeEach
    public void init() {
        member1 = Member.builder()
                .id(1l)
                .email("test1@gmail.com")
                .password("test1")
                .nickname("test1")
                .build();

        member2 = Member.builder()
                .id(2l)
                .email("test2@gmail.com")
                .password("test2")
                .nickname("test2")
                .build();
        member3 = Member.builder()
                .id(3l)
                .email("test3@gmail.com")
                .password("test3")
                .nickname("test3")
                .build();
        member4 = Member.builder()
                .id(4l)
                .email("test4@gmail.com")
                .password("test4")
                .nickname("test4")
                .build();
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
                Long memberId = 1l;
                Long targetId = 2l;

                MemberDto member = MemberDto.builder()
                        .id(memberId)
                        .build();

                MemberDto target = MemberDto.builder()
                        .id(targetId)
                        .build();

                when(repository.findById(memberId)).thenReturn(Optional.ofNullable(member1));
                when(repository.findById(targetId)).thenReturn(Optional.ofNullable(member2));

                memberControlService.addFriend(member, target);
                verify(repository, times(2)).findById(any());
                verify(repository, times(1)).save(member1);

                assertThat(member.getFriends().size()).isEqualTo(1);
                assertThat(member.getFriends()).contains(target);

                targetId = 3l;
                MemberDto target3 = MemberDto.builder()
                                .id(targetId)
                                        .build();
                when(repository.findById(targetId)).thenReturn(Optional.ofNullable(member3));

                memberControlService.addFriend(member, target3);

                verify(repository, times(2 + 2)).findById(any());
                verify(repository, times(1 + 1)).save(member1);

                assertThat(member.getFriends().size()).isEqualTo(2);
                assertThat(member.getFriends()).contains(target3);
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure {
            @DisplayName("memberId == targetId")
            @Test
            public void selfAddFriendException() {
                Long memberId = 1l;
                Long targetId = 1l;

                MemberDto member = MemberDto.builder()
                        .id(memberId)
                        .build();

                MemberDto target = MemberDto.builder()
                        .id(targetId)
                        .build();

                assertThrows(IllegalArgumentException.class, () -> {
                    memberControlService.addFriend(member, target);
                });
                verify(repository, times(0)).findById(anyLong());
                verify(repository, times(0)).save(any());
            }

            @DisplayName("same friendship insert")
            @Test
            public void sameFriendshipInsertExceptionTest() {
                Long memberId = 1l;
                Long targetId = 2l;

                MemberDto member = MemberDto.builder()
                        .id(memberId)
                        .build();

                MemberDto target = MemberDto.builder()
                        .id(targetId)
                        .build();


                lenient().when(repository.findById(memberId)).thenReturn(Optional.ofNullable(member1));
                lenient().when(repository.findById(targetId)).thenReturn(Optional.ofNullable(member2));

                memberControlService.addFriend(member, target);

                assertThrows(AlreadyExistException.class, () -> {
                    memberControlService.addFriend(member, target);
                });
            }

            @DisplayName("not found user")
            @Test
            public void notFoundExceptionTest() {

                Long memberId = 1l;
                when(repository.findById(memberId)).thenReturn(Optional.ofNullable(member1));
                Long targetId = 2l;
                when(repository.findById(targetId)).thenReturn(Optional.ofNullable(null));

                MemberDto member = MemberDto.builder()
                        .id(memberId)
                        .build();

                MemberDto target = MemberDto.builder()
                        .id(targetId)
                        .build();


                assertThrows(UserNotFoundException.class, () -> {
                    memberControlService.addFriend(member, target);
                });
                verify(repository, times(2)).findById(anyLong());
                verify(repository, times(0)).save(any());
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
            public void successfulFindTest(){
                String one = "1";

                ArrayList<Member> findOneTarget = new ArrayList<>();
                findOneTarget.add(member1);

                when(repository.findByNicknameContaining(one)).thenReturn(findOneTarget);

                List<MemberDto> findOne = memberControlService.findAllByNickname(MemberSearchDto.builder()
                        .nickname(one)
                        .build());

                assertThat(findOne).size().isEqualTo(1);
                assertThat(findOne).contains(memberControlService.entityToMemeberDto(member1));

                String two = "2";

                ArrayList<Member> findTwoTarget = new ArrayList<>();
                findTwoTarget.add(member2);

                when(repository.findByNicknameContaining(two)).thenReturn(findTwoTarget);

                List<MemberDto> findTwo = memberControlService.findAllByNickname(MemberSearchDto.builder()
                        .nickname(two)
                        .build());

                assertThat(findTwo).size().isEqualTo(1);
                assertThat(findTwo).contains(memberControlService.entityToMemeberDto(member2));

                String test = "test";
                ArrayList<Member> findTest = new ArrayList<>();
                findTest.add(member1);
                findTest.add(member2);
                findTest.add(member3);
                findTest.add(member4);

                when(repository.findByNicknameContaining(test)).thenReturn(findTest);

                List<MemberDto> testFind = memberControlService.findAllByNickname(MemberSearchDto.builder()
                        .nickname(test).build());

                assertThat(testFind).size().isEqualTo(4);
                assertThat(testFind).contains(memberControlService.entityToMemeberDto(member1));
                assertThat(testFind).contains(memberControlService.entityToMemeberDto(member2));
                assertThat(testFind).contains(memberControlService.entityToMemeberDto(member3));
                assertThat(testFind).contains(memberControlService.entityToMemeberDto(member4));
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure{
            @Test
            @DisplayName("unknown user search")
            public void unknownMemberSearch(){
                when(repository.findByNicknameContaining(any())).thenReturn(new ArrayList<>());

                List<MemberDto> unknown = memberControlService.findAllByNickname(MemberSearchDto.builder()
                        .nickname("unknown").build());

                assertThat(unknown).size().isEqualTo(0);
            }
        }
    }
}