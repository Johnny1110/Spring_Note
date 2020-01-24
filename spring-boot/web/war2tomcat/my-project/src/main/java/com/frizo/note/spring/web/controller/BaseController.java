package com.frizo.note.spring.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BaseController {
    @GetMapping(value = {"/", "/index"})
    public ModelAndView getIndex(){
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("message", "welcome to my-project");
        return mav;
    }
}
