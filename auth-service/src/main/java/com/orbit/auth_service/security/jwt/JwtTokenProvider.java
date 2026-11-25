package com.orbit.auth_service.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.orbit.auth_service.dto.TokenDto;
import com.orbit.auth_service.exception.InvalidJwtAuthenticationException;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {

    @Value("${security.jwt.token.secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:900000}")
    private long expireLength;

    @Value("${security.jwt.token.refresh-expire-length:604800000}")
    private long refreshExpireLength;

    @Value("${security.jwt.token.issuer}")
    private String issuer;


    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private Algorithm algorithm = null;

    @PostConstruct
    protected void init() {

        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        algorithm = Algorithm.HMAC256(keyBytes);

    }

    public TokenDto createAccessToken(String email, List<String> roles) {

        Date now = new Date();
        Date validity = new Date(now.getTime() + expireLength);

        String accessToken = getAccessToken(email, roles, now, validity);
        String refreshToken = getRefreshToken(email, now);
        return new TokenDto(email, true, now, validity, accessToken, refreshToken);

    }

    public Authentication authentication(String token) {

        DecodedJWT decodedJWT = decodedAccessToken(token);

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(
                decodedJWT.getSubject()
        );

        List<SimpleGrantedAuthority> authorities =
                decodedJWT.getClaim("roles")
                        .asList(String.class)
                        .stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);

    }

    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if(containsBearer(bearerToken)) {

            return bearerToken.substring("Bearer ".length());

        } else {

            throw new InvalidJwtAuthenticationException("Invalid JWT token");

        }

    }

    public boolean validateAccessToken(String token) {

        try {
            decodedAccessToken(token);
            return true;
        } catch (JWTVerificationException e) {
            throw new InvalidJwtAuthenticationException(
                    "Expired or invalid JWT token."
            );
        }
    }

    public boolean validateRefreshToken(String token) {

        try {
            decodedRefreshToken(token);
            return true;
        } catch (JWTVerificationException e) {
            throw new InvalidJwtAuthenticationException(
                    "Expired or invalid refresh token."
            );
        }
    }

    private DecodedJWT decodedAccessToken(String token) {
        JWTVerifier verifier =  JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaim("token_type", "access")
                .build();

        return verifier.verify(token);
    }

    private void decodedRefreshToken(String token) {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .withClaim("token_type", "refresh")
                .build();

        verifier.verify(token);
    }

    private String getRefreshToken(String email, Date now) {

        Date refreshTokenValidity = new Date(now.getTime() + refreshExpireLength);
        return JWT.create()
                .withIssuedAt(now)
                .withExpiresAt(refreshTokenValidity)
                .withSubject(email)
                .withIssuer(issuer)
                .withClaim("token_type", "refresh")
                .sign(algorithm);
    }

    private String getAccessToken(String email, List<String> roles, Date now, Date validity) {

        return JWT.create()
                .withClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(validity)
                .withSubject(email)
                .withIssuer(issuer)
                .withClaim("token_type", "access")
                .sign(algorithm);

    }

    private static boolean containsBearer(String bearerToken) {
        return StringUtils.isNotEmpty(bearerToken) && bearerToken.startsWith("Bearer ");
    }
}
