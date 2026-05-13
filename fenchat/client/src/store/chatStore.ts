import { create } from 'zustand'
import type { User, Conversation, Message } from '../types'

interface ChatStore {
  // 用户
  currentUser: User | null
  setCurrentUser: (user: User | null) => void

  // 会话列表
  conversations: Conversation[]
  setConversations: (convs: Conversation[]) => void
  updateConversation: (conv: Partial<Conversation> & { id: string }) => void
  addConversation: (conv: Conversation) => void

  // 当前选中会话
  activeConversation: Conversation | null
  setActiveConversation: (conv: Conversation | null) => void

  // 消息映射 conversationId -> Message[]
  messages: Record<string, Message[]>
  setMessages: (convId: string, msgs: Message[]) => void
  addMessage: (convId: string, msg: Message) => void
  updateMessage: (convId: string, msgId: string, data: Partial<Message>) => void

  // 在线用户
  onlineUsers: Set<string>
  setUserOnline: (userId: string) => void
  setUserOffline: (userId: string) => void

  // 未读数
  unreadCount: Record<string, number>
  setUnreadCount: (counts: Record<string, number>) => void
  clearUnread: (convId: string) => void
  incrementUnread: (convId: string) => void
}

export const useChatStore = create<ChatStore>((set) => ({
  currentUser: null,
  setCurrentUser: (user) => set({ currentUser: user }),

  conversations: [],
  setConversations: (convs) => set({ conversations: convs }),
  updateConversation: (conv) => set(state => ({
    conversations: state.conversations.map(c => c.id === conv.id ? { ...c, ...conv } : c)
  })),
  addConversation: (conv) => set(state => ({
    conversations: [conv, ...state.conversations.filter(c => c.id !== conv.id)]
  })),

  activeConversation: null,
  setActiveConversation: (conv) => set({ activeConversation: conv }),

  messages: {},
  setMessages: (convId, msgs) => set(state => ({ messages: { ...state.messages, [convId]: msgs } })),
  addMessage: (convId, msg) => set(state => ({
    messages: { ...state.messages, [convId]: [...(state.messages[convId] || []), msg] }
  })),
  updateMessage: (convId, msgId, data) => set(state => ({
    messages: {
      ...state.messages,
      [convId]: (state.messages[convId] || []).map(m => m.id === msgId ? { ...m, ...data } : m)
    }
  })),

  onlineUsers: new Set(),
  setUserOnline: (userId) => set(state => ({ onlineUsers: new Set([...state.onlineUsers, userId]) })),
  setUserOffline: (userId) => set(state => {
    const s = new Set(state.onlineUsers)
    s.delete(userId)
    return { onlineUsers: s }
  }),

  unreadCount: {},
  setUnreadCount: (counts) => set({ unreadCount: counts }),
  clearUnread: (convId) => set(state => ({ unreadCount: { ...state.unreadCount, [convId]: 0 } })),
  incrementUnread: (convId) => set(state => ({
    unreadCount: { ...state.unreadCount, [convId]: (state.unreadCount[convId] || 0) + 1 }
  }))
}))
