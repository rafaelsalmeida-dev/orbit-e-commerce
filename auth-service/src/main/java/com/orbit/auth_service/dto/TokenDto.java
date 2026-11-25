package com.orbit.auth_service.dto;

import java.util.Date;
import java.util.Objects;

public class TokenDto {

    private String email;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;

    public TokenDto() {
    }

    public TokenDto(String email, Boolean authenticated, Date created, Date expiration, String accessToken, String refreshToken) {
        this.email = email;
        this.authenticated = authenticated;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TokenDto tokenDto)) return false;
        return Objects.equals(getEmail(), tokenDto.getEmail()) && Objects.equals(getAuthenticated(), tokenDto.getAuthenticated()) && Objects.equals(getCreated(), tokenDto.getCreated()) && Objects.equals(getExpiration(), tokenDto.getExpiration()) && Objects.equals(getAccessToken(), tokenDto.getAccessToken()) && Objects.equals(getRefreshToken(), tokenDto.getRefreshToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getAuthenticated(), getCreated(), getExpiration(), getAccessToken(), getRefreshToken());
    }
}
