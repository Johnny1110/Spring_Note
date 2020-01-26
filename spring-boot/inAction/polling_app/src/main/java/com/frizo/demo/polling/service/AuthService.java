package com.frizo.demo.polling.service;

import com.frizo.demo.polling.payload.ApiResponse;
import com.frizo.demo.polling.payload.JwtAuthenticationResponse;
import com.frizo.demo.polling.payload.LoginRequest;
import com.frizo.demo.polling.payload.SignUpRequest;

public interface AuthService {
    JwtAuthenticationResponse signin(LoginRequest req);

    ApiResponse signup(SignUpRequest req);
}
