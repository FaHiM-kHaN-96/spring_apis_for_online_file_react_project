//package com.example.react_api.security_config;
//
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.nio.charset.StandardCharsets;
//import java.security.Key;
//import java.util.Date;
//import java.util.stream.Collectors;
//
//@Component
//public class JwtUtils {
//
//    private final Key key;
//    private final long validityMs;
//
//    // Inject secret & expiration from application.properties
//    public JwtUtils(
//            @Value("${jwt.secret}") String secret,
//            @Value("${jwt.expiration-ms:86400000}") long validityMs // default 24h
//    ) {
//        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//        this.validityMs = validityMs;
//    }
//
//    // Generate JWT token
//    public String generateToken(Authentication authentication) {
//        String username = authentication.getName();
//
//        String roles = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));
//
//        Date now = new Date();
//        Date expiryDate = new Date(now.getTime() + validityMs);
//
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("roles", roles)
//                .setIssuedAt(now)
//                .setExpiration(expiryDate)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//    }
//
//    // Extract username
//    public String getUsernameFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
//
//    // Validate token against UserDetails
//    public boolean validateToken(String token, UserDetails userDetails) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//
//            String username = claims.getSubject();
//            Date expiration = claims.getExpiration();
//
//            return (username.equals(userDetails.getUsername()) && !expiration.before(new Date()));
//        } catch (Exception e) {
//            return false;
//        }
//    }
//}
