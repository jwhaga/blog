package com.aurora.service.impl;

import com.alibaba.fastjson.JSON;
import com.aurora.constant.CommonConstant;
import com.aurora.entity.UserAuth;
import com.aurora.entity.UserInfo;
import com.aurora.entity.UserRole;
import com.aurora.exception.BizException;
import com.aurora.mapper.UserAuthMapper;
import com.aurora.mapper.UserInfoMapper;
import com.aurora.mapper.UserRoleMapper;
import com.aurora.model.dto.EmailDTO;
import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.model.dto.UserLogoutStatusDTO;
import com.aurora.model.dto.WebsiteConfigDTO;
import com.aurora.model.vo.PasswordVO;
import com.aurora.model.vo.UserVO;
import com.aurora.service.AuroraInfoService;
import com.aurora.service.RedisService;
import com.aurora.service.TokenService;
import com.aurora.strategy.context.SocialLoginStrategyContext;
import com.aurora.util.UserUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Objects;

import static com.aurora.constant.RabbitMQConstant.EMAIL_EXCHANGE;
import static com.aurora.constant.RedisConstant.CODE_EXPIRE_TIME;
import static com.aurora.constant.RedisConstant.USER_CODE_KEY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserAuthServiceImpl 单元测试")
class UserAuthServiceImplTest {

    @InjectMocks
    private UserAuthServiceImpl userAuthService;

    @Mock
    private UserAuthMapper userAuthMapper;
    @Mock
    private UserInfoMapper userInfoMapper;
    @Mock
    private UserRoleMapper userRoleMapper;
    @Mock
    private RedisService redisService;
    @Mock
    private AuroraInfoService auroraInfoService;
    @Mock
    private TokenService tokenService;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private SocialLoginStrategyContext socialLoginStrategyContext;

    @Captor
    private ArgumentCaptor<Message> messageCaptor;

    private UserVO validUserVO;
    private UserVO invalidEmailUserVO;

    private MockedConstruction<LambdaQueryWrapper> queryWrapperConstruction;
    private MockedConstruction<LambdaUpdateWrapper> updateWrapperConstruction;
    @BeforeEach
    void setUp() {
        validUserVO = new UserVO();
        validUserVO.setUsername("test@example.com");
        validUserVO.setPassword("password123");
        validUserVO.setCode("123456");

        invalidEmailUserVO = new UserVO();
        invalidEmailUserVO.setUsername("invalid-email");
        invalidEmailUserVO.setPassword("password123");
        invalidEmailUserVO.setCode("123456");
        
        lenient().when(auroraInfoService.getWebsiteConfig()).thenReturn(new WebsiteConfigDTO());

        // Mock LambdaQueryWrapper construction to avoid MyBatis-Plus lambda cache init
        queryWrapperConstruction = mockConstruction(LambdaQueryWrapper.class,
            (mock, context) -> {
                lenient().when(mock.select(any(SFunction.class))).thenReturn(mock);
                lenient().when(mock.eq(any(), any())).thenReturn(mock);
            });
        updateWrapperConstruction = mockConstruction(LambdaUpdateWrapper.class,
            (mock, context) -> {
                lenient().when(mock.set(any(), any())).thenReturn(mock);
                lenient().when(mock.eq(any(), any())).thenReturn(mock);
            });
    }

    @AfterEach
    void tearDown() {
        if (queryWrapperConstruction != null) queryWrapperConstruction.close();
        if (updateWrapperConstruction != null) updateWrapperConstruction.close();
    }

    @Nested
    @DisplayName("发送验证码")
    class SendCode {

        @Test
        @DisplayName("有效邮箱应发送验证码到 MQ 并保存到 Redis")
        void shouldSendCodeWhenValidEmail() {
            doNothing().when(redisService).set(eq(USER_CODE_KEY + "test@example.com"), anyString(), eq(CODE_EXPIRE_TIME));
            doNothing().when(rabbitTemplate).convertAndSend(eq(EMAIL_EXCHANGE), eq("*"), any(Message.class));

            userAuthService.sendCode("test@example.com");

            verify(rabbitTemplate).convertAndSend(eq(EMAIL_EXCHANGE), eq("*"), messageCaptor.capture());
            verify(redisService).set(eq(USER_CODE_KEY + "test@example.com"), anyString(), eq(CODE_EXPIRE_TIME));

            Message sentMessage = messageCaptor.getValue();
            assertNotNull(sentMessage);
            // 验证消息体是 EmailDTO 的 JSON
            String bodyJson = new String(sentMessage.getBody());
            assertTrue(bodyJson.contains("test@example.com"));
            assertTrue(bodyJson.contains("验证码"));
        }

        @Test
        @DisplayName("无效邮箱应抛出 BizException")
        void shouldThrowWhenInvalidEmail() {
            assertThrows(BizException.class, () -> userAuthService.sendCode("invalid-email"));
            verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Message.class));
            verify(redisService, never()).set(anyString(), any(), anyLong());
        }
    }

    @Nested
    @DisplayName("用户注册")
    class Register {

        @Test
        @DisplayName("应成功注册新用户")
        void shouldRegisterSuccessfully() {
            when(redisService.get(USER_CODE_KEY + "test@example.com")).thenReturn("123456");
            when(userAuthMapper.selectOne(any())).thenReturn(null);
            when(userInfoMapper.insert((UserInfo) any())).thenReturn(1);
            when(userRoleMapper.insert((UserRole) any())).thenReturn(1);
            when(userAuthMapper.insert((UserAuth) any())).thenReturn(1);

            userAuthService.register(validUserVO);

            verify(userInfoMapper).insert((UserInfo) any());
            verify(userRoleMapper).insert((UserRole) any());
            verify(userAuthMapper).insert((UserAuth) any());
        }

        @Test
        @DisplayName("无效邮箱应抛出 BizException")
        void shouldThrowWhenInvalidEmail() {
            assertThrows(BizException.class, () -> userAuthService.register(invalidEmailUserVO));
            verify(userAuthMapper, never()).insert((UserAuth) any());
        }

        @Test
        @DisplayName("验证码错误应抛出 BizException")
        void shouldThrowWhenWrongCode() {
            when(redisService.get(USER_CODE_KEY + "test@example.com")).thenReturn("wrong-code");
            // selectOne 不会被调用，因为 code 检查先失败
            assertThrows(BizException.class, () -> userAuthService.register(validUserVO));
            verify(userAuthMapper, never()).insert((UserAuth) any());
        }

        @Test
        @DisplayName("重复邮箱应抛出 BizException")
        void shouldThrowWhenDuplicateEmail() {
            when(redisService.get(USER_CODE_KEY + "test@example.com")).thenReturn("123456");
            UserAuth existingUser = new UserAuth();
            existingUser.setUsername("test@example.com");
            when(userAuthMapper.selectOne(any())).thenReturn(existingUser);

            assertThrows(BizException.class, () -> userAuthService.register(validUserVO));
            verify(userAuthMapper, never()).insert((UserAuth) any());
        }

        @Test
        @DisplayName("密码应使用 BCrypt 加密存储")
        void shouldEncryptPassword() {
            when(redisService.get(USER_CODE_KEY + "test@example.com")).thenReturn("123456");
            when(userAuthMapper.selectOne(any())).thenReturn(null);
            when(userInfoMapper.insert((UserInfo) any())).thenReturn(1);
            when(userRoleMapper.insert((UserRole) any())).thenReturn(1);
            when(userAuthMapper.insert((UserAuth) any())).thenReturn(1);

            userAuthService.register(validUserVO);

            ArgumentCaptor<UserAuth> authCaptor = ArgumentCaptor.forClass(UserAuth.class);
            verify(userAuthMapper).insert((UserAuth) authCaptor.capture());
            UserAuth savedAuth = authCaptor.getValue();
            assertNotNull(savedAuth.getPassword());
            assertTrue(BCrypt.checkpw("password123", savedAuth.getPassword()));
        }
    }

    @Nested
    @DisplayName("更新密码")
    class UpdatePassword {

        @Test
        @DisplayName("应成功更新已注册用户的密码")
        void shouldUpdatePasswordSuccessfully() {
            when(redisService.get(USER_CODE_KEY + "test@example.com")).thenReturn("123456");
            UserAuth existingUser = new UserAuth();
            existingUser.setUsername("test@example.com");
            when(userAuthMapper.selectOne(any())).thenReturn(existingUser);

            userAuthService.updatePassword(validUserVO);

            verify(userAuthMapper).update(any(UserAuth.class), any());
        }

        @Test
        @DisplayName("未注册邮箱应抛出 BizException")
        void shouldThrowWhenEmailNotRegistered() {
            when(redisService.get(USER_CODE_KEY + "test@example.com")).thenReturn("123456");
            when(userAuthMapper.selectOne(any())).thenReturn(null);

            assertThrows(BizException.class, () -> userAuthService.updatePassword(validUserVO));
            verify(userAuthMapper, never()).update(any(), any());
        }
    }

    @Nested
    @DisplayName("管理员修改密码")
    class UpdateAdminPassword {

        @Test
        @DisplayName("旧密码正确时应更新密码")
        void shouldUpdatePasswordWhenOldPasswordCorrect() {
            UserDetailsDTO mockUser = UserDetailsDTO.builder().id(1).build();
            try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
                userUtilMock.when(UserUtil::getUserDetailsDTO).thenReturn(mockUser);

                String rawPassword = "oldPassword123";
                String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());
                UserAuth existingAuth = UserAuth.builder()
                        .id(1)
                        .password(hashedPassword)
                        .build();
                when(userAuthMapper.selectOne(any())).thenReturn(existingAuth);

                PasswordVO passwordVO = new PasswordVO();
                passwordVO.setOldPassword(rawPassword);
                passwordVO.setNewPassword("newPassword456");

                userAuthService.updateAdminPassword(passwordVO);

                verify(userAuthMapper).updateById((UserAuth) any());
            }
        }

        @Test
        @DisplayName("旧密码错误时应抛出 BizException")
        void shouldThrowWhenOldPasswordWrong() {
            UserDetailsDTO mockUser = UserDetailsDTO.builder().id(1).build();
            try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
                userUtilMock.when(UserUtil::getUserDetailsDTO).thenReturn(mockUser);

                String hashedPassword = BCrypt.hashpw("correctPassword", BCrypt.gensalt());
                UserAuth existingAuth = UserAuth.builder()
                        .id(1)
                        .password(hashedPassword)
                        .build();
                when(userAuthMapper.selectOne(any())).thenReturn(existingAuth);

                PasswordVO passwordVO = new PasswordVO();
                passwordVO.setOldPassword("wrongPassword");
                passwordVO.setNewPassword("newPassword456");

                assertThrows(BizException.class, () -> userAuthService.updateAdminPassword(passwordVO));
                verify(userAuthMapper, never()).updateById((UserAuth) any());
            }
        }
    }

    @Nested
    @DisplayName("用户登出")
    class Logout {

        @Test
        @DisplayName("应删除 Redis 中的登录信息并返回成功状态")
        void shouldDeleteLoginUserFromRedis() {
            UserDetailsDTO mockUser = UserDetailsDTO.builder().id(TEST_USER_ID).build();
            try (MockedStatic<UserUtil> userUtilMock = mockStatic(UserUtil.class)) {
                userUtilMock.when(UserUtil::getUserDetailsDTO).thenReturn(mockUser);
                doNothing().when(tokenService).delLoginUser(TEST_USER_ID);

                UserLogoutStatusDTO result = userAuthService.logout();

                assertNotNull(result);
                assertEquals("注销成功", result.getMessage());
                verify(tokenService).delLoginUser(TEST_USER_ID);
            }
        }
    }

    private static final Integer TEST_USER_ID = 1;

}
