package com.sosim.server.service;

import com.sosim.server.config.exception.CustomException;
import com.sosim.server.domain.model.User;
import com.sosim.server.repository.UserRepository;
import com.sosim.server.type.ErrorCodeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // security session => Authentication => UserDetails
    // 이 때 @AuthenticationPrincipal 어노테이션이 만들어진다..?
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCodeType.NOT_FOUND_EMAIL));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .build();
    }
}