package com.hyeonuk.chatting.member.repository;

import com.hyeonuk.chatting.member.entity.MemberSecurity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberSecurityRepository extends JpaRepository<MemberSecurity,Long> {
}
