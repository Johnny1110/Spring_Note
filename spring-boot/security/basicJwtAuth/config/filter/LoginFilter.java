package basicJwtAuth.config.security.filter;

import basicJwtAuth.utils.JwtUtil;
import org.json.JSONObject;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginFilter extends AbstractAuthenticationProcessingFilter {
    public LoginFilter(String url, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse resp) throws AuthenticationException, IOException, ServletException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        if (username == null) {
            username = "";
        }
        if (password == null) {
            password = "";
        }
        username = username.trim();
        // 觸發AuthenticationManager
        // 這邊會使用我們自訂的 provider 進行驗證
        // 成功就去呼叫 successfulAuthentication()
        // 失敗就去呼叫 unsuccessfulAuthentication()
        return getAuthenticationManager()
                .authenticate(
                new UsernamePasswordAuthenticationToken(username,password)
        );
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse resp, FilterChain chain, Authentication auth) throws IOException, ServletException {
        // 登入成功，將token透過JwtUtil放到 response 中
        JwtUtil.addAuthentication(resp, auth);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse resp, AuthenticationException failed) throws IOException, ServletException {
        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        JSONObject object = new JSONObject();
        object.append("status",401);
        resp.getOutputStream().println(object.toString());
    }
}
