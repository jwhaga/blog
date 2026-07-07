import { defineStore } from 'pinia'

export const useLocalStore = defineStore('localStore', {
  state: () => {
    return {
      weight: 1 as number,
      recentSearch: [] as string[]
    }
  },
  actions: {},
  persist: true
})
