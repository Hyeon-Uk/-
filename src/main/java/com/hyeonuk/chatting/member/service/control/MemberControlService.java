package com.hyeonuk.chatting.member.service.control;

import com.hyeonuk.chatting.member.dto.MemberDto;

public interface MemberControlService {
    public void addFriend(Long memberId,Long targetId);
}
