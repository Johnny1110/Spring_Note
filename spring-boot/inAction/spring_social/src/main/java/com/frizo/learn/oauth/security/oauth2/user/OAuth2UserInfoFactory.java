package com.frizo.learn.oauth.security.oauth2.user;

import com.frizo.learn.oauth.exception.OAuth2AuthenticationProcessingException;
import com.frizo.learn.oauth.model.AuthProvider;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(AuthProvider authProvider, Map<String, Object> attributes) {
        switch (authProvider){
            case google:
                return new GoogleOAuth2UserInfo(attributes);
            case facebook:
                return new FacebookOAuth2UserInfo(attributes);
            case github:
                return new GithubOAuth2UserInfo(attributes);
            default:
                throw new OAuth2AuthenticationProcessingException("Sorry! Login with " + authProvider + " is not supported yet.");
        }

    }
}
