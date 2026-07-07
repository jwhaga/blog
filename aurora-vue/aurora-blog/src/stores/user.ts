import type { UserInfo } from '@/types/blog'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('userStore', {
  state: () => {
    return {
      currentUrl: '' as string,
      userVisible: false,
      userInfo: null as UserInfo | null,
      token: '' as string,
      accessArticles: [] as number[],
      tab: 0 as number,
      page: 1 as number
    }
  },
  actions: {},
  persist: {
    storage: window.sessionStorage
  }
})
