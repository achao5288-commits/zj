import { Router, Response } from 'express'
import prisma from '../config/prisma'
import { authMiddleware, AuthRequest } from '../middleware/auth'

const router = Router()

// 获取私聊历史消息
router.get('/private/:friendId', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { friendId } = req.params
    const { page = '1', limit = '50' } = req.query
    const skip = (parseInt(page as string) - 1) * parseInt(limit as string)

    const messages = await prisma.message.findMany({
      where: {
        OR: [
          { fromId: req.userId, toId: friendId },
          { fromId: friendId, toId: req.userId }
        ],
        groupId: null
      },
      include: { from: { select: { id: true, nickname: true, avatar: true } } },
      orderBy: { createdAt: 'asc' },
      skip,
      take: parseInt(limit as string)
    })

    // 标记已读
    await prisma.message.updateMany({
      where: { fromId: friendId, toId: req.userId, isRead: false },
      data: { isRead: true }
    })

    return res.json(messages)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取群聊历史消息
router.get('/group/:groupId', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { groupId } = req.params
    const { page = '1', limit = '50' } = req.query
    const skip = (parseInt(page as string) - 1) * parseInt(limit as string)

    const messages = await prisma.message.findMany({
      where: { groupId },
      include: { from: { select: { id: true, nickname: true, avatar: true } } },
      orderBy: { createdAt: 'asc' },
      skip,
      take: parseInt(limit as string)
    })
    return res.json(messages)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 撤回消息
router.put('/recall/:msgId', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { msgId } = req.params
    const msg = await prisma.message.findUnique({ where: { id: msgId } })
    if (!msg) return res.status(404).json({ message: '消息不存在' })
    if (msg.fromId !== req.userId) return res.status(403).json({ message: '无权操作' })

    const diff = Date.now() - new Date(msg.createdAt).getTime()
    if (diff > 2 * 60 * 1000) return res.status(400).json({ message: '超过2分钟无法撤回' })

    await prisma.message.update({ where: { id: msgId }, data: { isRecalled: true, content: '该消息已被撤回' } })
    return res.json({ message: '撤回成功' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取未读消息数
router.get('/unread', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const unread = await prisma.message.groupBy({
      by: ['fromId'],
      where: { toId: req.userId, isRead: false },
      _count: { id: true }
    })
    const result: Record<string, number> = {}
    unread.forEach(u => { result[u.fromId] = u._count.id })
    return res.json(result)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

export default router
