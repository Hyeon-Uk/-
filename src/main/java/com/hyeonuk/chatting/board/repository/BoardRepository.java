package com.hyeonuk.chatting.board.repository;

import com.hyeonuk.chatting.board.entity.Board;
import com.hyeonuk.chatting.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {
    List<Board> findByMember(Member member);
}
