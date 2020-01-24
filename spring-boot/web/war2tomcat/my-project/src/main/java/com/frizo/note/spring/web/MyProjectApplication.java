package com.frizo.note.spring.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Main class 繼承 SpringBootServletInitializer
 * SpringBootServletInitializer 又繼承了 WebApplicationInitializer
 * WebApplicationInitializer 是 Spring 3.1 新增介面，實作此介面的類別，不需額外宣告，會自動被 SpringServletContainerInitializer 偵測、自動呼叫註冊所需的相關方法
 * 注意!!! : Servlet 3.0 以上才支援XD；Servlet 2.5 之前的版本還是只能透過 web.xml 方式註冊 ApplicationContext 及 DispatcherServlet
 **/
@SpringBootApplication
public class MyProjectApplication extends SpringBootServletInitializer {
    // 要想包成 war 部屬到 tomcat 上，就必須如此複寫configure方法。
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MyProjectApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);
    }
}
