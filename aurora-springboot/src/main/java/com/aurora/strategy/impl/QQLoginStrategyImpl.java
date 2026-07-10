package com.aurora.strategy.impl;

import com.alibaba.fastjson.JSON;
import com.aurora.config.properties.QQConfigProperties;
import com.aurora.model.dto.QQTokenDTO;
import com.aurora.model.dto.QQUserInfoDTO;
import com.aurora.model.dto.SocialTokenDTO;
import com.aurora.model.dto.SocialUserInfoDTO;
import com.aurora.enums.LoginTypeEnum;
import com.aurora.exception.BizException;
import com.aurora.util.CommonUtil;
import com.aurora.model.vo.QQLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.aurora.constant.SocialLoginConstant.ACCESS_TOKEN;
import static com.aurora.constant.SocialLoginConstant.OAUTH_CONSUMER_KEY;
import static com.aurora.constant.SocialLoginConstant.QQ_OPEN_ID;
import static com.aurora.enums.StatusCodeEnum.QQ_LOGIN_ERROR;

@Slf4j
@Service("qqLoginStrategyImpl")
public class QQLoginStrategyImpl extends AbstractSocialLoginStrategyImpl {

    private static final String QQ_TOKEN_CHECK_FAIL = "QQ Token 校验失败";

    @Autowired
    private QQConfigProperties qqConfigProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public SocialTokenDTO getSocialToken(String data) {
        QQLoginVO qqLoginVO = JSON.parseObject(data, QQLoginVO.class);
        checkQQToken(qqLoginVO);
        return SocialTokenDTO.builder()
                .openId(qqLoginVO.getOpenId())
                .accessToken(qqLoginVO.getAccessToken())
                .loginType(LoginTypeEnum.QQ.getType())
                .build();
    }

    @Override
    public SocialUserInfoDTO getSocialUserInfo(SocialTokenDTO socialTokenDTO) {
        Map<String, String> formData = new HashMap<>(3);
        formData.put(QQ_OPEN_ID, socialTokenDTO.getOpenId());
        formData.put(ACCESS_TOKEN, socialTokenDTO.getAccessToken());
        formData.put(OAUTH_CONSUMER_KEY, qqConfigProperties.getAppId());
        String userInfoResult = restTemplate.getForObject(qqConfigProperties.getUserInfoUrl(), String.class, formData);
        if (userInfoResult == null) {
            throw new BizException(QQ_LOGIN_ERROR);
        }
        QQUserInfoDTO qqUserInfoDTO = JSON.parseObject(userInfoResult, QQUserInfoDTO.class);
        if (qqUserInfoDTO == null) {
            throw new BizException(QQ_LOGIN_ERROR);
        }
        return SocialUserInfoDTO.builder()
                .nickname(qqUserInfoDTO.getNickname())
                .avatar(qqUserInfoDTO.getFigureurl_qq_1())
                .build();
    }

    private void checkQQToken(QQLoginVO qqLoginVO) {
        Map<String, String> qqData = new HashMap<>(1);
        qqData.put(ACCESS_TOKEN, qqLoginVO.getAccessToken());
        try {
            String result = restTemplate.getForObject(qqConfigProperties.getCheckTokenUrl(), String.class, qqData);
            if (result == null) {
                throw new BizException(QQ_LOGIN_ERROR);
            }
            QQTokenDTO qqTokenDTO = JSON.parseObject(CommonUtil.getBracketsContent(result), QQTokenDTO.class);
            if (qqTokenDTO == null || !qqLoginVO.getOpenId().equals(qqTokenDTO.getOpenid())) {
                throw new BizException(QQ_LOGIN_ERROR);
            }
        } catch (Exception e) {
            log.error(QQ_TOKEN_CHECK_FAIL, e);
            throw new BizException(QQ_LOGIN_ERROR);
        }
    }

}
