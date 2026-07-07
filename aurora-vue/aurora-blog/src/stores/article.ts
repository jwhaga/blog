import { defineStore } from 'pinia'
import api from '@/api/api'
import type { ArticleCard, Category, Archive, PageParams } from '@/types/blog'

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
  actions: {
    async fetchTopAndFeaturedArticles() {
      const resp = await api.getTopAndFeaturedArticles()
      this.topArticle = resp.data.data.topArticle ?? null
      this.featuredArticles = resp.data.data.featuredArticles ?? []
    },
    async fetchArticles(params: PageParams) {
      const resp = await api.getArticles(params)
      this.articles = resp.data.data.records ?? []
    },
    async fetchCategories() {
      const resp = await api.getAllCategories()
      this.categories = resp.data.data ?? []
    },
    async fetchArchives(params: PageParams) {
      const resp = await api.getAllArchives(params)
      this.archives = resp.data.data.records ?? []
    }
  }
})
