package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import com.hyeonuk.chatting.member.entity.Member;

import java.util.List;
import java.util.stream.Collectors;

public interface MemberControlService {
    public void addFriend(MemberDto member,MemberDto target);
    public MemberDto findById(Long memberId);
    List<MemberDto> findAllByNickname(MemberSearchDto dto);

    default MemberDto entityToMemeberDto(Member member){
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .friends(member.getFriends().stream().map(friend->{
                    return MemberDto.builder()
                            .email(friend.getEmail())
                            .nickname(friend.getNickname())
                            .id(friend.getId())
                            .build();
                }).collect(Collectors.toList()))
                .build();
    }
}
