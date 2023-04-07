package com.hyeonuk.chatting.member.entity;

import com.hyeonuk.chatting.integ.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;

@Entity(name="member")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "email",unique = true,nullable = false)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name= "nickname",unique = true,nullable = false)
    private String nickname;

    @OneToOne(mappedBy = "member",cascade = CascadeType.ALL,orphanRemoval = true)
    MemberSecurity memberSecurity ;

    public void memberSecurityInit(MemberSecurity memberSecurity){
        this.memberSecurity=memberSecurity;
    }

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "friendship"
            ,joinColumns = @JoinColumn(name="member_id")
            ,inverseJoinColumns = @JoinColumn(name="friend_id")
    ,uniqueConstraints = {@UniqueConstraint(columnNames = {"member_id","friend_id"})}//같은 친구를 중복으로 처리하지 못하도록 제약조건 설정
    )
    private List<Member> friends = new ArrayList<>();

    @PrePersist//자기 자신을 추가하지 못하도록 설정
    public void checkSelfFriendship() {
        if (friends.contains(this)) {
            throw new DataIntegrityViolationException("Cannot add self as friend.");
        }
    }
}
