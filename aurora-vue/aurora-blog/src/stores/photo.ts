import { defineStore } from 'pinia'
import api from '@/api/api'
import type { Photo, PageParams } from '@/types/blog'

export const usePhotoStore = defineStore('photoStore', {
  state: () => {
    return {
      photos: [] as Photo[],
      id: null as number | null
    }
  },
  actions: {
    async fetchAlbums() {
      const resp = await api.getAlbums()
      return resp.data.data ?? []
    },
    async fetchPhotosByAlbumId(albumId: number, params: PageParams) {
      const resp = await api.getPhotosBuAlbumId(albumId, params)
      this.id = albumId
      this.photos = resp.data.data.records ?? []
    }
  }
})
