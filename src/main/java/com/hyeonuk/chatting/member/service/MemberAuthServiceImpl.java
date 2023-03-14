package com.hyeonuk.chatting.member.service;

import com.hyeonuk.chatting.member.dto.JoinDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.exception.AlreadyExistException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberRepository memberRepository;

    @Override
    public MemberDto save(JoinDto dto) {
        if(!dto.getPassword().equals(dto.getPasswordCheck())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        //이메일 검증
        memberRepository.findByEmail(dto.getEmail()).ifPresent(member -> {
            throw new AlreadyExistException(member.getEmail()+"은 이미 존재하는 이메일입니다.");
        });

        //닉네임 검증
        memberRepository.findByNickname(dto.getNickname()).ifPresent(member -> {
            throw new AlreadyExistException(member.getNickname()+"은 이미 존재하는 닉네임입니다.");
        });

        //비밀번호 인코딩 구현

        return this.entityToMemeberDto(memberRepository.save(this.joinDtoToEntity(dto)));
    }
}
