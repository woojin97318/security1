package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Spring Security Filter가 스프링 필터체인에 등록이 된다.
public class SecurityConfig {

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
                .defaultSuccessUrl("/");

        return httpSecurity.build();
    }
}
