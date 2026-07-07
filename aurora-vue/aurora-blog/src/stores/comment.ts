import { defineStore } from 'pinia'
import api from '@/api/api'
import type { Comment, CommentParams, PageParams } from '@/types/blog'

export const useCommentStore = defineStore('commentStore', {
  state: () => {
    return {
      recentComment: null as Comment | null,
      type: 1 as number
    }
  },
  actions: {
    async fetchComments(params: PageParams) {
      const resp = await api.getComments(params)
      return resp.data.data ?? []
    },
    async saveComment(params: CommentParams) {
      return await api.saveComment(params)
    },
    async fetchRepliesByCommentId(commentId: number) {
      const resp = await api.getRepliesByCommentId(commentId)
      return resp.data.data ?? []
    }
  }
})
