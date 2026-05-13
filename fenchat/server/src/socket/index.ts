import { Server, Socket } from 'socket.io'
import jwt from 'jsonwebtoken'
import prisma from '../config/prisma'
import redis from '../config/redis'

interface AuthSocket extends Socket {
  userId?: string
  nickname?: string
}

export const setupSocket = (io: Server) => {
  // JWT 认证中间件
  io.use(async (socket: AuthSocket, next) => {
    const token = socket.handshake.auth.token
    if (!token) return next(new Error('未授权'))
    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET!) as { userId: string }
      socket.userId = decoded.userId
      const user = await prisma.user.findUnique({ where: { id: decoded.userId }, select: { nickname: true } })
      socket.nickname = user?.nickname
      next()
    } catch {
      next(new Error('Token无效'))
    }
  })

  io.on('connection', async (socket: AuthSocket) => {
    const userId = socket.userId!
    console.log(`用户 ${socket.nickname}(${userId}) 已连接`)

    // 存储在线状态到 Redis/内存
    await redis.set(`online:${userId}`, socket.id, 'EX', 86400)
    await prisma.user.update({ where: { id: userId }, data: { status: 'online' } })

    // 广播用户上线
    socket.broadcast.emit('userOnline', { userId })

    // 加入所有群组房间
    const groupMembers = await prisma.groupMember.findMany({
      where: { userId },
      select: { groupId: true }
    })
    groupMembers.forEach(gm => socket.join(`group:${gm.groupId}`))

    // 推送离线消息
    const offlineMsgs = await prisma.message.findMany({
      where: { toId: userId, isRead: false },
      include: { from: { select: { id: true, nickname: true, avatar: true } } },
      orderBy: { createdAt: 'asc' }
    })
    if (offlineMsgs.length > 0) {
      socket.emit('offlineMessages', offlineMsgs)
    }

    // ======== 私聊消息 ========
    socket.on('sendPrivateMessage', async (data: {
      toId: string
      content: string
      type?: string
      fileUrl?: string
      fileName?: string
      fileSize?: number
    }) => {
      try {
        const msg = await prisma.message.create({
          data: {
            fromId: userId,
            toId: data.toId,
            content: data.content,
            type: data.type || 'text',
            fileUrl: data.fileUrl,
            fileName: data.fileName,
            fileSize: data.fileSize
          },
          include: { from: { select: { id: true, nickname: true, avatar: true } } }
        })

        // 回执给发送方
        socket.emit('messageSent', msg)

        // 推送给接收方
        const targetSocketId = await redis.get(`online:${data.toId}`)
        if (targetSocketId) {
          io.to(targetSocketId).emit('receivePrivateMessage', msg)
        }
      } catch (err) {
        console.error('发送消息失败:', err)
        socket.emit('messageError', { message: '发送失败' })
      }
    })

    // ======== 群聊消息 ========
    socket.on('sendGroupMessage', async (data: {
      groupId: string
      content: string
      type?: string
      fileUrl?: string
    }) => {
      try {
        // 检查是否是群成员
        const member = await prisma.groupMember.findUnique({
          where: { groupId_userId: { groupId: data.groupId, userId } }
        })
        if (!member) return socket.emit('messageError', { message: '你不是该群成员' })

        // 确保发送者的 Socket 已加入该群 Room（新建群后可能未加入）
        socket.join(`group:${data.groupId}`)

        const msg = await prisma.message.create({
          data: {
            fromId: userId,
            groupId: data.groupId,
            content: data.content,
            type: data.type || 'text',
            fileUrl: data.fileUrl
          },
          include: { from: { select: { id: true, nickname: true, avatar: true } } }
        })

        // 广播给群内所有成员
        io.to(`group:${data.groupId}`).emit('receiveGroupMessage', msg)
      } catch (err) {
        console.error('群聊消息失败:', err)
        socket.emit('messageError', { message: '发送失败' })
      }
    })

    // ======== 主动加入群组 Room（创建群/被邀请后调用）========
    socket.on('joinGroup', async (data: { groupId: string }) => {
      try {
        // 验证是否为群成员
        const member = await prisma.groupMember.findUnique({
          where: { groupId_userId: { groupId: data.groupId, userId } }
        })
        if (member) {
          socket.join(`group:${data.groupId}`)
          console.log(`用户 ${socket.nickname} 加入群组 room: ${data.groupId}`)
        }
      } catch (err) {
        console.error('加入群组失败:', err)
      }
    })

    // ======== 消息撤回 ========
    socket.on('recallMessage', async (data: { msgId: string }) => {
      try {
        const msg = await prisma.message.findUnique({ where: { id: data.msgId } })
        if (!msg || msg.fromId !== userId) return

        const diff = Date.now() - new Date(msg.createdAt).getTime()
        if (diff > 2 * 60 * 1000) return socket.emit('messageError', { message: '超过2分钟无法撤回' })

        await prisma.message.update({
          where: { id: data.msgId },
          data: { isRecalled: true, content: '该消息已被撤回' }
        })

        if (msg.toId) {
          const targetSocketId = await redis.get(`online:${msg.toId}`)
          if (targetSocketId) io.to(targetSocketId).emit('messageRecalled', { msgId: data.msgId })
          socket.emit('messageRecalled', { msgId: data.msgId })
        } else if (msg.groupId) {
          io.to(`group:${msg.groupId}`).emit('messageRecalled', { msgId: data.msgId })
        }
      } catch (err) {
        console.error('撤回失败:', err)
      }
    })

    // ======== 正在输入状态 ========
    socket.on('typing', async (data: { toId?: string; groupId?: string }) => {
      if (data.toId) {
        const targetSocketId = await redis.get(`online:${data.toId}`)
        if (targetSocketId) io.to(targetSocketId).emit('userTyping', { fromId: userId })
      } else if (data.groupId) {
        socket.to(`group:${data.groupId}`).emit('userTyping', { fromId: userId, groupId: data.groupId })
      }
    })

    // ======== 标记消息已读 ========
    socket.on('markRead', async (data: { fromId: string }) => {
      await prisma.message.updateMany({
        where: { fromId: data.fromId, toId: userId, isRead: false },
        data: { isRead: true }
      })
      const targetSocketId = await redis.get(`online:${data.fromId}`)
      if (targetSocketId) io.to(targetSocketId).emit('messagesRead', { byId: userId })
    })

    // ======== 断线处理 ========
    socket.on('disconnect', async () => {
      await redis.del(`online:${userId}`)
      await prisma.user.update({ where: { id: userId }, data: { status: 'offline' } })
      socket.broadcast.emit('userOffline', { userId })
      console.log(`用户 ${socket.nickname}(${userId}) 已离线`)
    })
  })
}
