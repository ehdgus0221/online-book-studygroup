package com.project.bookstudy.security.config;

import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.security.service.KakaoOAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityOAuthConfig {

    private final MemberRepository memberRepository;

    @Bean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new DefaultOAuth2UserService();
    }

    @Bean
    KakaoOAuth2MemberService kakaoOAuth2MemberService() {
        return new KakaoOAuth2MemberService(memberRepository, defaultOAuth2UserService());
    }
}