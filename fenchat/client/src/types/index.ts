export interface User {
  id: string
  username: string
  nickname: string
  avatar?: string
  bio?: string
  status?: 'online' | 'offline' | 'busy'
}

export interface Message {
  id: string
  content: string
  type: 'text' | 'image' | 'file' | 'voice'
  fileUrl?: string
  fileName?: string
  fileSize?: number
  fromId: string
  toId?: string
  groupId?: string
  isRead: boolean
  isRecalled: boolean
  createdAt: string
  from: Pick<User, 'id' | 'nickname' | 'avatar'>
}

export interface Conversation {
  id: string // friendId or groupId
  type: 'private' | 'group'
  name: string
  avatar?: string
  lastMessage?: Message
  unreadCount: number
  status?: string
  memberCount?: number // 群聊成员数
}

export interface Group {
  id: string
  name: string
  avatar?: string
  announcement?: string
  creatorId: string
  members: GroupMember[]
}

export interface GroupMember {
  id: string
  groupId: string
  userId: string
  role: 'owner' | 'admin' | 'member'
  user: Pick<User, 'id' | 'nickname' | 'avatar' | 'status'>
}

export interface FriendRequest {
  id: string
  fromId: string
  toId: string
  message?: string
  status: 'pending' | 'accepted' | 'rejected'
  createdAt: string
  from: Pick<User, 'id' | 'username' | 'nickname' | 'avatar'>
}

export interface Moment {
  id: string
  content: string
  images?: string
  userId: string
  createdAt: string
  user: Pick<User, 'id' | 'nickname' | 'avatar'>
  likes: MomentLike[]
  comments: MomentComment[]
}

export interface MomentLike {
  id: string
  momentId: string
  userId: string
  user: Pick<User, 'id' | 'nickname'>
}

export interface MomentComment {
  id: string
  content: string
  momentId: string
  userId: string
  createdAt: string
  user: Pick<User, 'id' | 'nickname' | 'avatar'>
}
