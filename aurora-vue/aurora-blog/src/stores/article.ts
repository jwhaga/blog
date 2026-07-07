import type { ArticleCard, Category, Archive } from '@/types/blog'
import { defineStore } from 'pinia'

export const useArticleStore = defineStore('articleStore', {
  state: () => {
    return {
      topArticle: null as ArticleCard | null,
      featuredArticles: [] as ArticleCard[],
      articles: [] as ArticleCard[],
      categories: [] as Category[],
      archives: [] as Archive[]
    }
  },
  actions: {}
})
