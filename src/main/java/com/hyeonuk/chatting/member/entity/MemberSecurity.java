package com.hyeonuk.chatting.member.entity;

import com.hyeonuk.chatting.integ.entity.BaseEntity;
import com.hyeonuk.chatting.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSecurity extends BaseEntity {
    @Setter
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id",referencedColumnName = "id")
    private Member member;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long member_id;


    @Column(nullable = false)
    private String salt;//salt 해시값을 저장

}
