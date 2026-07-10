package com.aurora.controller;


import com.aurora.annotation.AccessLimit;
import com.aurora.annotation.OptLog;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.UserAdminDTO;
import com.aurora.model.dto.UserAreaDTO;
import com.aurora.model.dto.UserInfoDTO;
import com.aurora.model.dto.UserLogoutStatusDTO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.PasswordVO;
import com.aurora.model.vo.QQLoginVO;
import com.aurora.model.vo.ResultVO;
import com.aurora.model.vo.UserVO;
import com.aurora.service.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;

import static com.aurora.constant.OptTypeConstant.UPDATE;

@Tag(name = "用户账号模块")
@RestController
public class UserAuthController {

    /** 验证码发送限流时间窗口（秒） */
    private static final int CODE_LIMIT_SECONDS = 60;
    /** 验证码发送限流最大请求次数 */
    private static final int CODE_LIMIT_MAX_COUNT = 1;

    @Autowired
    private UserAuthService userAuthService;

    @AccessLimit(seconds = CODE_LIMIT_SECONDS, maxCount = CODE_LIMIT_MAX_COUNT)
    @Operation(summary = "发送邮箱验证码")
    @GetMapping("/users/code")
    public ResultVO<?> sendCode(
            @Parameter(description = "用户名", required = true)
            String username) {
        userAuthService.sendCode(username);
        return ResultVO.ok();
    }

    @Operation(summary = "获取用户区域分布")
    @GetMapping("/admin/users/area")
    public ResultVO<List<UserAreaDTO>> listUserAreas(ConditionVO conditionVO) {
        return ResultVO.ok(userAuthService.listUserAreas(conditionVO));
    }

    @Operation(summary = "查询后台用户列表")
    @GetMapping("/admin/users")
    public ResultVO<PageResultDTO<UserAdminDTO>> listUsers(ConditionVO conditionVO) {
        return ResultVO.ok(userAuthService.listUsers(conditionVO));
    }

    @Operation(summary = "用户注册")
    @PostMapping("/users/register")
    public ResultVO<?> register(@Valid @RequestBody UserVO userVO) {
        userAuthService.register(userVO);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改密码")
    @PutMapping("/users/password")
    public ResultVO<?> updatePassword(@Valid @RequestBody UserVO user) {
        userAuthService.updatePassword(user);
        return ResultVO.ok();
    }

    @OptLog(optType = UPDATE)
    @Operation(summary = "修改管理员密码")
    @PutMapping("/admin/users/password")
    public ResultVO<?> updateAdminPassword(@Valid @RequestBody PasswordVO passwordVO) {
        userAuthService.updateAdminPassword(passwordVO);
        return ResultVO.ok();
    }

    @Operation(summary = "用户登出")
    @PostMapping("/users/logout")
    public ResultVO<UserLogoutStatusDTO> logout() {
        return ResultVO.ok(userAuthService.logout());
    }

    @Operation(summary = "qq登录")
    @PostMapping("/users/oauth/qq")
    public ResultVO<UserInfoDTO> qqLogin(@Valid @RequestBody QQLoginVO qqLoginVO) {
        return ResultVO.ok(userAuthService.qqLogin(qqLoginVO));
    }

}
