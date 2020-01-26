package com.frizo.demo.polling.api;

import com.frizo.demo.polling.payload.ApiResponse;
import com.frizo.demo.polling.payload.JwtAuthenticationResponse;
import com.frizo.demo.polling.payload.LoginRequest;
import com.frizo.demo.polling.payload.SignUpRequest;
import com.frizo.demo.polling.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse response = authService.signin(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        ApiResponse response = authService.signup(signUpRequest);
        if (response.getSuccess()){
            URI location = ServletUriComponentsBuilder
                    .fromCurrentContextPath().path("/api/users/{username}")
                    .buildAndExpand(signUpRequest.getName()).toUri();
            return ResponseEntity.created(location).body(response);
        }else {
            return new ResponseEntity<ApiResponse>(response, HttpStatus.BAD_REQUEST);
        }
    }

}
