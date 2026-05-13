import React, { useEffect, useRef, useState } from 'react'
import { Avatar, Input, Button, Image, Spin } from 'antd'
import {
  HeartOutlined, HeartFilled, CommentOutlined,
  PictureOutlined, CloseCircleFilled, LoadingOutlined
} from '@ant-design/icons'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import { useChatStore } from '../../store/chatStore'
import type { Moment } from '../../types'
import api from '../../utils/api'
import globalMessage from '../../utils/globalMessage'
import './Moments.css'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

const MAX_IMAGES = 9

const Moments: React.FC = () => {
  const [moments, setMoments] = useState<Moment[]>([])
  const [loading, setLoading] = useState(false)
  const [postContent, setPostContent] = useState('')
  const [commentInputs, setCommentInputs] = useState<Record<string, string>>({})
  const [showCommentInput, setShowCommentInput] = useState<Record<string, boolean>>({})

  // 图片相关状态
  const [selectedFiles, setSelectedFiles] = useState<File[]>([])
  const [imagePreviews, setImagePreviews] = useState<string[]>([])
  const [uploading, setUploading] = useState(false)

  const fileInputRef = useRef<HTMLInputElement>(null)
  const { currentUser } = useChatStore()

  const loadMoments = async () => {
    setLoading(true)
    try {
      const { data } = await api.get('/moments')
      setMoments(data)
    } catch { /* ignore */ } finally {
      setLoading(false)
    }
  }

  useEffect(() => { loadMoments() }, [])

  // 清理 Object URL，防止内存泄漏
  useEffect(() => {
    return () => { imagePreviews.forEach(url => URL.revokeObjectURL(url)) }
  }, [imagePreviews])

  // 选择图片
  const handleImageSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = Array.from(e.target.files || [])
    if (files.length === 0) return

    // 过滤非图片文件
    const imageFiles = files.filter(f => f.type.startsWith('image/'))
    if (imageFiles.length !== files.length) {
      globalMessage.warning('只支持图片文件')
    }

    // 限制最多 9 张
    const remaining = MAX_IMAGES - selectedFiles.length
    const toAdd = imageFiles.slice(0, remaining)
    if (imageFiles.length > remaining) {
      globalMessage.warning(`最多选择 ${MAX_IMAGES} 张图片`)
    }

    const newPreviews = toAdd.map(f => URL.createObjectURL(f))
    setSelectedFiles(prev => [...prev, ...toAdd])
    setImagePreviews(prev => [...prev, ...newPreviews])

    // 重置 input，允许重复选同一文件
    e.target.value = ''
  }

  // 删除预览中的某张图片
  const handleRemoveImage = (index: number) => {
    URL.revokeObjectURL(imagePreviews[index])
    setSelectedFiles(prev => prev.filter((_, i) => i !== index))
    setImagePreviews(prev => prev.filter((_, i) => i !== index))
  }

  // 发布动态
  const handlePost = async () => {
    if (!postContent.trim() && selectedFiles.length === 0) return
    setUploading(true)
    try {
      // 上传所有图片
      const uploadedUrls: string[] = []
      for (const file of selectedFiles) {
        const formData = new FormData()
        formData.append('file', file)
        const { data } = await api.post('/upload', formData)
        uploadedUrls.push(data.url)
      }

      // 发布动态（带图片 URL 数组）
      const payload: { content: string; images?: string[] } = { content: postContent }
      if (uploadedUrls.length > 0) payload.images = uploadedUrls

      const { data } = await api.post('/moments', payload)
      setMoments(prev => [data, ...prev])
      setPostContent('')
      setSelectedFiles([])
      setImagePreviews([])
      globalMessage.success('发布成功')
    } catch {
      globalMessage.error('发布失败，请重试')
    } finally {
      setUploading(false)
    }
  }

  const handleLike = async (momentId: string) => {
    try {
      const { data } = await api.post(`/moments/${momentId}/like`)
      setMoments(prev => prev.map(m => {
        if (m.id !== momentId) return m
        if (data.liked) {
          return { ...m, likes: [...m.likes, { id: 'tmp', momentId, userId: currentUser!.id, user: { id: currentUser!.id, nickname: currentUser!.nickname } }] }
        } else {
          return { ...m, likes: m.likes.filter(l => l.userId !== currentUser!.id) }
        }
      }))
    } catch { /* ignore */ }
  }

  const handleComment = async (momentId: string) => {
    const content = commentInputs[momentId]?.trim()
    if (!content) return
    try {
      const { data } = await api.post(`/moments/${momentId}/comment`, { content })
      setMoments(prev => prev.map(m => m.id === momentId ? { ...m, comments: [...m.comments, data] } : m))
      setCommentInputs(prev => ({ ...prev, [momentId]: '' }))
      setShowCommentInput(prev => ({ ...prev, [momentId]: false }))
    } catch { /* ignore */ }
  }

  const canPost = postContent.trim().length > 0 || selectedFiles.length > 0

  return (
    <div className="moments-container">
      {/* 发布区 */}
      <div className="moments-post-card">
        <Avatar src={currentUser?.avatar} size={40} className="post-avatar">
          {currentUser?.nickname?.[0]}
        </Avatar>
        <div className="post-input-area">
          <Input.TextArea
            value={postContent}
            onChange={e => setPostContent(e.target.value)}
            placeholder="分享你的心情..."
            autoSize={{ minRows: 2, maxRows: 5 }}
            className="post-textarea"
          />

          {/* 图片预览区 */}
          {imagePreviews.length > 0 && (
            <div className={`post-image-preview grid-${Math.min(imagePreviews.length, 3)}`}>
              {imagePreviews.map((src, i) => (
                <div key={i} className="preview-item">
                  <img src={src} alt={`预览${i + 1}`} className="preview-img" />
                  <CloseCircleFilled
                    className="preview-remove"
                    onClick={() => handleRemoveImage(i)}
                  />
                </div>
              ))}
              {/* 继续添加按钮（未达9张时显示） */}
              {imagePreviews.length < MAX_IMAGES && (
                <div className="preview-add" onClick={() => fileInputRef.current?.click()}>
                  <PictureOutlined className="preview-add-icon" />
                  <span>{imagePreviews.length}/{MAX_IMAGES}</span>
                </div>
              )}
            </div>
          )}

          <div className="post-actions">
            <div className="post-left-actions">
              {/* 隐藏的文件输入 */}
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                multiple
                style={{ display: 'none' }}
                onChange={handleImageSelect}
              />
              <div
                className={`post-img-btn ${selectedFiles.length > 0 ? 'active' : ''}`}
                onClick={() => fileInputRef.current?.click()}
                title="添加图片（最多9张）"
              >
                <PictureOutlined />
                {selectedFiles.length > 0 && (
                  <span className="img-count-badge">{selectedFiles.length}</span>
                )}
              </div>
            </div>
            <Button
              type="primary"
              size="small"
              disabled={!canPost}
              loading={uploading}
              onClick={handlePost}
              className="post-submit-btn"
              icon={uploading ? <LoadingOutlined /> : undefined}
            >
              {uploading ? '发布中...' : '发布'}
            </Button>
          </div>
        </div>
      </div>

      {/* 动态列表 */}
      {loading ? (
        <div className="moments-loading"><Spin /></div>
      ) : (
        <div className="moments-list">
          {moments.map(moment => {
            const isLiked = moment.likes.some(l => l.userId === currentUser?.id)
            const images = moment.images ? JSON.parse(moment.images) : []

            return (
              <div key={moment.id} className="moment-card">
                <Avatar src={moment.user.avatar} size={42} className="moment-user-avatar">
                  {moment.user.nickname[0]}
                </Avatar>
                <div className="moment-body">
                  <div className="moment-user-name">{moment.user.nickname}</div>
                  {moment.content && (
                    <div className="moment-content">{moment.content}</div>
                  )}

                  {images.length > 0 && (
                    <div className={`moment-images grid-${Math.min(images.length, 3)}`}>
                      <Image.PreviewGroup>
                        {images.map((img: string, i: number) => (
                          <Image key={i} src={img} className="moment-img" />
                        ))}
                      </Image.PreviewGroup>
                    </div>
                  )}

                  <div className="moment-footer">
                    <span className="moment-time">{dayjs(moment.createdAt).fromNow()}</span>
                    <div className="moment-actions-bar">
                      <span className="moment-action" onClick={() => handleLike(moment.id)}>
                        {isLiked
                          ? <HeartFilled style={{ color: '#E8580A' }} />
                          : <HeartOutlined />}
                        {moment.likes.length > 0 && <span>{moment.likes.length}</span>}
                      </span>
                      <span className="moment-action" onClick={() =>
                        setShowCommentInput(prev => ({ ...prev, [moment.id]: !prev[moment.id] }))
                      }>
                        <CommentOutlined />
                        {moment.comments.length > 0 && <span>{moment.comments.length}</span>}
                      </span>
                    </div>
                  </div>

                  {/* 点赞列表 */}
                  {moment.likes.length > 0 && (
                    <div className="likes-row">
                      <HeartFilled style={{ color: '#E8580A', fontSize: 12 }} />
                      {moment.likes.map(l => l.user.nickname).join('、')}
                    </div>
                  )}

                  {/* 评论列表 */}
                  {moment.comments.length > 0 && (
                    <div className="comments-list">
                      {moment.comments.map(c => (
                        <div key={c.id} className="comment-item">
                          <span className="comment-author">{c.user.nickname}：</span>
                          <span className="comment-text">{c.content}</span>
                        </div>
                      ))}
                    </div>
                  )}

                  {/* 评论输入 */}
                  {showCommentInput[moment.id] && (
                    <div className="comment-input-row">
                      <Input
                        size="small"
                        placeholder="写评论..."
                        value={commentInputs[moment.id] || ''}
                        onChange={e => setCommentInputs(prev => ({ ...prev, [moment.id]: e.target.value }))}
                        onPressEnter={() => handleComment(moment.id)}
                        className="comment-input"
                      />
                      <Button size="small" type="primary" className="comment-submit" onClick={() => handleComment(moment.id)}>
                        发送
                      </Button>
                    </div>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

export default Moments
