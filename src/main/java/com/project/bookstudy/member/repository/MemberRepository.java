package com.project.bookstudy.member.repository;

import com.project.bookstudy.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface MemberRepository extends JpaRepository<Member, Long> {

    // email을 통해 회원 찾기
    Optional<Member> findByEmail(String email);

    // RefreshToken 정보 찾기
    Optional<Member> findByRefreshToken(String refreshToken);
}
