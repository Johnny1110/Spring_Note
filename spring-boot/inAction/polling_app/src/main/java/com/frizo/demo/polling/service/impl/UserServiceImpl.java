package com.frizo.demo.polling.service.impl;

import com.frizo.demo.polling.dao.UserRepository;
import com.frizo.demo.polling.entity.User;
import com.frizo.demo.polling.exception.ResourceNotFoundException;
import com.frizo.demo.polling.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

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
}
