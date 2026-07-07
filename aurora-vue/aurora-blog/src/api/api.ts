import request from '@/utils/request'
import type { PageParams, LoginParams, RegisterParams, CommentParams, PasswordParams, AccessArticleParams, QQLoginParams } from '@/types/blog'

export default {
  getTopAndFeaturedArticles: () => {
    return request.get('/api/articles/topAndFeatured')
  },
  getArticles: (params: PageParams) => {
    return request.get('/api/articles/all', { params: params })
  },
  getArticlesByCategoryId: (params: PageParams) => {
    return request.get('/api/articles/categoryId', { params: params })
  },
  getArticeById: (articleId: number) => {
    return request.get('/api/articles/' + articleId)
  },
  getAllCategories: () => {
    return request.get('/api/categories/all')
  },
  getAllTags: () => {
    return request.get('/api/tags/all')
  },
  getTopTenTags: () => {
    return request.get('/api/tags/topTen')
  },
  getArticlesByTagId: (params: PageParams) => {
    return request.get('/api/articles/tagId', { params: params })
  },
  getAllArchives: (params: PageParams) => {
    return request.get('/api/archives/all', { params: params })
  },
  login: (params: LoginParams) => {
    return request.post('/api/users/login', params)
  },
  saveComment: (params: CommentParams) => {
    return request.post('/api/comments/save', params)
  },
  getComments: (params: PageParams) => {
    return request.get('/api/comments', { params: params })
  },
  getTopSixComments: () => {
    return request.get('/api/comments/topSix')
  },
  getAbout: () => {
    return request.get('/api/about')
  },
  getFriendLink: () => {
    return request.get('/api/links')
  },
  submitUserInfo: (params: Record<string, unknown>) => {
    return request.put('/api/users/info', params)
  },
  getUserInfoById: (id: number) => {
    return request.get('/api/users/info/' + id)
  },
  updateUserSubscribe: (params: Record<string, unknown>) => {
    return request.put('/api/users/subscribe', params)
  },
  sendValidationCode: (username: string) => {
    return request.get('/api/users/code', {
      params: {
        username: username
      }
    })
  },
  bindingEmail: (params: Record<string, unknown>) => {
    return request.put('/api/users/email', params)
  },
  register: (params: RegisterParams) => {
    return request.post('/api/users/register', params)
  },
  searchArticles: (params: Record<string, unknown>) => {
    return request.get('/api/articles/search', {
      params: params
    })
  },
  getAlbums: () => {
    return request.get('/api/photos/albums')
  },
  getPhotosBuAlbumId: (albumId: number, params: PageParams) => {
    return request.get('/api/albums/' + albumId + '/photos', {
      params: params
    })
  },
  getWebsiteConfig: () => {
    return request.get('/api')
  },
  qqlogin: (params: LoginParams) => {
    return request.post('/api/users/oauth/qq', params)
  },
  report: () => {
    request.post('/api/report')
  },
  getTalks: (params: PageParams) => {
    return request.get('/api/talks', {
      params: params
    })
  },
  getTalkById: (id: number) => {
    return request.get('/api/talks/' + id)
  },
  logout: () => {
    return request.post('/api/users/logout')
  },
  getRepliesByCommentId: (commentId: number) => {
    return request.get(`/api/comments/${commentId}/replies`)
  },
  updatePassword: (params: PasswordParams) => {
    return request.put('/api/users/password', params)
  },
  accessArticle: (params: AccessArticleParams) => {
    return request.post('/api/articles/access', params)
  }
}
