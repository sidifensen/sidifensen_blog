import { describe, it, expect, vi, beforeEach } from 'vitest'
import axios from 'axios'

// Mock axios
vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => ({
      interceptors: {
        request: { use: vi.fn() },
        response: { use: vi.fn() },
      },
    })),
  },
}))

// Mock modules
vi.mock('@/utils/Auth', () => ({
  GetJwt: vi.fn(() => 'mock-token'),
  SetJwt: vi.fn(),
  RemoveJwt: vi.fn(),
}))

vi.mock('@/router/index.js', () => ({
  default: {
    push: vi.fn(),
  },
}))

vi.mock('@/stores/userStore', () => ({
  useUserStore: vi.fn(() => ({
    user: { id: 1 },
    clearUser: vi.fn(),
  })),
}))

vi.mock('element-plus', async () => {
  const actual = await vi.importActual('element-plus')
  return {
    ...actual,
    ElMessage: {
      success: vi.fn(),
      error: vi.fn(),
    },
  }
})

vi.mock('@/utils/Request', () => ({
  default: vi.fn(() => ({
    interceptors: {
      request: { use: vi.fn() },
      response: { use: vi.fn() },
    },
  })),
}))

describe('API Module Structure Tests', () => {
  describe('Article API', () => {
    it('应导出文章相关 API 函数', async () => {
      const articleApi = await import('@/api/article')
      expect(typeof articleApi.addArticle).toBe('function')
      expect(typeof articleApi.saveDraft).toBe('function')
      expect(typeof articleApi.getArticleDetail).toBe('function')
      expect(typeof articleApi.getUserArticleList).toBe('function')
      expect(typeof articleApi.updateArticle).toBe('function')
      expect(typeof articleApi.deleteArticle).toBe('function')
      expect(typeof articleApi.getAllArticleList).toBe('function')
      expect(typeof articleApi.getVipArticleList).toBe('function')
      expect(typeof articleApi.searchArticleByTitle).toBe('function')
      expect(typeof articleApi.getHotArticleList).toBe('function')
      expect(typeof articleApi.getHotTags).toBe('function')
    })
  })

  describe('Tag API', () => {
    it('应导出标签相关 API 函数', async () => {
      const tagApi = await import('@/api/tag')
      expect(typeof tagApi.getTagList).toBe('function')
    })
  })

  describe('Announcement API', () => {
    it('应导出公告相关 API 函数', async () => {
      const announcementApi = await import('@/api/announcement')
      expect(typeof announcementApi.getAnnouncementPage).toBe('function')
      expect(typeof announcementApi.readAnnouncement).toBe('function')
      expect(typeof announcementApi.getReadAnnouncementIds).toBe('function')
    })
  })

  describe('User API', () => {
    it('应导出用户相关 API 函数', async () => {
      const userApi = await import('@/api/user')
      expect(typeof userApi.login).toBe('function')
      expect(typeof userApi.register).toBe('function')
      expect(typeof userApi.info).toBe('function')
      expect(typeof userApi.getUserInfoById).toBe('function')
      expect(typeof userApi.updateUserInfo).toBe('function')
      expect(typeof userApi.sendEmail).toBe('function')
      expect(typeof userApi.getRecommendedAuthors).toBe('function')
      expect(typeof userApi.getCommunityStats).toBe('function')
      expect(typeof userApi.getHotSearches).toBe('function')
    })
  })

  describe('Comment API', () => {
    it('应导出评论相关 API 函数', async () => {
      const commentApi = await import('@/api/comment')
      expect(typeof commentApi.getCommentList).toBe('function')
      expect(typeof commentApi.addComment).toBe('function')
      expect(typeof commentApi.deleteComment).toBe('function')
    })
  })

  describe('Like API', () => {
    it('应导出点赞相关 API 函数', async () => {
      const likeApi = await import('@/api/like')
      expect(typeof likeApi.toggleLike).toBe('function')
      expect(typeof likeApi.isLiked).toBe('function')
    })
  })

  describe('Album API', () => {
    it('应导出相册相关 API 函数', async () => {
      const albumApi = await import('@/api/album')
      expect(typeof albumApi.getAlbum).toBe('function')
      expect(typeof albumApi.createAlbum).toBe('function')
      expect(typeof albumApi.updateAlbum).toBe('function')
      expect(typeof albumApi.deleteAlbum).toBe('function')
      expect(typeof albumApi.listAlbum).toBe('function')
      expect(typeof albumApi.listAllAlbum).toBe('function')
    })
  })

  describe('VIP API', () => {
    it('应导出 VIP 相关 API 函数', async () => {
      const vipApi = await import('@/api/vip')
      expect(typeof vipApi.getVipPlans).toBe('function')
      expect(typeof vipApi.createVipOrder).toBe('function')
      expect(typeof vipApi.getVipOrder).toBe('function')
      expect(typeof vipApi.getVipMe).toBe('function')
      expect(typeof vipApi.getVipOrderList).toBe('function')
    })
  })

  describe('Follow API', () => {
    it('应导出关注相关 API 函数', async () => {
      const followApi = await import('@/api/follow')
      expect(typeof followApi.toggleFollow).toBe('function')
      expect(typeof followApi.isFollowing).toBe('function')
      expect(typeof followApi.getFollowList).toBe('function')
      expect(typeof followApi.getFansList).toBe('function')
    })
  })

  describe('History API', () => {
    it('应导出历史记录相关 API 函数', async () => {
      const historyApi = await import('@/api/history')
      expect(typeof historyApi.getUserHistoryList).toBe('function')
      expect(typeof historyApi.clearUserHistory).toBe('function')
    })
  })

  describe('Favorite API', () => {
    it('应导出收藏相关 API 函数', async () => {
      const favoriteApi = await import('@/api/favorite')
      expect(typeof favoriteApi.getFavoriteList).toBe('function')
      expect(typeof favoriteApi.addFavorite).toBe('function')
      expect(typeof favoriteApi.updateFavorite).toBe('function')
      expect(typeof favoriteApi.addArticleToFavorite).toBe('function')
      expect(typeof favoriteApi.removeArticleFromFavorite).toBe('function')
    })
  })

  describe('Photo API', () => {
    it('应导出照片相关 API 函数', async () => {
      const photoApi = await import('@/api/photo')
      expect(typeof photoApi.uploadAlbumPhoto).toBe('function')
      expect(typeof photoApi.uploadArticlePhoto).toBe('function')
      expect(typeof photoApi.uploadAvatar).toBe('function')
      expect(typeof photoApi.batchDeletePhoto).toBe('function')
    })
  })

  describe('Notification API', () => {
    it('应导出通知相关 API 函数', async () => {
      const notificationApi = await import('@/api/notification')
      expect(typeof notificationApi.getUserNotifications).toBe('function')
      expect(typeof notificationApi.getUnreadNotificationCount).toBe('function')
      expect(typeof notificationApi.markNotificationsAsRead).toBe('function')
      expect(typeof notificationApi.deleteNotifications).toBe('function')
    })
  })

  describe('Column API', () => {
    it('应导出专栏相关 API 函数', async () => {
      const columnApi = await import('@/api/column')
      expect(typeof columnApi.getColumnList).toBe('function')
      expect(typeof columnApi.addColumn).toBe('function')
      expect(typeof columnApi.getColumnDetail).toBe('function')
      expect(typeof columnApi.updateColumn).toBe('function')
      expect(typeof columnApi.deleteColumn).toBe('function')
    })
  })
})
