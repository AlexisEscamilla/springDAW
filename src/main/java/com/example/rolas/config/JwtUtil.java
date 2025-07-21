package com.example.rolas.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


public class JwtUtil {
	private static final String SECRET_KEY = "cdcc32679d61b9e095af3cfd3fa6eb955244a1b186ddc576349e861ed9d75265"; // tu clave base64

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)), SignatureAlgorithm.HS256)
                .compact();
    }
}

