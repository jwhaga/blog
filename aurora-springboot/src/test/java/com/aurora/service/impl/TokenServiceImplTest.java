package com.aurora.service.impl;

import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.service.RedisService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static com.aurora.constant.AuthConstant.*;
import static com.aurora.constant.RedisConstant.LOGIN_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenServiceImpl 单元测试")
class TokenServiceImplTest {

    private static final String TEST_SECRET = "aurora-dev-jwt-secret-key-change-in-production";
    private static final Integer TEST_USER_ID = 1;
    private static final String TOKEN_PREFIX_VALUE = "Bearer ";
    private static final String TOKEN_HEADER_VALUE = "Authorization";

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Mock
    private RedisService redisService;

    private UserDetailsDTO userDetailsDTO;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", TEST_SECRET);

        userDetailsDTO = UserDetailsDTO.builder()
                .id(TEST_USER_ID)
                .username("admin@163.com")
                .password("encoded-password")
                .nickname("Admin")
                .email("admin@163.com")
                .isDisable(0)
                .loginType(1)
                .build();
    }

    @Nested
    @DisplayName("创建 Token")
    class CreateToken {

        @Test
        @DisplayName("应能根据 userId 创建有效 JWT")
        void shouldCreateTokenWhenGivenUserId() {
            String token = tokenService.createToken(TEST_USER_ID.toString());

            assertNotNull(token);
            assertFalse(token.isEmpty());
            // JWT 格式: header.payload.signature
            assertEquals(3, token.split("\\.").length);
        }

        @Test
        @DisplayName("应能根据 UserDetailsDTO 创建 Token 并刷新 Redis")
        void shouldCreateTokenWhenGivenUserDetails() {
            doReturn(true).when(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));

            String token = tokenService.createToken(userDetailsDTO);

            assertNotNull(token);
            assertFalse(token.isEmpty());
            verify(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));
            assertNotNull(userDetailsDTO.getExpireTime());
        }
    }

    @Nested
    @DisplayName("解析 Token")
    class ParseToken {

        @Test
        @DisplayName("应能正确解析有效 Token 并返回 Claims")
        void shouldParseTokenWhenGivenValidToken() {
            String token = tokenService.createToken(TEST_USER_ID.toString());

            Claims claims = tokenService.parseToken(token);

            assertNotNull(claims);
            assertEquals(TEST_USER_ID.toString(), claims.getSubject());
            assertNotNull(claims.getId());
            assertEquals("huaweimian", claims.getIssuer());
        }

        @Test
        @DisplayName("应能解析包含用户信息的 Token")
        void shouldParseTokenWhenGivenUserDetailsToken() {
            doReturn(true).when(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));
            String token = tokenService.createToken(userDetailsDTO);

            Claims claims = tokenService.parseToken(token);

            assertNotNull(claims);
            assertEquals(TEST_USER_ID.toString(), claims.getSubject());
        }

        @Test
        @DisplayName("无效 Token 应抛出异常")
        void shouldThrowWhenInvalidToken() {
            assertThrows(Exception.class, () -> tokenService.parseToken("invalid.token.here"));
        }

        @Test
        @DisplayName("空 Token 应抛出异常")
        void shouldThrowWhenEmptyToken() {
            assertThrows(Exception.class, () -> tokenService.parseToken(""));
        }
    }

    @Nested
    @DisplayName("刷新 Token")
    class RefreshToken {

        @Test
        @DisplayName("应设置 expireTime 并保存到 Redis")
        void shouldUpdateExpireTimeAndSaveToRedis() {
            doReturn(true).when(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));

            tokenService.refreshToken(userDetailsDTO);

            assertNotNull(userDetailsDTO.getExpireTime());
            verify(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));
        }

        @Test
        @DisplayName("expireTime 应在未来合理范围内")
        void shouldSetExpireTimeInFuture() {
            doReturn(true).when(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));

            tokenService.refreshToken(userDetailsDTO);

            assertNotNull(userDetailsDTO.getExpireTime());
            assertTrue(userDetailsDTO.getExpireTime().isAfter(LocalDateTime.now()));
        }
    }

    @Nested
    @DisplayName("续期 Token")
    class RenewToken {

        @Test
        @DisplayName("距过期 <= CAPTCHA_EXPIRE_MINUTES 时应刷新")
        void shouldRefreshWhenWithinThreshold() {
            userDetailsDTO.setExpireTime(LocalDateTime.now().plusMinutes(TEN_MINUTES));
            doReturn(true).when(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));

            tokenService.renewToken(userDetailsDTO);

            verify(redisService).hSet(eq(LOGIN_USER), eq(TEST_USER_ID.toString()), any(UserDetailsDTO.class), eq((long) TOKEN_EXPIRE_SECONDS));
        }

        @Test
        @DisplayName("距过期 > CAPTCHA_EXPIRE_MINUTES 时不刷新")
        void shouldNotRefreshWhenOutsideThreshold() {
            // 设置过期时间在 30 分钟后（超过 20 分钟阈值）
            userDetailsDTO.setExpireTime(LocalDateTime.now().plusMinutes(THIRTY_MINUTES));

            tokenService.renewToken(userDetailsDTO);

            verify(redisService, never()).hSet(anyString(), anyString(), any(), anyLong());
        }
    }

    @Nested
    @DisplayName("获取当前用户")
    class GetUserDetailDTO {

        @Test
        @DisplayName("应从 Authorization Header 解析 Token 并获取用户")
        void shouldGetUserFromHeader() {
            String token = tokenService.createToken(TEST_USER_ID.toString());
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader(TOKEN_HEADER_VALUE)).thenReturn(TOKEN_PREFIX_VALUE + token);
            when(redisService.hGet(LOGIN_USER, TEST_USER_ID.toString())).thenReturn(userDetailsDTO);

            UserDetailsDTO result = tokenService.getUserDetailDTO(request);

            assertNotNull(result);
            assertEquals(TEST_USER_ID, result.getId());
            verify(redisService).hGet(LOGIN_USER, TEST_USER_ID.toString());
        }

        @Test
        @DisplayName("无 Token 时应返回 null")
        void shouldReturnNullWhenNoToken() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader(TOKEN_HEADER_VALUE)).thenReturn(null);

            UserDetailsDTO result = tokenService.getUserDetailDTO(request);

            assertNull(result);
            verify(redisService, never()).hGet(anyString(), anyString());
        }

        @Test
        @DisplayName("空 Token 时应返回 null")
        void shouldReturnNullWhenEmptyToken() {
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader(TOKEN_HEADER_VALUE)).thenReturn("");

            UserDetailsDTO result = tokenService.getUserDetailDTO(request);

            assertNull(result);
        }
    }

    @Nested
    @DisplayName("删除登录用户")
    class DelLoginUser {

        @Test
        @DisplayName("应从 Redis 中删除用户登录信息")
        void shouldDeleteLoginUser() {
            doNothing().when(redisService).hDel(LOGIN_USER, TEST_USER_ID.toString());

            tokenService.delLoginUser(TEST_USER_ID);

            verify(redisService).hDel(LOGIN_USER, TEST_USER_ID.toString());
        }
    }

    // 测试辅助常量
    private static final long TEN_MINUTES = 10;
    private static final long THIRTY_MINUTES = 30;

}
