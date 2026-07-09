package com.aurora.service.impl;

import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.service.RedisService;
import com.aurora.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.aurora.constant.AuthConstant.*;
import static com.aurora.constant.RedisConstant.LOGIN_USER;


@Service
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private RedisService redisService;

    @Override
    public String createToken(UserDetailsDTO userDetailsDTO) {
        refreshToken(userDetailsDTO);
        String userId = userDetailsDTO.getId().toString();
        return createToken(userId);
    }

    @Override
    public String createToken(String subject) {
        SecretKey secretKey = generalKey();
        return Jwts.builder().id(getUuid()).subject(subject)
                .issuer("huaweimian")
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public void refreshToken(UserDetailsDTO userDetailsDTO) {
        LocalDateTime currentTime = LocalDateTime.now();
        userDetailsDTO.setExpireTime(currentTime.plusSeconds(TOKEN_EXPIRE_SECONDS));
        String userId = userDetailsDTO.getId().toString();
        redisService.hSet(LOGIN_USER, userId, userDetailsDTO, TOKEN_EXPIRE_SECONDS);
    }

    @Override
    public void renewToken(UserDetailsDTO userDetailsDTO) {
        LocalDateTime expireTime = userDetailsDTO.getExpireTime();
        LocalDateTime currentTime = LocalDateTime.now();
        if (Duration.between(currentTime, expireTime).toMinutes() <= CAPTCHA_EXPIRE_MINUTES) {
            refreshToken(userDetailsDTO);
        }
    }

    @Override
    public Claims parseToken(String token) {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public UserDetailsDTO getUserDetailDTO(HttpServletRequest request) {
        String token = Optional.ofNullable(request.getHeader(TOKEN_HEADER)).orElse("").replaceFirst(TOKEN_PREFIX, "");
        if (StringUtils.hasText(token) && !token.equals("null")) {
            Claims claims = parseToken(token);
            String userId = claims.getSubject();
            return (UserDetailsDTO) redisService.hGet(LOGIN_USER, userId);
        }
        return null;
    }

    @Override
    public void delLoginUser(Integer userId) {
        redisService.hDel(LOGIN_USER, String.valueOf(userId));
    }

    public String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public SecretKey generalKey() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedKey = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(encodedKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
