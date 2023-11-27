package com.project.bookstudy.member.domain;

import com.project.bookstudy.common.dto.ErrorCode;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "member_id")
    private Long id;
    private String email;
    private String name;
    private String phone;

    @Lob
    private String career;
    private Long point = 0L;
    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private String password;
    private String refreshToken; // 리프레시 토큰

    @Builder
    private Member(String email, String name, String phone, String career) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.career = career;
        this.point = 0L;
        this.status = MemberStatus.ACTIVE;
        this.role = Role.MEMBER;
    }

    public void updateName(String name) {
        if (name == null) return;
        this.name = name;
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }

    public void usePoint(Long point) {
        if (this.point < point) throw new IllegalStateException(ErrorCode.POINT_NOT_ENOUGH.getDescription());
        this.point -= point;
    }

    public void chargePoint(Long point) {
        this.point += point;
    }

}