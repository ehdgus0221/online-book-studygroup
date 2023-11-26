package com.project.bookstudy.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.bookstudy.member.repository.MemberRepository;
import com.project.bookstudy.security.filter.ExceptionHandlerFilter;
import com.project.bookstudy.security.filter.JwtAuthenticationProcessingFilter;
import com.project.bookstudy.security.filter.handler.ApiAccessDeniedHandler;
import com.project.bookstudy.security.filter.handler.ApiAuthenticationEntryPoint;
import com.project.bookstudy.security.filter.handler.OAuth2LoginFailureHandler;
import com.project.bookstudy.security.filter.handler.OAuth2LoginSuccessHandler;
import com.project.bookstudy.security.service.JwtTokenService;
import com.project.bookstudy.security.service.KakaoOAuth2MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final KakaoOAuth2MemberService kakaoOAuth2MemberService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final ObjectMapper objectMapper;
    private final JwtTokenService jwtTokenProvider;
    private final MemberRepository memberRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .headers().frameOptions().disable();

        http.authorizeRequests()
                .mvcMatchers("/test").authenticated()
                .anyRequest().permitAll();

        http.oauth2Login()
                .userInfoEndpoint().userService(kakaoOAuth2MemberService)
                .and()
                .successHandler(oAuth2LoginSuccessHandler)
                .failureHandler(oAuth2LoginFailureHandler);

        // 필터 순서를 설정하여 정상작동 및 Filter에서 예외처리 진행
        http
                .addFilterBefore(new JwtAuthenticationProcessingFilter(jwtTokenProvider, memberRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(objectMapper), JwtAuthenticationProcessingFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(new ApiAuthenticationEntryPoint(objectMapper)) //AuthenticationException
                .accessDeniedHandler(new ApiAccessDeniedHandler(objectMapper));     //AccessDeniedException

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(PathRequest.toH2Console());
    }
}