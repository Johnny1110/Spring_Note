package com.frizo.note.spring.web.security.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaseController {
    @GetMapping("/login")
    public String getLoginPage(){
        return "login";
    }

    @GetMapping(value = {"/", "/index"})
    public ModelAndView getIndex(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView mav = new ModelAndView();
        mav.setViewName("index");
        mav.addObject("componentName", "/components/welcome.html");
        mav.addObject("name", auth.getName());
        mav.addObject("auths", auth.getAuthorities());
        return mav;
    }
}
