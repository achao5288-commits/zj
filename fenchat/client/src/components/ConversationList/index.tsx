import React, { useEffect, useState } from 'react'
import { Avatar, Badge, Input, Modal, Button, Dropdown, Checkbox } from 'antd'
import { SearchOutlined, PlusOutlined, UserAddOutlined, TeamOutlined } from '@ant-design/icons'
import dayjs from 'dayjs'
import { useChatStore } from '../../store/chatStore'
import type { Conversation, User } from '../../types'
import api from '../../utils/api'
import globalMessage from '../../utils/globalMessage'
import { getSocket } from '../../socket'
import './ConversationList.css'

const ConversationList: React.FC = () => {
  const [search, setSearch] = useState('')
  const [addModalOpen, setAddModalOpen] = useState(false)
  const [searchQuery, setSearchQuery] = useState('')
  const [searchResults, setSearchResults] = useState<User[]>([])
  const [searching, setSearching] = useState(false)

  // 创建群聊状态
  const [createGroupOpen, setCreateGroupOpen] = useState(false)
  const [groupName, setGroupName] = useState('')
  const [friendList, setFriendList] = useState<User[]>([])
  const [selectedFriendIds, setSelectedFriendIds] = useState<string[]>([])
  const [creatingGroup, setCreatingGroup] = useState(false)

  const {
    conversations, activeConversation, setActiveConversation,
    unreadCount, setConversations, addConversation
  } = useChatStore()

  // 加载好友和群组会话
  useEffect(() => {
    const loadConversations = async () => {
      try {
        const [friendsRes, groupsRes, unreadRes] = await Promise.all([
          api.get('/friends'),
          api.get('/groups/mine'),
          api.get('/messages/unread')
        ])

        const friendConvs: Conversation[] = friendsRes.data.map((f: User) => ({
          id: f.id,
          type: 'private' as const,
          name: f.nickname,
          avatar: f.avatar,
          status: f.status,
          unreadCount: unreadRes.data[f.id] || 0
        }))

        const groupConvs: Conversation[] = groupsRes.data.map((g: any) => ({
          id: g.id,
          type: 'group' as const,
          name: g.name,
          avatar: g.avatar,
          unreadCount: 0,
          memberCount: g.members?.length || 0
        }))

        setConversations([...friendConvs, ...groupConvs])
      } catch { /* ignore */ }
    }
    loadConversations()
  }, [])

  // 搜索用户
  const handleSearch = async () => {
    if (!searchQuery.trim()) return
    setSearching(true)
    try {
      const { data } = await api.get(`/auth/search?q=${searchQuery}`)
      setSearchResults(data)
    } catch { /* ignore */ } finally {
      setSearching(false)
    }
  }

  // 添加好友
  const handleAddFriend = async (userId: string) => {
    try {
      await api.post('/friends/request', { toId: userId, message: '你好，我想加你为好友' })
      globalMessage.success('好友申请已发送')
    } catch (e: any) {
      globalMessage.error(e.response?.data?.message || '发送失败')
    }
  }

  // 打开创建群聊弹窗，加载好友列表
  const handleOpenCreateGroup = async () => {
    setCreateGroupOpen(true)
    setGroupName('')
    setSelectedFriendIds([])
    try {
      const { data } = await api.get('/friends')
      setFriendList(data)
    } catch { /* ignore */ }
  }

  // 创建群聊
  const handleCreateGroup = async () => {
    if (!groupName.trim()) return globalMessage.warning('请输入群名称')
    if (selectedFriendIds.length === 0) return globalMessage.warning('请至少选择一位成员')
    setCreatingGroup(true)
    try {
      const { data } = await api.post('/groups', { name: groupName.trim(), memberIds: selectedFriendIds })
      const newConv: Conversation = {
        id: data.id,
        type: 'group',
        name: data.name,
        avatar: data.avatar,
        unreadCount: 0,
        memberCount: data.members?.length || 0
      }
      addConversation(newConv)
      // 通知服务端 Socket 加入新群组 Room
      const socket = getSocket()
      socket.emit('joinGroup', { groupId: data.id })
      setCreateGroupOpen(false)
      globalMessage.success('群聊创建成功！')
    } catch (e: any) {
      globalMessage.error(e.response?.data?.message || '创建失败')
    } finally {
      setCreatingGroup(false)
    }
  }

  const filteredConvs = conversations.filter(c =>
    c.name.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="conv-list">
      {/* 搜索栏 */}
      <div className="conv-search-bar">
        <Input
          prefix={<SearchOutlined style={{ color: '#E8580A' }} />}
          placeholder="搜索..."
          value={search}
          onChange={e => setSearch(e.target.value)}
          className="conv-search-input"
        />
        <Dropdown
          trigger={['click']}
          menu={{
            items: [
              { key: 'friend', icon: <UserAddOutlined />, label: '添加好友' },
              { key: 'group', icon: <TeamOutlined />, label: '创建群聊' }
            ],
            onClick: ({ key }) => {
              if (key === 'friend') setAddModalOpen(true)
              else if (key === 'group') handleOpenCreateGroup()
            }
          }}
        >
          <div className="add-btn">
            <PlusOutlined />
          </div>
        </Dropdown>
      </div>

      {/* 会话列表 */}
      <div className="conv-items">
        {filteredConvs.map(conv => (
          <div
            key={conv.id}
            className={`conv-item ${activeConversation?.id === conv.id ? 'active' : ''}`}
            onClick={() => setActiveConversation(conv)}
          >
            <div className="conv-avatar-wrap">
              <Avatar src={conv.avatar} size={44} className={`conv-avatar ${conv.type === 'group' ? 'group-avatar' : ''}`}>
                {conv.type === 'group' ? <TeamOutlined /> : conv.name[0]}
              </Avatar>
              {conv.type === 'private' && (
                <div className={`online-badge ${conv.status === 'online' ? 'online' : ''}`} />
              )}
              {conv.type === 'group' && (
                <div className="group-badge" />
              )}
            </div>
            <div className="conv-info">
              <div className="conv-top">
                <span className="conv-name">{conv.name}</span>
                {conv.lastMessage && (
                  <span className="conv-time">
                    {dayjs(conv.lastMessage.createdAt).format('HH:mm')}
                  </span>
                )}
              </div>
              <div className="conv-bottom">
                <span className="conv-last">
                  {conv.lastMessage
                    ? (conv.lastMessage.isRecalled ? '消息已撤回' : conv.lastMessage.content)
                    : (conv.type === 'group' ? `群聊 · ${conv.memberCount || 0}人` : '点击开始聊天')}
                </span>
                {(unreadCount[conv.id] || 0) > 0 && (
                  <Badge count={unreadCount[conv.id]} size="small" className="unread-badge" />
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* 创建群聊弹窗 */}
      <Modal
        title={<span><TeamOutlined style={{ color: '#E8580A', marginRight: 8 }} />创建群聊</span>}
        open={createGroupOpen}
        onCancel={() => setCreateGroupOpen(false)}
        onOk={handleCreateGroup}
        okText="创建群聊"
        cancelText="取消"
        okButtonProps={{ loading: creatingGroup, style: { background: 'linear-gradient(135deg,#E8580A,#FF8C42)', border: 'none' } }}
        className="create-group-modal"
      >
        <div className="create-group-body">
          <Input
            placeholder="请输入群名称（最多20字）"
            value={groupName}
            onChange={e => setGroupName(e.target.value)}
            maxLength={20}
            className="group-name-input"
          />
          <div className="friend-select-label">选择群成员（好友）</div>
          <div className="friend-select-list">
            {friendList.length === 0 ? (
              <div className="no-friends-tip">暂无好友，请先添加好友</div>
            ) : (
              <Checkbox.Group
                value={selectedFriendIds}
                onChange={vals => setSelectedFriendIds(vals as string[])}
                style={{ display: 'flex', flexDirection: 'column', gap: 10, width: '100%' }}
              >
                {friendList.map(f => (
                  <Checkbox key={f.id} value={f.id} className="friend-checkbox-item">
                    <Avatar src={f.avatar} size={28} style={{ marginRight: 8, background: 'linear-gradient(135deg,#E8580A,#FF8C42)', color: 'white' }}>
                      {f.nickname[0]}
                    </Avatar>
                    <span className="friend-checkbox-name">{f.nickname}</span>
                    <span className="friend-checkbox-username">@{f.username}</span>
                  </Checkbox>
                ))}
              </Checkbox.Group>
            )}
          </div>
          {selectedFriendIds.length > 0 && (
            <div className="selected-count">已选 {selectedFriendIds.length} 人</div>
          )}
        </div>
      </Modal>

      {/* 添加好友弹窗 */}
      <Modal
        title="添加好友"
        open={addModalOpen}
        onCancel={() => { setAddModalOpen(false); setSearchResults([]); setSearchQuery('') }}
        footer={null}
        className="add-friend-modal"
      >
        <div className="add-friend-search">
          <Input
            placeholder="搜索用户名或昵称"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            onPressEnter={handleSearch}
            suffix={<SearchOutlined onClick={handleSearch} style={{ cursor: 'pointer', color: '#E8580A' }} />}
          />
        </div>
        <div className="search-results">
          {searching && <div className="searching">搜索中...</div>}
          {searchResults.map(u => (
            <div key={u.id} className="search-user-item">
              <Avatar src={u.avatar}>{u.nickname[0]}</Avatar>
              <div className="search-user-info">
                <div className="search-user-name">{u.nickname}</div>
                <div className="search-user-username">@{u.username}</div>
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
            <div className="no-result">未找到用户</div>
          )}
        </div>
      </Modal>
    </div>
  )
}

export default ConversationList
