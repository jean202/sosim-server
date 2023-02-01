package com.sosim.server.service;

/* 수정 요망
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // security session => Authentication => UserDetails
    // 이 때 @AuthenticationPrincipal 어노테이션이 만들어진다..?
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);
        if(userEntity != null) {
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}

 */