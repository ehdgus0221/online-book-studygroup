package com.project.bookstudy.security.service;

import com.project.bookstudy.member.domain.Member;
import com.project.bookstudy.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class KakaoOAuth2MemberService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final DefaultOAuth2UserService userService;  //상속 대신 합성 사용

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        //기존 DefaultOAuth2UserService 로직
        OAuth2User oAuth2User = userService.loadUser(userRequest);

        //Save or Update at DB
        Map<String, Object> memberInfo = oAuth2User.getAttribute("kakao_account");
        String email = (String) memberInfo.get("email");
        String name = (String) ((Map) memberInfo.get("profile")).get("nickname");

        log.info("email : " + email);
        log.info("name : " + name);

        saveOrUpdate(email, name);

        return oAuth2User;
    }

    private void saveOrUpdate(String email, String name) {

        Optional<Member> memberOptional = memberRepository.findByEmail(email);

        memberOptional.ifPresentOrElse(
                member -> {if (member.getName().equals(name)) {member.updateName(name);}}
                , () -> memberRepository.save(Member.builder().email(email).name(name).build()));
    }
}