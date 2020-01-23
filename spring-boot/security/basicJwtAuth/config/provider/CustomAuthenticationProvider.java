package basicJwtAuth.config.security.provider;

import basicJwtAuth.config.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

// 自定義 security 驗證
public class CustomAuthenticationProvider implements AuthenticationProvider {
    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails user = userService.loadUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("User Not Found");
        }
        if(username.equals(user.getUsername()) && password.equals(user.getPassword())){
            // 回傳 Authentication 令牌
            return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        }else {
            throw new BadCredentialsException("Password Error");
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
