import type { Tag } from '@/types/blog'
import { defineStore } from 'pinia'

export const useTagStore = defineStore('tagStore', {
  state: () => {
    return {
      homeTags: [] as Tag[],
      tags: [] as Tag[]
    }
  },
  actions: {}
})
