package com.cos.security1.config.auth;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Security 설정에서 `loginProcessingUrl("/login")`가 있다.
 * login 요청이 오면 자동으로 UserDetailsService 타입으로 loc 되어 있는 loadUserByUsername 함수가 샐행된다.
 */
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 일반 로그인 후 처리
     * Security session(내부 Authentication(내부 UserDetails))
     *
     * 해당 함수가 종료될 때 @AuthenticationPrincipal 어노테이션이 만들어진다.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return new PrincipalDetails(user);
        }
        return null;
    }
}
