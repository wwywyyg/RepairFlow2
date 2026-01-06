package org.repairflow.repairflowa.Services.JwtServices;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtServices {
    @Value("${app.jwt.secret}") private String secret;
    @Value("${app.jwt.exp-minutes}") private long expMinutes;

    public String generateToken(String email,Long userId,String role) {
        Instant now = Instant.now();
        return JWT.create()
                .withSubject(email)
                .withClaim("uid",userId)
                .withClaim("role",role)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(expMinutes, ChronoUnit.MINUTES)))
                .sign(Algorithm.HMAC256(secret));
    }

    public DecodedJWT verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    public DecodedJWT decodeAndVerify(String token) {return verifyToken(token);}

    public String extractEmail(String token) {return decodeAndVerify(token).getSubject();}

    public Long extractUserId(String token) {return decodeAndVerify(token).getClaim("uid").asLong();}

    public String extractRole(String token) {return decodeAndVerify(token).getClaim("role").asString();}
}
