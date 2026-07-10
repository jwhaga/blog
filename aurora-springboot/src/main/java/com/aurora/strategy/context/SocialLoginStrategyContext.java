package com.aurora.strategy.context;

import com.aurora.exception.BizException;
import com.aurora.model.dto.UserInfoDTO;
import com.aurora.enums.LoginTypeEnum;
import com.aurora.strategy.SocialLoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SocialLoginStrategyContext {

    private static final String STRATEGY_NOT_FOUND = "未找到对应的登录策略";

    @Autowired
    private Map<String, SocialLoginStrategy> socialLoginStrategyMap;

    public UserInfoDTO executeLoginStrategy(String data, LoginTypeEnum loginTypeEnum) {
        SocialLoginStrategy strategy = socialLoginStrategyMap.get(loginTypeEnum.getStrategy());
        if (strategy == null) {
            throw new BizException(STRATEGY_NOT_FOUND);
        }
        return strategy.login(data);
    }

}
