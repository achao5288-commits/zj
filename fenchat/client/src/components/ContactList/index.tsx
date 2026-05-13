import React, { useEffect, useState } from 'react'
import { Avatar, Badge, Button, Tabs, Modal, Input, Empty, Spin, Tooltip } from 'antd'
import {
  MessageOutlined, DeleteOutlined, TeamOutlined,
  LogoutOutlined, UserOutlined, SearchOutlined,
  UserAddOutlined, PlusOutlined
} from '@ant-design/icons'
import { useChatStore } from '../../store/chatStore'
import type { User, Conversation } from '../../types'
import api from '../../utils/api'
import globalMessage from '../../utils/globalMessage'
import { getSocket } from '../../socket'
import './ContactList.css'

interface Group {
  id: string
  name: string
  avatar?: string
  memberCount: number
  members: { user: Pick<User, 'id' | 'nickname' | 'avatar' | 'status'> }[]
}

interface Props {
  onStartChat: (conv: Conversation) => void
}

const ContactList: React.FC<Props> = ({ onStartChat }) => {
  const [activeTab, setActiveTab] = useState('friends')
  const [friends, setFriends] = useState<User[]>([])
  const [groups, setGroups] = useState<Group[]>([])
  const [loading, setLoading] = useState(false)
  const [search, setSearch] = useState('')

  // 添加好友搜索
  const [addModalOpen, setAddModalOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<User[]>([])
  const [searching, setSearching] = useState(false)

  const { onlineUsers } = useChatStore()

  // 加载好友列表
  const loadFriends = async () => {
    setLoading(true)
    try {
      const { data } = await api.get('/friends')
      setFriends(data)
    } catch { /* ignore */ } finally {
      setLoading(false)
    }
  }

  // 加载群组列表
  const loadGroups = async () => {
    setLoading(true)
    try {
      const { data } = await api.get('/groups/mine')
      setGroups(data.map((g: any) => ({ ...g, memberCount: g.members?.length || 0 })))
    } catch { /* ignore */ } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadFriends()
    loadGroups()
  }, [])

  // 删除好友
  const handleDeleteFriend = (friend: User) => {
    Modal.confirm({
      title: `删除好友`,
      content: `确定要删除好友「${friend.nickname}」吗？`,
      okText: '删除',
      cancelText: '取消',
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          await api.delete(`/friends/${friend.id}`)
          setFriends(prev => prev.filter(f => f.id !== friend.id))
          globalMessage.success('已删除好友')
        } catch (e: any) {
          globalMessage.error(e.response?.data?.message || '删除失败')
        }
      }
    })
  }

  // 退出群组
  const handleLeaveGroup = (group: Group) => {
    Modal.confirm({
      title: `退出群聊`,
      content: `确定要退出「${group.name}」吗？`,
      okText: '退出',
      cancelText: '取消',
      okButtonProps: { danger: true },
      onOk: async () => {
        try {
          await api.delete(`/groups/${group.id}/leave`)
          setGroups(prev => prev.filter(g => g.id !== group.id))
          globalMessage.success('已退出群聊')
        } catch (e: any) {
          globalMessage.error(e.response?.data?.message || '退出失败')
        }
      }
    })
  }

  // 搜索用户
  const handleSearchUser = async () => {
    if (!searchQuery.trim()) return
    setSearching(true)
    try {
      const { data } = await api.get(`/auth/search?q=${searchQuery}`)
      setSearchResults(data)
    } catch { /* ignore */ } finally {
      setSearching(false)
    }
  }

  // 发送好友申请
  const handleAddFriend = async (userId: string) => {
    try {
      await api.post('/friends/request', { toId: userId, message: '你好，我想加你为好友' })
      globalMessage.success('好友申请已发送')
    } catch (e: any) {
      globalMessage.error(e.response?.data?.message || '发送失败')
    }
  }

  // 过滤列表
  const filteredFriends = friends.filter(f =>
    f.nickname.toLowerCase().includes(search.toLowerCase()) ||
    f.username.toLowerCase().includes(search.toLowerCase())
  )
  const filteredGroups = groups.filter(g =>
    g.name.toLowerCase().includes(search.toLowerCase())
  )

  const friendsTab = (
    <div className="contact-panel">
      {loading ? (
        <div className="contact-loading"><Spin /></div>
      ) : filteredFriends.length === 0 ? (
        <Empty description="暂无好友" image={Empty.PRESENTED_IMAGE_SIMPLE} className="contact-empty" />
      ) : (
        <div className="contact-list">
          {filteredFriends.map(friend => {
            const isOnline = onlineUsers.has(friend.id) || friend.status === 'online'
            return (
              <div key={friend.id} className="contact-item">
                <div className="contact-avatar-wrap">
                  <Avatar src={friend.avatar} size={46} className="contact-avatar">
                    {friend.nickname[0]}
                  </Avatar>
                  <div className={`contact-status-dot ${isOnline ? 'online' : 'offline'}`} />
                </div>
                <div className="contact-info">
                  <div className="contact-name">{friend.nickname}</div>
                  <div className="contact-sub">
                    <span className="contact-username">@{friend.username}</span>
                    <Badge
                      status={isOnline ? 'success' : 'default'}
                      text={isOnline ? '在线' : '离线'}
                      className="contact-online-text"
                    />
                  </div>
                </div>
                <div className="contact-actions">
                  <Tooltip title="发消息">
                    <Button
                      type="primary"
                      shape="circle"
                      icon={<MessageOutlined />}
                      size="small"
                      className="action-chat-btn"
                      onClick={() => onStartChat({
                        id: friend.id,
                        type: 'private',
                        name: friend.nickname,
                        avatar: friend.avatar,
                        status: friend.status,
                        unreadCount: 0
                      })}
                    />
                  </Tooltip>
                  <Tooltip title="删除好友">
                    <Button
                      shape="circle"
                      icon={<DeleteOutlined />}
                      size="small"
                      danger
                      className="action-delete-btn"
                      onClick={() => handleDeleteFriend(friend)}
                    />
                  </Tooltip>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )

  const groupsTab = (
    <div className="contact-panel">
      {loading ? (
        <div className="contact-loading"><Spin /></div>
      ) : filteredGroups.length === 0 ? (
        <Empty description="暂无群聊" image={Empty.PRESENTED_IMAGE_SIMPLE} className="contact-empty" />
      ) : (
        <div className="contact-list">
          {filteredGroups.map(group => (
            <div key={group.id} className="contact-item">
              <div className="contact-avatar-wrap">
                <Avatar
                  src={group.avatar}
                  size={46}
                  className="contact-avatar group-contact-avatar"
                  icon={<TeamOutlined />}
                />
              </div>
              <div className="contact-info">
                <div className="contact-name">{group.name}</div>
                <div className="contact-sub">
                  <UserOutlined style={{ fontSize: 11, marginRight: 4, color: '#aaa' }} />
                  <span className="contact-username">{group.memberCount} 位成员</span>
                </div>
              </div>
              <div className="contact-actions">
                <Tooltip title="进入群聊">
                  <Button
                    type="primary"
                    shape="circle"
                    icon={<MessageOutlined />}
                    size="small"
                    className="action-chat-btn"
                    onClick={() => onStartChat({
                      id: group.id,
                      type: 'group',
                      name: group.name,
                      avatar: group.avatar,
                      unreadCount: 0,
                      memberCount: group.memberCount
                    })}
                  />
                </Tooltip>
                <Tooltip title="退出群聊">
                  <Button
                    shape="circle"
                    icon={<LogoutOutlined />}
                    size="small"
                    danger
                    className="action-delete-btn"
                    onClick={() => handleLeaveGroup(group)}
                  />
                </Tooltip>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  )

  return (
    <div className="contactlist-container">
      {/* 头部 */}
      <div className="contactlist-header">
        <h2 className="contactlist-title">联系人</h2>
        <Tooltip title="添加好友">
          <Button
            type="primary"
            shape="circle"
            icon={<PlusOutlined />}
            className="add-friend-header-btn"
            onClick={() => { setAddModalOpen(true); setSearchResults([]); setSearchQuery('') }}
          />
        </Tooltip>
      </div>

      {/* 搜索框 */}
      <div className="contactlist-search">
        <Input
          prefix={<SearchOutlined style={{ color: '#E8580A' }} />}
          placeholder="搜索好友或群聊..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="contactlist-search-input"
          allowClear
        />
      </div>

      {/* Tab 内容 */}
      <Tabs
        activeKey={activeTab}
        onChange={setActiveTab}
        className="contactlist-tabs"
        items={[
          {
            key: 'friends',
            label: (
              <span className="tab-label">
                <UserOutlined /> 好友 {friends.length > 0 && <span className="tab-count">{friends.length}</span>}
              </span>
            ),
            children: friendsTab
          },
          {
            key: 'groups',
            label: (
              <span className="tab-label">
                <TeamOutlined /> 群聊 {groups.length > 0 && <span className="tab-count">{groups.length}</span>}
              </span>
            ),
            children: groupsTab
          }
        ]}
      />

      {/* 添加好友弹窗 */}
      <Modal
        title="搜索添加好友"
        open={addModalOpen}
        onCancel={() => { setAddModalOpen(false); setSearchResults([]); setSearchQuery('') }}
        footer={null}
      >
        <div style={{ marginBottom: 16 }}>
          <Input
            placeholder="搜索用户名或昵称"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            onPressEnter={handleSearchUser}
            suffix={<SearchOutlined onClick={handleSearchUser} style={{ cursor: 'pointer', color: '#E8580A' }} />}
          />
        </div>
        <div style={{ maxHeight: 320, overflowY: 'auto' }}>
          {searching && <div style={{ textAlign: 'center', color: '#aaa', padding: 20 }}>搜索中...</div>}
          {searchResults.map(u => (
            <div key={u.id} className="search-result-item">
              <Avatar src={u.avatar} size={38}>{u.nickname[0]}</Avatar>
              <div className="search-result-info">
                <div className="search-result-name">{u.nickname}</div>
                <div className="search-result-username">@{u.username}</div>
              </div>
              <Button
                type="primary"
                size="small"
                icon={<UserAddOutlined />}
                onClick={() => handleAddFriend(u.id)}
                className="add-user-btn"
              >
                添加
              </Button>
            </div>
          ))}
          {!searching && searchResults.length === 0 && searchQuery && (
            <div style={{ textAlign: 'center', color: '#aaa', padding: 20 }}>未找到用户</div>
          )}
        </div>
      </Modal>
    </div>
  )
}

export default ContactList
