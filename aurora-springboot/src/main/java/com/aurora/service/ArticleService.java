package com.aurora.service;

import com.aurora.model.dto.ArchiveDTO;
import com.aurora.model.dto.ArticleAdminDTO;
import com.aurora.model.dto.ArticleAdminViewDTO;
import com.aurora.model.dto.ArticleCardDTO;
import com.aurora.model.dto.ArticleDTO;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.PageResultDTO;
import com.aurora.model.dto.TopAndFeaturedArticlesDTO;
import com.aurora.entity.Article;
import com.aurora.model.vo.ArticlePasswordVO;
import com.aurora.model.vo.ArticleTopFeaturedVO;
import com.aurora.model.vo.ArticleVO;
import com.aurora.model.vo.ConditionVO;
import com.aurora.model.vo.DeleteVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ArticleService extends IService<Article> {

    TopAndFeaturedArticlesDTO listTopAndFeaturedArticles();

    PageResultDTO<ArticleCardDTO> listArticles();

    PageResultDTO<ArticleCardDTO> listArticlesByCategoryId(Integer categoryId);

    ArticleDTO getArticleById(Integer articleId);

    void accessArticle(ArticlePasswordVO articlePasswordVO);

    PageResultDTO<ArticleCardDTO> listArticlesByTagId(Integer tagId);

    PageResultDTO<ArchiveDTO> listArchives();

    PageResultDTO<ArticleAdminDTO> listArticlesAdmin(ConditionVO conditionVO);

    void saveOrUpdateArticle(ArticleVO articleVO);

    void updateArticleTopAndFeatured(ArticleTopFeaturedVO articleTopFeaturedVO);

    void updateArticleDelete(DeleteVO deleteVO);

    void deleteArticles(List<Integer> articleIds);

    ArticleAdminViewDTO getArticleByIdAdmin(Integer articleId);

    List<String> exportArticles(List<Integer> articleIdList);

    List<ArticleSearchDTO> listArticlesBySearch(ConditionVO condition);

}
