package com.refda.bookstore.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // KUNCI RAHASIA: Di real app simpan di env variable.
    // Harus cukup panjang (min 32 karakter) biar aman.
    private final String jwtSecret = "RahasiaNegaraRefdaYangSangatPanjangDanAmanSekali12345";

    // Token berlaku 1 hari (dalam milidetik)
    private final int jwtExpirationMs = 86400000;

    private Key key() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Generate Token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Ambil Email dari Token
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Validasi Token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            // Token invalid, expired, atau dimodifikasi orang iseng
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}