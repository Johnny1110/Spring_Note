package basicJwtAuth.config.security;

import basicJwtAuth.config.security.filter.JWTAuthenticationFilter;
import basicJwtAuth.config.security.filter.LoginFilter;

import basicJwtAuth.config.security.provider.CustomAuthenticationProvider;
import basicJwtAuth.service.UserService;
import basicJwtAuth.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    UserService userService(){
        return new UserServiceImpl();
    }

    @Bean
    AuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider();
    }

    // 設置HTTP請求驗證
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .and()
                .authorizeRequests().anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(new LoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
        ;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 使用自定義的驗證
        auth.authenticationProvider(customAuthenticationProvider());
    }
}
