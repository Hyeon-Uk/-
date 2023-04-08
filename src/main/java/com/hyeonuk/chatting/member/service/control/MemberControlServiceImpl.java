package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;
import com.hyeonuk.chatting.member.dto.control.MemberSearchDto;
import com.hyeonuk.chatting.member.entity.Member;
import com.hyeonuk.chatting.member.exception.auth.join.AlreadyExistException;
import com.hyeonuk.chatting.member.exception.control.UserNotFoundException;
import com.hyeonuk.chatting.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberControlServiceImpl implements MemberControlService{
    private final MemberRepository memberRepository;

    /*
    * 1. targetId가 db에 존재하는지 확인
    * 1-1 . 없으면 NotFoundException throw
    * 2. 있으면 memberId == targetId 인지 확인
    * 2-1 . 같으면 IllegalArgumentException throw
    * 3. 이미 친구라면 AlreadyExistException throw
    * */
    @Override
    public void addFriend(MemberDto member, MemberDto target) {
        Long memberId = member.getId();
        Long targetId = target.getId();
        if(memberId==targetId){
            throw new IllegalArgumentException("자기 자신을 친구추가할 수 없습니다.");
        }
        Member memberEntity = memberRepository.findById(memberId).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        Member targetEntity = memberRepository.findById(targetId).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        if(memberEntity.getFriends().contains(targetEntity)){
            throw new AlreadyExistException("이미 친구입니다.");
        }

        member.getFriends().add(target);
        memberEntity.getFriends().add(targetEntity);
        memberRepository.save(memberEntity);
    }

    @Override
    public MemberDto findById(Long memberId) {
        return entityToMemeberDto(memberRepository.findById(memberId).orElseThrow(()->new UserNotFoundException("해당 유저를 찾을 수 없습니다.")));
    }

    @Override
    public List<MemberDto> findAllByNickname(MemberSearchDto dto) {
        return memberRepository.findByNicknameContaining(dto.getNickname()).stream().map(this::entityToMemeberDto).collect(Collectors.toList());
    }
}
