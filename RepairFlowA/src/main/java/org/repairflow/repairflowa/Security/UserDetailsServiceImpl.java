package org.repairflow.repairflowa.Security;



import org.repairflow.repairflowa.Repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var auths = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

//        return userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("User not found with email :" + username ));
        return new UserPrincipal(user.getId(),user.getEmail(),user.getPasswordHash(),auths,user.isActive());
    }
}
