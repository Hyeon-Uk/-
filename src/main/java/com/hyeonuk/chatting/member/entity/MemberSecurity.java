package com.hyeonuk.chatting.member.entity;

import com.hyeonuk.chatting.integ.entity.BaseEntity;
import com.hyeonuk.chatting.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSecurity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id",referencedColumnName = "id")
    private Member member;

    @Column(name="tryCount")
    @ColumnDefault(value="0")
    private int tryCount;//로그인 시도 횟수

    private LocalDateTime blockedTime;//로그인 실패시 N분동안 블락

    @Column(nullable = false)
    private String salt;//salt 해시값을 저장

}
