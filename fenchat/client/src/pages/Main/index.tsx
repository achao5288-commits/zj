import React, { useEffect, useState } from 'react'
import { Avatar, Tooltip, Badge, Popover, List, Button } from 'antd'
import {
  MessageOutlined, TeamOutlined, ReadOutlined,
  BellOutlined, LogoutOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import { useChatStore } from '../../store/chatStore'
import { getSocket, disconnectSocket } from '../../socket'
import type { Message, FriendRequest } from '../../types'
import ConversationList from '../../components/ConversationList'
import ChatWindow from '../../components/ChatWindow'
import ContactList from '../../components/ContactList'
import Moments from '../../components/Moments'
import globalMessage from '../../utils/globalMessage'
import api from '../../utils/api'
import './Main.css'

type Tab = 'chat' | 'contacts' | 'moments'

const MainPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<Tab>('chat')
  const [friendRequests, setFriendRequests] = useState<FriendRequest[]>([])
  const navigate = useNavigate()

  const {
    currentUser, setCurrentUser,
    addMessage, updateConversation,
    setUserOnline, setUserOffline, incrementUnread,
    activeConversation, setActiveConversation
  } = useChatStore()

  // 初始化用户信息
  useEffect(() => {
    const stored = localStorage.getItem('user')
    if (!stored) { navigate('/login'); return }
    setCurrentUser(JSON.parse(stored))
    loadFriendRequests()
  }, [])

  const loadFriendRequests = async () => {
    try {
      const { data } = await api.get('/friends/requests')
      setFriendRequests(data)
    } catch { /* ignore */ }
  }

  // Socket 连接和事件绑定
  useEffect(() => {
    if (!currentUser) return
    const socket = getSocket()

    // 私聊消息
    socket.on('receivePrivateMessage', (msg: Message) => {
      const convId = msg.fromId === currentUser.id ? msg.toId! : msg.fromId
      addMessage(convId, msg)
      if (activeConversation?.id !== convId) {
        incrementUnread(convId)
        globalMessage.info(`${msg.from.nickname}: ${msg.content.substring(0, 20)}`)
      }
      updateConversation({ id: convId, lastMessage: msg })
    })

    // 发送方回执
    socket.on('messageSent', (msg: Message) => {
      const convId = msg.toId || msg.groupId!
      addMessage(convId, msg)
      updateConversation({ id: convId, lastMessage: msg })
    })

    // 群聊消息
    socket.on('receiveGroupMessage', (msg: Message) => {
      const convId = msg.groupId!
      addMessage(convId, msg)
      if (activeConversation?.id !== convId) {
        incrementUnread(convId)
      }
      updateConversation({ id: convId, lastMessage: msg })
    })

    // 消息撤回
    socket.on('messageRecalled', ({ msgId }: { msgId: string }) => {
      Object.keys(useChatStore.getState().messages).forEach(convId => {
        useChatStore.getState().updateMessage(convId, msgId, { isRecalled: true, content: '该消息已被撤回' })
      })
    })

    // 在线状态
    socket.on('userOnline', ({ userId }: { userId: string }) => {
      setUserOnline(userId)
      updateConversation({ id: userId, status: 'online' })
    })
    socket.on('userOffline', ({ userId }: { userId: string }) => {
      setUserOffline(userId)
      updateConversation({ id: userId, status: 'offline' })
    })

    // 离线消息批量推送
    socket.on('offlineMessages', (msgs: Message[]) => {
      msgs.forEach(msg => {
        const convId = msg.fromId === currentUser.id ? msg.toId! : msg.fromId
        addMessage(convId, msg)
        incrementUnread(convId)
      })
    })

    return () => {
      socket.off('receivePrivateMessage')
      socket.off('messageSent')
      socket.off('receiveGroupMessage')
      socket.off('messageRecalled')
      socket.off('userOnline')
      socket.off('userOffline')
      socket.off('offlineMessages')
    }
  }, [currentUser, activeConversation])

  const handleAcceptRequest = async (reqId: string) => {
    try {
      await api.put(`/friends/request/${reqId}`, { action: 'accept' })
      globalMessage.success('已接受好友申请')
      setFriendRequests(prev => prev.filter(r => r.id !== reqId))
    } catch { /* ignore */ }
  }

  const handleRejectRequest = async (reqId: string) => {
    try {
      await api.put(`/friends/request/${reqId}`, { action: 'reject' })
      setFriendRequests(prev => prev.filter(r => r.id !== reqId))
    } catch { /* ignore */ }
  }

  // 点击联系人中的「发消息」 / 「进入群聊」，跳转到聊天页
  const handleStartChatFromContact = (conv: import('../../types').Conversation) => {
    setActiveConversation(conv)
    setActiveTab('chat')
  }

  const handleLogout = () => {
    disconnectSocket()
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setCurrentUser(null)
    navigate('/login')
  }

  const notificationContent = (
    <div className="notification-panel">
      <div className="notif-title">好友申请</div>
      {friendRequests.length === 0 ? (
        <div className="notif-empty">暂无新申请</div>
      ) : (
        <List
          dataSource={friendRequests}
          renderItem={req => (
            <List.Item className="friend-req-item">
              <div className="req-user">
                <Avatar src={req.from.avatar}>{req.from.nickname[0]}</Avatar>
                <span className="req-name">{req.from.nickname}</span>
              </div>
              <div className="req-actions">
                <Button size="small" type="primary" className="req-accept-btn" onClick={() => handleAcceptRequest(req.id)}>接受</Button>
                <Button size="small" onClick={() => handleRejectRequest(req.id)}>拒绝</Button>
              </div>
            </List.Item>
          )}
        />
      )}
    </div>
  )

  const navItems = [
    { key: 'chat', icon: <MessageOutlined />, label: '消息' },
    { key: 'contacts', icon: <TeamOutlined />, label: '联系人' },
    { key: 'moments', icon: <ReadOutlined />, label: '动态' },
  ]

  return (
    <div className="main-layout">
      {/* 左侧导航栏 */}
      <div className="sidebar">
        <div className="sidebar-top">
          <Avatar src={currentUser?.avatar} size={42} className="self-avatar">
            {currentUser?.nickname?.[0]}
          </Avatar>
        </div>

        <nav className="sidebar-nav">
          {navItems.map(item => (
            <Tooltip key={item.key} title={item.label} placement="right">
              <div
                className={`nav-item ${activeTab === item.key ? 'active' : ''}`}
                onClick={() => setActiveTab(item.key as Tab)}
              >
                {item.icon}
              </div>
            </Tooltip>
          ))}
        </nav>

        <div className="sidebar-bottom">
          <Popover content={notificationContent} trigger="click" placement="rightBottom">
            <Tooltip title="通知" placement="right">
              <div className="nav-item notif-item">
                <Badge count={friendRequests.length} size="small" offset={[4, -4]}>
                  <BellOutlined />
                </Badge>
              </div>
            </Tooltip>
          </Popover>
          <Tooltip title="退出登录" placement="right">
            <div className="nav-item logout-item" onClick={handleLogout}>
              <LogoutOutlined />
            </div>
          </Tooltip>
        </div>
      </div>

      {/* 主内容区 */}
      <div className="main-content">
        {activeTab === 'chat' && (
          <>
            <ConversationList />
            <div className="chat-area">
              <ChatWindow />
            </div>
          </>
        )}
        {activeTab === 'moments' && <Moments />}
        {activeTab === 'contacts' && (
          <ContactList onStartChat={handleStartChatFromContact} />
        )}
      </div>
    </div>
  )
}

export default MainPage
