package com.frizo.learn.oauth.controller;

import com.frizo.learn.oauth.payload.AuthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
public class BaseController {
    @GetMapping("/oauth2/redirect")
    public ResponseEntity<AuthResponse> oauth2Redirect(@RequestParam("token") String token) throws URISyntaxException {
        AuthResponse response = new AuthResponse(token);
        return ResponseEntity.created(URI.create("/index.html")).body(response);
    }
}
