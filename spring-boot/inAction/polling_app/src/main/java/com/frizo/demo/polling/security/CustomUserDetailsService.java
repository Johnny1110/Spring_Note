package com.frizo.demo.polling.security;

import com.frizo.demo.polling.dao.UserRepository;
import com.frizo.demo.polling.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                                  .orElseThrow(() ->
                                      new UsernameNotFoundException(
                                              String.format("User not found with username or email : %s", usernameOrEmail)
                                      )
                                  );
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UsernameNotFoundException(String.format("User not found with id : %s", id))
        );
        return UserPrincipal.create(user);
    }
}
