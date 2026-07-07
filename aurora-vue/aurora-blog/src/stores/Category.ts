import { defineStore } from 'pinia'
import api from '@/api/api'
import type { Category } from '@/types/blog'

export const useCategoryStore = defineStore('categoryStore', {
  state: () => {
    return {
      categories: [] as Category[]
    }
  },
  actions: {
    async fetchCategories() {
      const resp = await api.getAllCategories()
      this.categories = resp.data.data ?? []
    }
  }
})
