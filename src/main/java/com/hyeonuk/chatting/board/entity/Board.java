package com.hyeonuk.chatting.board.entity;

import com.hyeonuk.chatting.integ.entity.BaseEntity;
import com.hyeonuk.chatting.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title",nullable = false)
    private String title;

    @Column(name="content",nullable = false)
    private String content;

    @Column(name="hit")
    private int hit;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name="member_id")
    private Member member;

    public void updateHit(){
        this.hit++;
    }
}
