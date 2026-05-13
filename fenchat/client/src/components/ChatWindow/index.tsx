import React, { useEffect, useRef } from 'react'
import { Avatar, Input, Button, Tooltip, Empty, Spin, Popover } from 'antd'
import {
  SendOutlined, SmileOutlined, PictureOutlined,
  FileOutlined, RollbackOutlined, TeamOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import { useChatStore } from '../../store/chatStore'
import { getSocket } from '../../socket'
import api from '../../utils/api'
import type { Message } from '../../types'
import './ChatWindow.css'

// 常用表情列表
const EMOJI_LIST = [
  '😊','😂','🤣','❤️','😍','🥰','😘','😎','🤔','😅',
  '😆','🤗','😜','😛','🤩','🥳','😏','😒','😓','🤦',
  '🙌','👍','👎','✌️','🤞','👏','🙏','💪','🤝','🫶',
  '🎉','🎊','🎁','🔥','💯','⭐','💫','✨','💥','🌟',
  '😭','😤','🤬','😱','😰','🥺','😔','😴','🤮','🤧',
  '🐶','🐱','🐻','🐼','🦊','🐸','🐧','🐦','🦄','🌈',
  '🍎','🍕','🍔','🍜','🍣','🎂','🧋','☕','🍺','🎵',
  '⚽','🏀','🎮','🎯','🏆','🎪','🎬','📱','💻','🚀',
]

const ChatWindow: React.FC = () => {
  const [inputValue, setInputValue] = React.useState('')
  const [loading, setLoading] = React.useState(false)
  const [sending, setSending] = React.useState(false)
  const [typing, setTyping] = React.useState(false)
  const [showEmojiPicker, setShowEmojiPicker] = React.useState(false)
  const messagesEndRef = useRef<HTMLDivElement>(null)
  const typingTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null)
  const fileInputRef = useRef<HTMLInputElement>(null)

  const { activeConversation, currentUser, messages, addMessage, updateMessage, setMessages, clearUnread } = useChatStore()

  const convId = activeConversation?.id || ''
  const convMessages = messages[convId] || []

  // 加载历史消息
  useEffect(() => {
    if (!activeConversation) return
    setLoading(true)
    const url = activeConversation.type === 'private'
      ? `/messages/private/${convId}`
      : `/messages/group/${convId}`
    api.get(url).then(({ data }) => {
      setMessages(convId, data)
      clearUnread(convId)
    }).finally(() => setLoading(false))
  }, [convId])

  // 滚动到底部
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [convMessages.length])

  // Socket 事件监听
  useEffect(() => {
    const socket = getSocket()
    const handleTyping = (data: { fromId: string }) => {
      if (data.fromId === convId) {
        setTyping(true)
        if (typingTimerRef.current) clearTimeout(typingTimerRef.current)
        typingTimerRef.current = setTimeout(() => setTyping(false), 3000)
      }
    }
    socket.on('userTyping', handleTyping)
    return () => { socket.off('userTyping', handleTyping) }
  }, [convId])

  const sendMessage = async () => {
    if (!inputValue.trim() || !activeConversation || !currentUser) return
    const content = inputValue.trim()
    setInputValue('')
    setSending(true)

    const socket = getSocket()
    if (activeConversation.type === 'private') {
      socket.emit('sendPrivateMessage', { toId: convId, content, type: 'text' })
    } else {
      socket.emit('sendGroupMessage', { groupId: convId, content, type: 'text' })
    }
    setSending(false)
  }

  const handleRecall = (msg: Message) => {
    const socket = getSocket()
    socket.emit('recallMessage', { msgId: msg.id })
  }

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file || !activeConversation) return
    const formData = new FormData()
    formData.append('file', file)
    try {
      const { data } = await api.post('/upload', formData)
      const isImage = file.type.startsWith('image/')
      const socket = getSocket()
      const payload = {
        content: isImage ? '[图片]' : `[文件] ${file.name}`,
        type: isImage ? 'image' : 'file',
        fileUrl: data.url,
        fileName: file.name,
        fileSize: file.size
      }
      if (activeConversation.type === 'private') {
        socket.emit('sendPrivateMessage', { toId: convId, ...payload })
      } else {
        socket.emit('sendGroupMessage', { groupId: convId, ...payload })
      }
    } catch { /* ignore */ }
    e.target.value = ''
  }

  const handleInputChange = (val: string) => {
    setInputValue(val)
    const socket = getSocket()
    if (activeConversation?.type === 'private') {
      socket.emit('typing', { toId: convId })
    } else if (activeConversation?.type === 'group') {
      socket.emit('typing', { groupId: convId })
    }
  }

  // 插入表情到输入框
  const handleEmojiClick = (emoji: string) => {
    setInputValue(prev => prev + emoji)
    setShowEmojiPicker(false)
  }

  // 表情面板内容
  const emojiPanel = (
    <div className="emoji-panel">
      <div className="emoji-panel-title">常用表情</div>
      <div className="emoji-grid">
        {EMOJI_LIST.map(emoji => (
          <span
            key={emoji}
            className="emoji-item"
            onClick={() => handleEmojiClick(emoji)}
            title={emoji}
          >
            {emoji}
          </span>
        ))}
      </div>
    </div>
  )

  const renderMessage = (msg: Message) => {
    const isMine = msg.fromId === currentUser?.id
    const canRecall = isMine && !msg.isRecalled &&
      dayjs().diff(dayjs(msg.createdAt), 'minute') < 2

    return (
      <div key={msg.id} className={`msg-row ${isMine ? 'msg-mine' : 'msg-other'}`}>
        {!isMine && (
          <Avatar src={msg.from.avatar} className="msg-avatar">
            {msg.from.nickname?.[0]}
          </Avatar>
        )}
        <div className="msg-body">
          {!isMine && <div className="msg-name">{msg.from.nickname}</div>}
          <div className={`msg-bubble ${isMine ? 'bubble-mine' : 'bubble-other'} ${msg.isRecalled ? 'bubble-recalled' : ''}`}>
            {msg.isRecalled ? (
              <span className="recalled-text">该消息已被撤回</span>
            ) : msg.type === 'image' ? (
              <img src={msg.fileUrl} alt="图片" className="msg-image" onClick={() => window.open(msg.fileUrl)} />
            ) : msg.type === 'file' ? (
              <a href={msg.fileUrl} download={msg.fileName} className="msg-file">
                <FileOutlined /> {msg.fileName}
              </a>
            ) : (
              <span className="msg-text">{msg.content}</span>
            )}
          </div>
          <div className="msg-meta">
            <span className="msg-time">{dayjs(msg.createdAt).format('HH:mm')}</span>
            {canRecall && (
              <Tooltip title="撤回">
                <RollbackOutlined className="recall-btn" onClick={() => handleRecall(msg)} />
              </Tooltip>
            )}
          </div>
        </div>
        {isMine && (
          <Avatar src={currentUser?.avatar} className="msg-avatar">
            {currentUser?.nickname?.[0]}
          </Avatar>
        )}
      </div>
    )
  }

  if (!activeConversation) {
    return (
      <div className="chat-empty">
        <Empty description="选择一个联系人开始聊天" image={Empty.PRESENTED_IMAGE_SIMPLE} />
      </div>
    )
  }

  return (
    <div className="chat-window">
      {/* 顶部标题栏 */}
      <div className="chat-header">
        <Avatar
          src={activeConversation.avatar}
          size={36}
          style={activeConversation.type === 'group' ? { background: 'linear-gradient(135deg,#7B5CF0,#A78BFA)' } : {}}
        >
          {activeConversation.type === 'group' ? <TeamOutlined /> : activeConversation.name[0]}
        </Avatar>
        <div className="chat-header-info">
          <span className="chat-header-name">{activeConversation.name}</span>
          {activeConversation.type === 'private' && (
            <span className={`status-dot ${activeConversation.status === 'online' ? 'online' : 'offline'}`}>
              {activeConversation.status === 'online' ? '在线' : '离线'}
            </span>
          )}
          {activeConversation.type === 'group' && (
            <span className="group-member-count">
              <TeamOutlined style={{ fontSize: 10, marginRight: 3 }} />
              {activeConversation.memberCount || 0} 位成员
            </span>
          )}
        </div>
      </div>

      {/* 消息区 */}
      <div className="chat-messages">
        {loading ? (
          <div className="loading-center"><Spin /></div>
        ) : (
          <>
            {convMessages.map(renderMessage)}
            {typing && (
              <div className="typing-indicator">
                <span /><span /><span />
                <small>对方正在输入...</small>
              </div>
            )}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* 输入区 */}
      <div className="chat-input-area">
        <div className="input-toolbar">
          <Popover
            open={showEmojiPicker}
            onOpenChange={setShowEmojiPicker}
            trigger="click"
            content={emojiPanel}
            placement="topLeft"
            overlayClassName="emoji-popover"
          >
            <Tooltip title="表情">
              <SmileOutlined className={`toolbar-icon ${showEmojiPicker ? 'toolbar-icon-active' : ''}`} />
            </Tooltip>
          </Popover>
          <Tooltip title="发送图片/文件">
            <PictureOutlined className="toolbar-icon" onClick={() => fileInputRef.current?.click()} />
          </Tooltip>
          <input ref={fileInputRef} type="file" style={{ display: 'none' }} onChange={handleFileUpload} />
        </div>
        <div className="input-row">
          <Input.TextArea
            value={inputValue}
            onChange={e => handleInputChange(e.target.value)}
            onPressEnter={e => { if (!e.shiftKey) { e.preventDefault(); sendMessage() } }}
            placeholder="输入消息（Enter 发送，Shift+Enter 换行）"
            autoSize={{ minRows: 2, maxRows: 4 }}
            className="chat-textarea"
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            onClick={sendMessage}
            loading={sending}
            disabled={!inputValue.trim()}
            className="send-btn"
          >
            发送
          </Button>
        </div>
      </div>
    </div>
  )
}

export default ChatWindow
