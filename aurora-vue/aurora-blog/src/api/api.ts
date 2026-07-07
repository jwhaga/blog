import request from '@/utils/request'

export default {
  getTopAndFeaturedArticles: () => {
    return request.get('/api/articles/topAndFeatured')
  },
  getArticles: (params: any) => {
    return request.get('/api/articles/all', { params: params })
  },
  getArticlesByCategoryId: (params: any) => {
    return request.get('/api/articles/categoryId', { params: params })
  },
  getArticeById: (articleId: any) => {
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
  getArticlesByTagId: (params: any) => {
    return request.get('/api/articles/tagId', { params: params })
  },
  getAllArchives: (params: any) => {
    return request.get('/api/archives/all', { params: params })
  },
  login: (params: any) => {
    return request.post('/api/users/login', params)
  },
  saveComment: (params: any) => {
    return request.post('/api/comments/save', params)
  },
  getComments: (params: any) => {
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
  submitUserInfo: (params: any) => {
    return request.put('/api/users/info', params)
  },
  getUserInfoById: (id: any) => {
    return request.get('/api/users/info/' + id)
  },
  updateUserSubscribe: (params: any) => {
    return request.put('/api/users/subscribe', params)
  },
  sendValidationCode: (username: any) => {
    return request.get('/api/users/code', {
      params: {
        username: username
      }
    })
  },
  bindingEmail: (params: any) => {
    return request.put('/api/users/email', params)
  },
  register: (params: any) => {
    return request.post('/api/users/register', params)
  },
  searchArticles: (params: any) => {
    return request.get('/api/articles/search', {
      params: params
    })
  },
  getAlbums: () => {
    return request.get('/api/photos/albums')
  },
  getPhotosBuAlbumId: (albumId: any, params: any) => {
    return request.get('/api/albums/' + albumId + '/photos', {
      params: params
    })
  },
  getWebsiteConfig: () => {
    return request.get('/api')
  },
  qqLogin: (params: any) => {
    return request.post('/api/users/oauth/qq', params)
  },
  report: () => {
    request.post('/api/report')
  },
  getTalks: (params: any) => {
    return request.get('/api/talks', {
      params: params
    })
  },
  getTalkById: (id: any) => {
    return request.get('/api/talks/' + id)
  },
  logout: () => {
    return request.post('/api/users/logout')
  },
  getRepliesByCommentId: (commentId: any) => {
    return request.get(`/api/comments/${commentId}/replies`)
  },
  updatePassword: (params: any) => {
    return request.put('/api/users/password', params)
  },
  accessArticle: (params: any) => {
    return request.post('/api/articles/access', params)
  }
}
