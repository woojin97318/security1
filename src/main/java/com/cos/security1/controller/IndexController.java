package com.cos.security1.controller;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class IndexController {

    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;

    /**
     * 일반 로그인
     */
    @GetMapping("/test/login")
    public @ResponseBody String testLogin(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        /**
         * 방법 1.
         * param -> Authentication authentication
         * authentication.getDetails()을 PrincipalDetails 타입으로 다운캐스팅
         * PrincipalDetails는 UserDetails를 implements를 받았기 때문에 같은 타입이다.
         * code
         * `PrincipalDetails principalDetails = (PrincipalDetails) authentication.getDetails();`
         *
         * 방법 2.
         * param -> @AuthenticationPrincipal PrincipalDetails principalDetails
         * @AuthenticationPrincipal 어노테이션을 사용하여 PrincipalDetails 타입으로 직접 받을 수 있다.
         */
        return "세션 정보 확인";
    }

    /**
     * OAuth2 로그인 인증
     */
    @GetMapping("/test/oauth2/login")
    public @ResponseBody String testOAuth2Login(Authentication authentication,
                                                @AuthenticationPrincipal OAuth2User oAuth2User) {
        /**
         * 방법 1.
         * param -> Authentication authentication
         * authentication.getDetails()을 OAuth2User 타입으로 다운캐스팅
         * code
         * `OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();`
         *
         * 방법 2.
         * param -> @AuthenticationPrincipal OAuth2User oAuth2User
         * @AuthenticationPrincipal 어노테이션을 사용하여 OAuth2User 타입으로 직접 받을 수 있다.
         */
        return "OAuth2 세션 정보 확인";
    }

    @GetMapping
    public String index() {
        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody String user() {
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/login-form")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/join-form")
    public String joinForm() {
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");

        String rawPassword = user.getPassword();
        String encPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user);

        return "redirect:/login-form";
    }

    /**
     * 권한 하나만 건다면 @Secured
     */
    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info() {
        return "개인정보";
    }

    /**
     * 여러 권한을 걸고싶으면 @PreAuthorize
     */
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data() {
        return "데이터 정보";
    }
}
