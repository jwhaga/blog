import { defineStore } from 'pinia'
import api from '@/api/api'
import type { ArticleCard } from '@/types/blog'

export const useSearchStore = defineStore('searchStore', {
  state: () => ({
    openModal: false,
    keywords: '',
    articles: [] as ArticleCard[]
  }),
  getters: {},
  actions: {
    setOpenModal(status: boolean) {
      this.openModal = status
      if (status === true) document.body.classList.add('modal--active')
      else document.body.classList.remove('modal--active')
      document.getElementById('App-Container')?.focus()
    },
    async search(keywords: string) {
      this.keywords = keywords
      const resp = await api.searchArticles({ keywords })
      this.articles = resp.data.data ?? []
    },
    clearSearch() {
      this.keywords = ''
      this.articles = []
    }
  }
})
