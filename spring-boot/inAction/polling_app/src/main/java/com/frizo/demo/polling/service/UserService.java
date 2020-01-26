package com.frizo.demo.polling.service;

import com.frizo.demo.polling.entity.User;

public interface UserService {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsername(String username);
}
