package com.frizo.learn.oauth.config;

import com.frizo.learn.oauth.security.CustomUserDetailsService;
import com.frizo.learn.oauth.security.RestAuthenticationEntryPoint;
import com.frizo.learn.oauth.security.TokenAuthenticationFilter;
import com.frizo.learn.oauth.security.oauth2.CustomOAuth2UserService;
import com.frizo.learn.oauth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.frizo.learn.oauth.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.frizo.learn.oauth.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;
    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    @Autowired
    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    /*
      Spring OAuth2 預設 使用 HttpSessionOAuth2AuthorizationRequestRepository 來保存認證請求，
       但是我們的應用是 statusless，所以我們不能存 session。我們將把請求以 base64 的方式存進 cookie.
    */
    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 禁用 HTTPSession

                .and()
                .csrf()
                .disable()
                .formLogin()
                .disable()
                .httpBasic()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint()) // 認證錯誤會進入此處，返回 401

                .and()
                .authorizeRequests()
                .antMatchers("/auth/**", "/oauth2/**")
                .permitAll()
                .anyRequest()
                .authenticated()

                .and()
                .oauth2Login()
                .authorizationEndpoint()
                .baseUri("/oauth2/authorize") // 自訂義使用第三方登入時，url 路徑為 {base-url}/oauth2/authorize/{registration-id}
                .authorizationRequestRepository(cookieAuthorizationRequestRepository()) // AuthorizationRequestRepository 負責維護 OAuth2AuthorizationRequest

                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)

                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")

                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

        // 添加自訂義的 Request Header 解析 Filter，解析 Token 使用。
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().antMatchers("/css/**") // 訪問css/** 不擋
                .and()
                .ignoring().antMatchers("/js/**") // 訪問js/** 不擋
                .and()
                .ignoring().antMatchers("/images/**") // 訪問images/** 不擋
                .and()
                .ignoring().antMatchers("/favicon.ico") // 訪問favicon.ico 不擋
                .and()
                .ignoring().antMatchers("/error")
                .and()
                .ignoring().antMatchers("/")
                .and()
                .ignoring().antMatchers("/**.html");
    }
}
