package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.NotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    Member member1,member2,member3,member4;

    @BeforeEach
    public void init(){
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
    public class AddFriendsTest{
        @Nested
        @DisplayName("Success")
        public class SuccessTest{

            @DisplayName("addFriend Success")
            @Test
            public void addFriendSuccessTest(){
                Long memberId = 1l;
                Long targetId = 2l;

                when(repository.findById(memberId)).thenReturn(Optional.ofNullable(member1));
                when(repository.findById(targetId)).thenReturn(Optional.ofNullable(member2));

                memberControlService.addFriend(memberId,targetId);
                verify(repository,times(2)).findById(any());
                verify(repository,times(1)).save(member1);

                assertThat(member1.getFriends().size()).isEqualTo(1);
                assertThat(member1.getFriends()).contains(member2);

                targetId = 3l;
                when(repository.findById(targetId)).thenReturn(Optional.ofNullable(member3));

                memberControlService.addFriend(memberId,targetId);

                verify(repository,times(2+2)).findById(any());
                verify(repository,times(1+1)).save(member1);

                assertThat(member1.getFriends().size()).isEqualTo(2);
                assertThat(member1.getFriends()).contains(member3);
            }
        }

        @Nested
        @DisplayName("failure")
        public class Failure{
            @DisplayName("memberId == targetId")
            @Test
            public void selfAddFriendException(){
                Long memberId = 1l;
                Long targetId = 1l;
                assertThrows(IllegalArgumentException.class,()->{
                    memberControlService.addFriend(memberId,targetId);
                });
                verify(repository,times(0)).findById(anyLong());
                verify(repository,times(0)).save(any());
            }

            @DisplayName("same friendship insert")
            @Test
            public void sameFriendshipInsertExceptionTest(){
                Long memberId = 1l;
                Long targetId = 2l;

                lenient().when(repository.findById(memberId)).thenReturn(Optional.ofNullable(member1));
                lenient().when(repository.findById(targetId)).thenReturn(Optional.ofNullable(member2));

                memberControlService.addFriend(memberId,targetId);

                assertThrows(AlreadyExistException.class,()->{
                    memberControlService.addFriend(memberId,targetId);
                });
            }

            @DisplayName("not found user")
            @Test
            public void notFoundExceptionTest(){

                Long memberId = 1l;
                when(repository.findById(memberId)).thenReturn(Optional.ofNullable(member1));
                Long targetId = 2l;
                when(repository.findById(targetId)).thenReturn(Optional.ofNullable(null));

                assertThrows(NotFoundException.class,()->{
                    memberControlService.addFriend(memberId,targetId);
                });
                verify(repository,times(2)).findById(anyLong());
                verify(repository,times(0)).save(any());
            }
        }
    }
}