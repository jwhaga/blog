// ============================================================
// Blog Type Definitions
// ============================================================
// Shared types for API requests/responses and Pinia store states.
// ============================================================

// ─── Article ───────────────────────────────────────────────
export interface ArticleCard {
  id: number
  articleTitle: string
  articleCover?: string
  articleAbstract?: string
  createTime: string
  categoryName?: string
  tagNames?: string[]
  viewsCount?: number
  likeCount?: number
  isFeatured?: number
  isTop?: number
  status?: number
  type?: number
  password?: string
}

export interface ArticleDetail extends ArticleCard {
  articleContent: string
  categoryId?: number
  preArticleCard?: ArticleCard
  nextArticleCard?: ArticleCard
  viewCount?: number
}

export interface TopAndFeatured {
  topArticle: ArticleCard | null
  featuredArticles: ArticleCard[]
}

// ─── Category ──────────────────────────────────────────────
export interface Category {
  id: number
  categoryName: string
  articleCount?: number
  createTime?: string
}

// ─── Tag ───────────────────────────────────────────────────
export interface Tag {
  id: number
  tagName: string
  articleCount?: number
  createTime?: string
}

// ─── Archive ───────────────────────────────────────────────
export interface Archive {
  time: string
  articles: ArticleCard[]
}

// ─── Comment ───────────────────────────────────────────────
export interface Comment {
  id: number
  userId: number
  nickname: string
  avatar: string
  website?: string
  commentContent: string
  createTime: string
  replyCount?: number
  replyDTOList?: Comment[]
}

// ─── Photo ─────────────────────────────────────────────────
export interface PhotoAlbum {
  id: number
  albumName: string
  albumCover?: string
  albumDesc?: string
  photoCount?: number
  status?: number
}

export interface Photo {
  id: number
  photoName?: string
  photoSrc: string
  albumId?: number
  createTime?: string
}

// ─── Talk ──────────────────────────────────────────────────
export interface Talk {
  id: number
  content: string
  images?: string[]
  createTime: string
  likeCount?: number
  isTop?: number
  status?: number
}

// ─── Friend Link ───────────────────────────────────────────
export interface FriendLink {
  id: number
  linkName: string
  linkAvatar?: string
  linkAddress: string
  linkIntro?: string
  createTime?: string
}

// ─── Website Config ────────────────────────────────────────
export interface WebsiteConfig {
  name?: string
  englishName?: string
  author?: string
  authorAvatar?: string
  authorIntro?: string
  logo?: string
  userAvatar?: string
  touristAvatar?: string
  websiteTitle?: string
  notice?: string
  beianNumber?: string
  gonganBeianNumber?: string
  github?: string
  gitee?: string
  qq?: string
  weChat?: string
  weibo?: string
  csdn?: string
  zhihu?: string
  twitter?: string
  stackoverflow?: string
  multiLanguage?: number
  qqLogin?: number
  isCommentReview?: number
  isEmailNotice?: number
  isReward?: number
  weiXinQRCode?: string
  alipayQRCode?: string
  favicon?: string
  websiteCreateTime?: string
}

// ─── User ──────────────────────────────────────────────────
export interface UserInfo {
  id: number
  userInfoId?: number
  email?: string
  nickname: string
  avatar: string
  intro?: string
  website?: string
  isSubscribe?: number
  ipAddress?: string
  ipSource?: string
  browser?: string
  os?: string
  lastLoginTime?: string
  loginType?: number
  isDisable?: number
  roles?: string[]
}

// ─── Pagination ────────────────────────────────────────────
export interface PageResult<T = unknown> {
  records: T[]
  count: number
}

// ─── API Params ────────────────────────────────────────────
export interface PageParams {
  current: number
  size: number
  [key: string]: unknown
}

export interface LoginParams {
  username: string
  password: string
}

export interface RegisterParams extends LoginParams {
  code: string
}

export interface CommentParams {
  commentContent: string
  articleId?: number
  talkId?: number
  replyId?: number
  parentId?: number
}

export interface PasswordParams {
  username: string
  password: string
  code: string
}

export interface AccessArticleParams {
  articleId: number
  password: string
}

export interface QQLoginParams {
  openId: string
  accessToken: string
}
