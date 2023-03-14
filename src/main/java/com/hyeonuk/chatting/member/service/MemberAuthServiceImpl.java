package com.hyeonuk.chatting.member.service;

import com.hyeonuk.chatting.member.dto.JoinDto;
import com.hyeonuk.chatting.member.dto.LoginDto;
import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.NotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MemberAuthServiceImpl implements MemberAuthService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
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

        //가입 해시 셋팅

        //이메일 발송
        
        
        return this.entityToMemeberDto(memberRepository.save(this.joinDtoToEntity(dto)));
    }

    /*
    *
    * 로그인 시에 userCheck부분이 false면 throw exception
    *
    * 이메일이 존재하지 않거나 패스워드 불일치시 throw exception
    * */
    @Override
    public MemberDto login(LoginDto dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(()-> new NotFoundException("해당하는 유저가 존재하지 않습니다."));

        if(!member.getPassword().equals(dto.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if(!member.getUserCheck()){
            throw new IllegalArgumentException("이메일 체크를 해주세요");
        }

        return entityToMemeberDto(member);
    }
}
