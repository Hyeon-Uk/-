package com.hyeonuk.chatting.member.repository;

import com.hyeonuk.chatting.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,Long> {
}
