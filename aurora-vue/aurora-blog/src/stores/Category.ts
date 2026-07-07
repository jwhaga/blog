import type { Category } from '@/types/blog'
import { defineStore } from 'pinia'

export const useCategoryStore = defineStore('categoryStore', {
  state: () => {
    return {
        categories: [] as Category[]
    }
  },
  actions: {}
})
