package com.cos.security1.config.oauth;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private UserRepository userRepository;

    public PrincipalOauth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * OAuth2 구글 로그인을 사용자가 진행할 때
     * 구글로 부터 받은 userRequest 데이터에 대한
     * 후처리(자동 회원가입)가 진행되는 메서드
     *
     * 구글 로그인 클릭 > 구글 로그인창 > 로그인 완료 > code 리턴(OAuth-Client라이브러리가 받음)
     * > AccessToken Request > userRequest 정보 > loadUser() 메서드 호출 > Google한테 회원 프로필 받음
     *
     * 해당 함수가 종료될 때 @AuthenticationPrincipal 어노테이션이 만들어진다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = (String) oAuth2User.getAttributes().get("sub");
        String username = provider + "_" + providerId; // "google_12105646546"
        String password = "무의미 값";
        String email = (String) oAuth2User.getAttributes().get("email");
        String role = "ROLE_USER";

        User userEntity = userRepository.findByUsername(username);

        // 자동 회원가입 + 로그인 아니면 그냥 로그인
        if (userEntity == null) {
            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
