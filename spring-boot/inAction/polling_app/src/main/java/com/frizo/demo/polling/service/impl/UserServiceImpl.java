package com.frizo.demo.polling.service.impl;

import com.frizo.demo.polling.dao.UserRepository;
import com.frizo.demo.polling.entity.User;
import com.frizo.demo.polling.exception.AppException;
import com.frizo.demo.polling.exception.ResourceNotFoundException;
import com.frizo.demo.polling.payload.ApiResponse;
import com.frizo.demo.polling.payload.ResetPasswordRequest;
import com.frizo.demo.polling.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()->
                new ResourceNotFoundException("User", "username", username)
        );
    }

    @Override
    @Transactional
    public ApiResponse resetUserPassword(ResetPasswordRequest req) {
        User user = userRepository.findByUsernameOrEmail(req.getUsernameOrEmail(), req.getUsernameOrEmail()).orElseThrow(
                () -> new AppException("Something wrong with the server, please contect to server admin.")
        );

        if (passwordEncoder.matches(req.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(req.getNewPassword()));
            user.setAuthToken("");
            userRepository.save(user);
            return new ApiResponse(true, "Password reset successfully!");
        }else{
            return new ApiResponse(false, "Password reset faild, because old password is not correct.");
        }
    }
}
