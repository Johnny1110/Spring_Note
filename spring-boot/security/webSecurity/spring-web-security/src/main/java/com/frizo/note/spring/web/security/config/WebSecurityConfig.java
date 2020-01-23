package com.frizo.note.spring.web.security.config;

import com.frizo.note.spring.web.security.service.impl.UserLoginServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    UserDetailsService userLoginService(){
        return new UserLoginServiceImpl();
    }

    /**
     * Spring Security 預設必須要用 passwordEncoder，使用 PasswordEncoderFactories 可以根據 db
     * 存有的密碼格式自動判別並進行加密解密。若使用 SHA-256 加密則 db 密碼欄位存碼格式舉例如下:
     * {SHA-256}ce5ca673d13b36118d54a7cf13aeb0ca012383bf771e713421b4d1fd841f539a
     **/
    @Bean
    PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        //return new BCryptPasswordEncoder();
    }

    /**
     * 添加自訂 userDetailsService 與 passwordEncoder
     **/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userLoginService())
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Spring Secutiry 權限核心定義
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated() //所有請求需登入才可訪問。
                .and()
                .formLogin()//使用 web 表單登入
                .loginPage("/login") // 指定登入頁
                .failureUrl("/login?error") // 登入失敗導向此頁
                .permitAll() // "/login" 與 "/login?error" 可任意被訪問。
                .and()
                .rememberMe() // 允許使用勾選記住我
                .tokenValiditySeconds(360000) //360000 秒過期
                .key("my-Key") // token key
                .and()
                .logout()
                .logoutSuccessUrl("/login") // 登出成功導向 "/login"
                .permitAll()// 登出請求可以任意被訪問。
                .and()
                .csrf().disable(); // 停用防跨域攻擊(測試階段停用，不推薦)
    }

    /**
     * 靜態資源訪問設定
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
           .ignoring().antMatchers("/css/**")
           .and()
           .ignoring().antMatchers("/js/**")
           .and()
           .ignoring().antMatchers("/images/**")
           .and()
           .ignoring().antMatchers("/favicon.ico");
    }

}
