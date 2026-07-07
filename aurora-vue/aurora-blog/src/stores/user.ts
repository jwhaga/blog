import { defineStore } from 'pinia'
import api from '@/api/api'
import type { UserInfo, LoginParams, PasswordParams } from '@/types/blog'

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
  actions: {
    async login(params: LoginParams) {
      const resp = await api.login(params)
      const body = resp.data
      if (body.flag && body.data) {
        this.token = body.data.token ?? ''
        this.userInfo = body.data.userInfo ?? null
        sessionStorage.setItem('token', this.token)
      }
      return body
    },
    async logout() {
      await api.logout()
      this.token = ''
      this.userInfo = null
      this.accessArticles = []
      sessionStorage.removeItem('token')
    },
    async fetchUserInfoById(id: number) {
      const resp = await api.getUserInfoById(id)
      this.userInfo = resp.data.data ?? null
    },
    async updatePassword(params: PasswordParams) {
      await api.updatePassword(params)
    }
  },
  persist: {
    storage: window.sessionStorage
  }
})
