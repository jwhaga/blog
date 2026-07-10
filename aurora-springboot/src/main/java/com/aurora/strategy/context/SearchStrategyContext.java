package com.aurora.strategy.context;

import com.aurora.exception.BizException;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.strategy.SearchStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.aurora.enums.SearchModeEnum.getStrategy;

@Service
public class SearchStrategyContext {

    private static final String STRATEGY_NOT_FOUND = "未找到对应的搜索策略";

    @Value("${search.mode}")
    private String searchMode;

    @Autowired
    private Map<String, SearchStrategy> searchStrategyMap;

    public List<ArticleSearchDTO> executeSearchStrategy(String keywords) {
        SearchStrategy strategy = searchStrategyMap.get(getStrategy(searchMode));
        if (strategy == null) {
            throw new BizException(STRATEGY_NOT_FOUND);
        }
        return strategy.searchArticle(keywords);
    }

}
