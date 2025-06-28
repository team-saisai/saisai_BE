package com.saisai.config.jwt;

import static com.saisai.domain.common.exception.ExceptionCode.USER_NOT_FOUND;

import com.saisai.domain.common.exception.CustomException;
import com.saisai.domain.user.entity.User;
import com.saisai.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User findUser = userRepository.findByEmail(username)
            .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        return new org.springframework.security.core.userdetails.User(findUser.getEmail(), findUser.getPassword(),
            AuthUserDetails.from(findUser).getAuthorities());
    }
}
