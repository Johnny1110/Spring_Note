package com.frizo.learn.oauth.controller;

import net.minidev.json.JSONObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class SimpleController {
    @GetMapping(value = {"/hello", "/"})
    public String hello(Principal principal){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("authName", authentication.getName());
        jsonObject.put("principal", authentication.getPrincipal().toString());

        return jsonObject.toJSONString();
    }
}
