package com.aurora.strategy.context;

import com.aurora.enums.MarkdownTypeEnum;
import com.aurora.exception.BizException;
import com.aurora.strategy.ArticleImportStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ArticleImportStrategyContext {

    private static final String STRATEGY_NOT_FOUND = "未找到对应的文章导入策略";

    @Autowired
    private Map<String, ArticleImportStrategy> articleImportStrategyMap;

    public void importArticles(MultipartFile file, String type) {
        ArticleImportStrategy strategy = articleImportStrategyMap.get(MarkdownTypeEnum.getMarkdownType(type));
        if (strategy == null) {
            throw new BizException(STRATEGY_NOT_FOUND);
        }
        strategy.importArticles(file);
    }
}
