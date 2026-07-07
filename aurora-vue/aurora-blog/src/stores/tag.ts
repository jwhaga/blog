import { defineStore } from 'pinia'
import api from '@/api/api'
import type { Tag } from '@/types/blog'

export const useTagStore = defineStore('tagStore', {
  state: () => {
    return {
      homeTags: [] as Tag[],
      tags: [] as Tag[]
    }
  },
  actions: {
    async fetchTags() {
      const resp = await api.getAllTags()
      this.tags = resp.data.data ?? []
    },
    async fetchTopTenTags() {
      const resp = await api.getTopTenTags()
      this.homeTags = resp.data.data ?? []
    }
  }
})
