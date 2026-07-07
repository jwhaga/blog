import type { PhotoAlbum, Photo } from '@/types/blog'
import { defineStore } from 'pinia'

export const usePhotoStore = defineStore('photoStore', {
  state: () => {
    return {
      photoAlbumVisible: true,
      photos: [] as Photo[],
      id: null as number | null
    }
  },
  actions: {}
})
