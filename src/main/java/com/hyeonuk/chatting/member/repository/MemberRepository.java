package com.hyeonuk.chatting.member.repository;

import com.hyeonuk.chatting.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long>{
    Optional<Member> findById(Long id);
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);
    List<Member> findByNicknameContaining(String nickname);
    Member save(Member member);

    List<Member> findAll();

    void deleteById(Long id);
    void delete(Member member);
}
