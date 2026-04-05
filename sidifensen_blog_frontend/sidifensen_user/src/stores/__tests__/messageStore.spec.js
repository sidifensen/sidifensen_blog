import { describe, it, expect, beforeEach, vi } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useMessageStore } from '../messageStore'

describe('messageStore', () => {
  beforeEach(() => {
    vi.useFakeTimers()
    setActivePinia(createPinia())
  })

  afterEach(() => {
    vi.useRealTimers()
  })

  describe('initial state', () => {
    it('应初始化为空会话列表', () => {
      const store = useMessageStore()
      expect(store.conversationList).toEqual([])
    })

    it('应初始化总未读数为0', () => {
      const store = useMessageStore()
      expect(store.totalUnreadCount).toBe(0)
    })

    it('应初始化当前聊天用户ID为null', () => {
      const store = useMessageStore()
      expect(store.currentChatUserId).toBeNull()
    })

    it('应初始化为空消息列表', () => {
      const store = useMessageStore()
      expect(store.currentChatMessages).toEqual([])
    })
  })

  describe('setConversationList', () => {
    it('应正确设置会话列表', () => {
      const store = useMessageStore()
      const list = [
        { targetUserId: 1, unreadCount: 2 },
        { targetUserId: 2, unreadCount: 3 },
      ]
      store.setConversationList(list)
      expect(store.conversationList).toEqual(list)
    })

    it('应正确计算总未读数', () => {
      const store = useMessageStore()
      const list = [
        { targetUserId: 1, unreadCount: 2 },
        { targetUserId: 2, unreadCount: 3 },
      ]
      store.setConversationList(list)
      expect(store.totalUnreadCount).toBe(5)
    })
  })

  describe('updateConversation', () => {
    it('应更新已有会话', () => {
      const store = useMessageStore()
      store.conversationList = [{ targetUserId: 1, lastMessageContent: 'old', unreadCount: 0 }]

      store.updateConversation(1, { content: 'new message', createTime: '2026-04-05T12:00:00' }, 1)

      const conv = store.conversationList.find((c) => c.targetUserId === 1)
      expect(conv.lastMessageContent).toBe('new message')
      expect(conv.unreadCount).toBe(1)
    })

    it('应创建新会话当不存在时', () => {
      const store = useMessageStore()
      store.updateConversation(
        1,
        { content: 'first message', createTime: '2026-04-05T12:00:00' },
        1,
      )

      expect(store.conversationList.length).toBe(1)
      expect(store.conversationList[0].targetUserId).toBe(1)
      expect(store.conversationList[0].lastMessageContent).toBe('first message')
    })

    it('应在有新消息时增加未读数', () => {
      const store = useMessageStore()
      store.updateConversation(
        1,
        { content: 'message', createTime: '2026-04-05T12:00:00' },
        undefined, // 后端没传未读数
      )

      expect(store.conversationList[0].unreadCount).toBe(1)
      expect(store.totalUnreadCount).toBe(1)
    })

    it('当用户在聊天窗口时不应增加未读数', () => {
      const store = useMessageStore()
      store.currentChatUserId = 1

      store.updateConversation(
        1,
        { content: 'message', createTime: '2026-04-05T12:00:00' },
        undefined,
      )

      expect(store.conversationList[0].unreadCount).toBe(0)
    })
  })

  describe('clearConversationUnread', () => {
    it('应清空指定会话的未读数', () => {
      const store = useMessageStore()
      store.conversationList = [{ targetUserId: 1, unreadCount: 5 }]
      store.totalUnreadCount = 5

      store.clearConversationUnread(1)

      expect(store.conversationList[0].unreadCount).toBe(0)
      expect(store.totalUnreadCount).toBe(0)
    })

    it('不应影响其他会话的未读数', () => {
      const store = useMessageStore()
      store.conversationList = [
        { targetUserId: 1, unreadCount: 5 },
        { targetUserId: 2, unreadCount: 3 },
      ]
      store.totalUnreadCount = 8

      store.clearConversationUnread(1)

      expect(store.totalUnreadCount).toBe(3)
    })
  })

  describe('setCurrentChatUser', () => {
    it('应设置当前聊天用户ID', () => {
      const store = useMessageStore()
      store.setCurrentChatUser(123)
      expect(store.currentChatUserId).toBe(123)
    })

    it('应清空该用户的未读数', () => {
      const store = useMessageStore()
      store.conversationList = [{ targetUserId: 123, unreadCount: 5 }]
      store.totalUnreadCount = 5

      store.setCurrentChatUser(123)

      expect(store.conversationList[0].unreadCount).toBe(0)
      expect(store.totalUnreadCount).toBe(0)
    })
  })

  describe('addMessageToCurrentChat', () => {
    it('应添加消息到当前聊天列表', () => {
      const store = useMessageStore()
      const message = { id: 1, content: 'hello' }
      store.addMessageToCurrentChat(message)
      expect(store.currentChatMessages.length).toBe(1)
      expect(store.currentChatMessages[0].id).toBe(1)
      expect(store.currentChatMessages[0].content).toBe('hello')
    })
  })

  describe('setCurrentChatMessages', () => {
    it('应设置当前聊天消息列表', () => {
      const store = useMessageStore()
      const messages = [
        { id: 1, content: 'hello' },
        { id: 2, content: 'world' },
      ]
      store.setCurrentChatMessages(messages)
      expect(store.currentChatMessages).toEqual(messages)
    })
  })

  describe('revokeMessageInCurrentChat', () => {
    it('应标记消息为已撤回', () => {
      const store = useMessageStore()
      store.currentChatMessages = [{ id: 1, content: 'hello', isRevoked: 0 }]

      store.revokeMessageInCurrentChat(1)

      expect(store.currentChatMessages[0].isRevoked).toBe(1)
    })
  })

  describe('removeConversation', () => {
    it('应删除指定会话', () => {
      const store = useMessageStore()
      store.conversationList = [
        { targetUserId: 1, unreadCount: 2 },
        { targetUserId: 2, unreadCount: 3 },
      ]
      store.totalUnreadCount = 5

      store.removeConversation(1)

      expect(store.conversationList.length).toBe(1)
      expect(store.conversationList[0].targetUserId).toBe(2)
      expect(store.totalUnreadCount).toBe(3)
    })
  })

  describe('updateUserOnlineStatus', () => {
    it('应更新用户在线状态', () => {
      const store = useMessageStore()
      store.conversationList = [{ targetUserId: 1, isOnline: false }]

      store.updateUserOnlineStatus(1, true)

      expect(store.conversationList[0].isOnline).toBe(true)
    })
  })

  describe('updateConversationLastMessage', () => {
    it('应更新会话最后一条消息', () => {
      const store = useMessageStore()
      store.conversationList = [{ targetUserId: 1, lastMessageContent: 'old' }]

      store.updateConversationLastMessage(1, 'new message')

      expect(store.conversationList[0].lastMessageContent).toBe('new message')
    })
  })

  describe('clearAll', () => {
    it('应清空所有数据', () => {
      const store = useMessageStore()
      store.conversationList = [{ targetUserId: 1 }]
      store.totalUnreadCount = 5
      store.currentChatUserId = 1
      store.currentChatMessages = [{ id: 1 }]

      store.clearAll()

      expect(store.conversationList).toEqual([])
      expect(store.totalUnreadCount).toBe(0)
      expect(store.currentChatUserId).toBeNull()
      expect(store.currentChatMessages).toEqual([])
    })
  })

  describe('typing states', () => {
    it('setTargetUserTyping 应设置目标用户输入状态', () => {
      const store = useMessageStore()
      store.setTargetUserTyping(true)
      expect(store.targetUserTyping).toBe(true)
    })

    it('setConversationTyping 应设置指定会话输入状态', () => {
      const store = useMessageStore()
      store.setConversationTyping(123, true)
      expect(store.conversationTypingMap[123]).toBe(true)
    })

    it('clearAllTyping 应清空所有输入状态', () => {
      const store = useMessageStore()
      store.targetUserTyping = true
      store.conversationTypingMap = { 123: true }

      store.clearAllTyping()

      expect(store.targetUserTyping).toBe(false)
      expect(store.conversationTypingMap).toEqual({})
    })
  })
})
