package com.aurora.strategy.context;

import com.aurora.exception.BizException;
import com.aurora.strategy.UploadStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

import static com.aurora.enums.UploadModeEnum.getStrategy;

@Service
public class UploadStrategyContext {

    private static final String STRATEGY_NOT_FOUND = "未找到对应的上传策略";

    @Value("${upload.mode}")
    private String uploadMode;

    @Autowired
    private Map<String, UploadStrategy> uploadStrategyMap;

    public String executeUploadStrategy(MultipartFile file, String path) {
        UploadStrategy strategy = uploadStrategyMap.get(getStrategy(uploadMode));
        if (strategy == null) {
            throw new BizException(STRATEGY_NOT_FOUND);
        }
        return strategy.uploadFile(file, path);
    }

    public String executeUploadStrategy(String fileName, InputStream inputStream, String path) {
        UploadStrategy strategy = uploadStrategyMap.get(getStrategy(uploadMode));
        if (strategy == null) {
            throw new BizException(STRATEGY_NOT_FOUND);
        }
        return strategy.uploadFile(fileName, inputStream, path);
    }

}
