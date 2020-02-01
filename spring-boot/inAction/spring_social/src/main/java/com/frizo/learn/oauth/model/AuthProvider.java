package com.frizo.learn.oauth.model;

public enum  AuthProvider {
    local("local"),
    facebook("facebook"),
    google("google"),
    github("github");

    private String name;

    AuthProvider(String authProvider) {
        this.name = authProvider;
    }

    @Override
    public String toString(){
        return this.name;
    }
}