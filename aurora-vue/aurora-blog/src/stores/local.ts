import { defineStore } from 'pinia'

export const useLocalStore = defineStore('localStore', {
  state: () => ({
    weight: 1 as number,
    recentSearch: [] as string[]
  }),
  actions: {
    addRecentSearch(keyword: string) {
      const filtered = this.recentSearch.filter(k => k !== keyword)
      filtered.unshift(keyword)
      this.recentSearch = filtered.slice(0, 10)
    },
    removeRecentSearch(keyword: string) {
      this.recentSearch = this.recentSearch.filter(k => k !== keyword)
    }
  }
})
