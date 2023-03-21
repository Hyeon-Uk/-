package com.hyeonuk.chatting.member.entity;

import com.hyeonuk.chatting.integ.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "friendship",
            joinColumns = @JoinColumn(name="member_id"),
            inverseJoinColumns = @JoinColumn(name="friend_id"))
    private List<Member> friends = new ArrayList<>();
}
