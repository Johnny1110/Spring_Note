package com.frizo.demo.polling.service.impl;

import com.frizo.demo.polling.dao.RoleRepository;
import com.frizo.demo.polling.dao.UserRepository;
import com.frizo.demo.polling.entity.Role;
import com.frizo.demo.polling.entity.RoleName;
import com.frizo.demo.polling.entity.User;
import com.frizo.demo.polling.exception.AppException;
import com.frizo.demo.polling.payload.ApiResponse;
import com.frizo.demo.polling.payload.JwtAuthenticationResponse;
import com.frizo.demo.polling.payload.LoginRequest;
import com.frizo.demo.polling.payload.SignUpRequest;
import com.frizo.demo.polling.security.JwtTokenProvider;
import com.frizo.demo.polling.security.UserPrincipal;
import com.frizo.demo.polling.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public JwtAuthenticationResponse signin(LoginRequest req) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getUsernameOrEmail(),
                        req.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        userRepository.setAuthTokenById(principal.getId(), jwt);
        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public ApiResponse signup(SignUpRequest req) {
        if(userRepository.existsByUsername(req.getUsername())) {
            return new ApiResponse(false, String.format("Username is already taken : %s", req.getUsername()));
        }

        if(userRepository.existsByEmail(req.getEmail())) {
            return new ApiResponse(false, String.format("Email Address already in use : %s", req.getEmail()));
        }

        // 建立 User
        User user = new User(req.getName(), req.getUsername(), req.getEmail(), req.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException(String.format("User Role not set : %s", RoleName.ROLE_USER)));
        user.setRoles(Collections.singleton(userRole));
        user = userRepository.save(user);
        return new ApiResponse(true, "User registered successfully");
    }
}
