package basicJwtAuth.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.json.JSONObject;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.*;

public class JwtUtil {
    static final long EXPIRATIONTIME = 432_000_000;     // 5天
    static final String HEADER_STRING = "Authorization";// 存放Token的Header Key
    static final Key key = MacProvider.generateKey();    //給定一組密鑰，用來解密以及加密使用

    public static void addAuthentication(HttpServletResponse response, Authentication user) {
        Map<String, Object> auth = new HashMap<>();
        auth.put("authorize", user.getAuthorities());
        String jwts = Jwts.builder()
                .setClaims(auth)
                .setSubject(user.getName()) // setClaims() 一定要在 setSubject() 上面，不然會被覆蓋。
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
                .signWith(key)
                .compact();
        try {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().println(generateJsonResponse(user, jwts));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null){
            try{
                Claims claims = Jwts.parser()
                        .setSigningKey(key)
                        .parseClaimsJws(token)
                        .getBody();
                String user = claims.getSubject();
                List<GrantedAuthority> authorities = generateGrantedAuthorityList(claims.get("authorize"));
                return user != null ? new UsernamePasswordAuthenticationToken(user, null, authorities) : null;
            }catch (JwtException ex){
                System.out.println(ex);
            }
        }
        return null;
    }

    private static List<GrantedAuthority> generateGrantedAuthorityList(Object authorize) {
        String authsStr = (String) ((List<Map>) authorize).get(0).get("authority");
        return AuthorityUtils.commaSeparatedStringToAuthorityList(authsStr);
    }

    private static String generateJsonResponse(Authentication user, String jwts) {
        JSONObject object = new JSONObject();
        object.put("username", user.getName())
              .put("token", jwts)
              .put("authorize", user.getAuthorities());
        return object.toString();
    }

}
