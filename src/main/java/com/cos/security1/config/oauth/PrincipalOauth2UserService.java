package com.cos.security1.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    /**
     * OAuth2 구글 로그인을 사용자가 진행할 때
     * 구글로 부터 받은 userRequest 데이터에 대한
     * 후처리(자동 회원가입)가 진행되는 메서드
     *
     * 구글 로그인 클릭 > 구글 로그인창 > 로그인 완료 > code 리턴(OAuth-Client라이브러리가 받음)
     * > AccessToken Request > userRequest 정보 > loadUser() 메서드 호출 > 회원 프로필 받음 from Google
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        return super.loadUser(userRequest);
    }
}
