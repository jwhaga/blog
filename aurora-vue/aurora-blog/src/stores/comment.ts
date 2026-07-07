import type { Comment } from '@/types/blog'
import { defineStore } from 'pinia'

export const useCommentStore = defineStore('commentStore', {
  state: () => {
    return {
      recentComment: null as Comment | null,
      type: '' as string
    }
  },
  actions: {}
})
