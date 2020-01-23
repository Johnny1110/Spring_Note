package com.frizo.note.spring.web.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class WebSecurityApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(WebSecurityApplication.class, args);
    }

    // 要想包成 war 部屬到 tomcat 上，就必須複寫 SpringBootServletInitializer 的此方法
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(WebSecurityApplication.class);
    }
}
