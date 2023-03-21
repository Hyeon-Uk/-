package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.NotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberControlServiceImpl implements MemberControlService{
    private final MemberRepository memberRepository;

    /*
    * 1. targetId가 db에 존재하는지 확인
    * 1-1 . 없으면 NotFoundException throw
    * 2. 있으면 memberId == targetId 인지 확인
    * 2-1 . 같으면 IllegalArgumentException throw
    * 3. 없다면 친구관계 넣어주기
    * */
    @Override
    public void addFriend(Long memberId, Long targetId) {
        if(memberId==targetId){
            throw new IllegalArgumentException("자기 자신을 친구추가할 수 없습니다.");
        }
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));
        Member target = memberRepository.findById(targetId).orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다."));

        member.getFriends().add(target);
        memberRepository.save(member);
    }
}
