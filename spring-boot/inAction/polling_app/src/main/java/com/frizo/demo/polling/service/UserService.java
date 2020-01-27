package com.frizo.demo.polling.service;

import com.frizo.demo.polling.entity.User;
import com.frizo.demo.polling.payload.ApiResponse;
import com.frizo.demo.polling.payload.ResetPasswordRequest;

public interface UserService {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User findByUsername(String username);

    ApiResponse resetUserPassword(ResetPasswordRequest req);
}
