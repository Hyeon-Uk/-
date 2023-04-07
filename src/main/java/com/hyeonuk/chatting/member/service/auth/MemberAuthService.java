package com.hyeonuk.chatting.member.service.auth;

import com.hyeonuk.chatting.member.dto.auth.JoinDto;
import com.hyeonuk.chatting.member.dto.auth.LoginDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;

import java.util.stream.Collectors;

public interface MemberAuthService {
    MemberDto save(JoinDto dto);
    MemberDto login(LoginDto dto);

    default Member joinDtoToEntity(JoinDto dto){

        Member member = Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .nickname(dto.getNickname())
                .memberSecurity(dto.getSecurity())
                .build();
        dto.getSecurity().setMember(member);

        return member;
    }

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
