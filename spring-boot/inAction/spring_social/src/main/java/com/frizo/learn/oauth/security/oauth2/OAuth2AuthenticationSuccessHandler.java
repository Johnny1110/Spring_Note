package com.frizo.learn.oauth.security.oauth2;

import com.frizo.learn.oauth.config.AppProperties;
import com.frizo.learn.oauth.exception.BadRequestException;
import com.frizo.learn.oauth.security.TokenProvider;
import com.frizo.learn.oauth.utils.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.frizo.learn.oauth.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AppProperties appProperties;
    @Autowired
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(req, resp, auth);
        if (resp.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(req, resp);
        getRedirectStrategy().sendRedirect(req, resp, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest req, HttpServletResponse resp, Authentication auth) {
        Optional<String> redirectUri = CookieUtils.getCookie(req, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);
        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new BadRequestException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        String token = tokenProvider.createToken(auth);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", token)
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        return appProperties.getOauth2().getAuthorizedRedirectUris()
                            .stream()
                            .anyMatch(authorizedRedirectUri ->{
                                System.err.println("authorizedRedirectUri :" + authorizedRedirectUri);
                                URI authorizedURI = URI.create(authorizedRedirectUri);
                                if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost()) &&
                                   authorizedURI.getPort() == clientRedirectUri.getPort()){
                                    return true;
                                }
                                return false;
                            });
    }
}
