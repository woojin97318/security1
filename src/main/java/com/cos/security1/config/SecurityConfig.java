package com.cos.security1.config;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
// Spring Security Filter가 스프링 필터체인에 등록이 된다.
@EnableWebSecurity
// @Secured 활성화, @PreAuthorize & @PosteAuthorize(잘 안씀) 어노테이션 활성화
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    PrincipalOauth2UserService principalOauth2UserService;

    @Bean // 해당 메서드의 리턴되는 obj를 loc로 등록해줌
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // token을 사용하는 방식이기 때문에 csrf disable
        httpSecurity.csrf().disable();

        httpSecurity.authorizeRequests()
                // 인증이 필요
                .antMatchers("/user/**").authenticated()
                // 인증 & 권한 필요
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                // other request는 전부 허용
                .anyRequest().permitAll()
                .and()
                // 권한이 없는 페이지는 login 페이지로 이동
                .formLogin()
                .loginPage("/login-form")
                // "/login" 주소가 호출이 되면 security가 낚아채서 대신 로그인을 잰행
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")
                .and()
                // Oauth2 로그인 또한 같은 "/login-form"으로 설정
                .oauth2Login()
                .loginPage("/login-form")
                /**
                 * 구글 로그인이 완료된 뒤에 후처리가 필요.
                 * 1. 코드 받기(인증)
                 * 2. 액세스토큰(권한)
                 * 3. 사용자 프로필 정보 수집
                 * 4-1. 정보를 토대로 자동 회원가입
                 * 4-2. 이메일, 전화번호, 이름, ID / 쇼핑몰 -> 주소 / 백화점 -> 고객 등급 필요
                 *
                 * Tip. 구글 로그인 -> 코드x, 액세스토큰 + 사용자 프로필 정보 수집
                 */
                .userInfoEndpoint()
                .userService(principalOauth2UserService);

        return httpSecurity.build();
    }
}
