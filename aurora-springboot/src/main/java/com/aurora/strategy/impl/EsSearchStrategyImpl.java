package com.aurora.strategy.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.strategy.SearchStrategy;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightFieldParameters;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.*;
import static com.aurora.enums.ArticleStatusEnum.PUBLIC;

@Slf4j
@Service("esSearchStrategyImpl")
public class EsSearchStrategyImpl implements SearchStrategy {

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public List<ArticleSearchDTO> searchArticle(String keywords) {
        if (StringUtils.isBlank(keywords)) {
            return new ArrayList<>();
        }
        return search(buildQuery(keywords));
    }

    private NativeQuery buildQuery(String keywords) {
        Query boolQuery = QueryBuilders.bool(b -> b
                .must(m -> m.bool(bb -> bb
                        .should(s -> s.match(ma -> ma.field("articleTitle").query(keywords)))
                        .should(s -> s.match(ma -> ma.field("articleContent").query(keywords)))
                ))
                .must(m -> m.term(t -> t.field("isDelete").value((long) FALSE)))
                .must(m -> m.term(t -> t.field("status").value((long) PUBLIC.getStatus())))
        );

        HighlightFieldParameters titleParams = HighlightFieldParameters.builder()
                .withPreTags(PRE_TAG)
                .withPostTags(POST_TAG)
                .build();
        HighlightFieldParameters contentParams = HighlightFieldParameters.builder()
                .withPreTags(PRE_TAG)
                .withPostTags(POST_TAG)
                .withFragmentSize(50)
                .build();

        HighlightField titleField = new HighlightField("articleTitle", titleParams);
        HighlightField contentField = new HighlightField("articleContent", contentParams);
        Highlight highlight = new Highlight(Arrays.asList(titleField, contentField));
        HighlightQuery highlightQuery = new HighlightQuery(highlight, ArticleSearchDTO.class);

        return NativeQuery.builder()
                .withQuery(boolQuery)
                .withHighlightQuery(highlightQuery)
                .build();
    }

    private List<ArticleSearchDTO> search(NativeQuery nativeQuery) {
        try {
            SearchHits<ArticleSearchDTO> search = elasticsearchOperations.search(nativeQuery, ArticleSearchDTO.class);
            return search.getSearchHits().stream().map(hit -> {
                ArticleSearchDTO article = hit.getContent();
                List<String> titleHighLightList = hit.getHighlightFields().get("articleTitle");
                if (CollectionUtils.isNotEmpty(titleHighLightList)) {
                    article.setArticleTitle(titleHighLightList.get(0));
                }
                List<String> contentHighLightList = hit.getHighlightFields().get("articleContent");
                if (CollectionUtils.isNotEmpty(contentHighLightList)) {
                    article.setArticleContent(contentHighLightList.get(contentHighLightList.size() - 1));
                }
                return article;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("ES 搜索失败", e);
        }
        return new ArrayList<>();
    }

}
