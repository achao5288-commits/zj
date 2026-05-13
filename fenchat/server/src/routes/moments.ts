import { Router, Response } from 'express'
import prisma from '../config/prisma'
import { authMiddleware, AuthRequest } from '../middleware/auth'

const router = Router()

// 发布动态
router.post('/', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { content, images } = req.body
    const moment = await prisma.moment.create({
      data: {
        content,
        images: images ? JSON.stringify(images) : null,
        userId: req.userId!
      },
      include: {
        user: { select: { id: true, nickname: true, avatar: true } },
        likes: true,
        comments: { include: { user: { select: { id: true, nickname: true, avatar: true } } } }
      }
    })
    return res.status(201).json(moment)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取朋友圈动态（好友的）
router.get('/', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { page = '1', limit = '20' } = req.query
    const skip = (parseInt(page as string) - 1) * parseInt(limit as string)

    // 获取好友ID列表
    const friends = await prisma.friend.findMany({
      where: { OR: [{ userAId: req.userId }, { userBId: req.userId }] }
    })
    const friendIds = friends.map(f => f.userAId === req.userId ? f.userBId : f.userAId)
    friendIds.push(req.userId!) // 包含自己

    const moments = await prisma.moment.findMany({
      where: { userId: { in: friendIds } },
      include: {
        user: { select: { id: true, nickname: true, avatar: true } },
        likes: { include: { user: { select: { id: true, nickname: true } } } },
        comments: { include: { user: { select: { id: true, nickname: true, avatar: true } } }, orderBy: { createdAt: 'asc' } }
      },
      orderBy: { createdAt: 'desc' },
      skip,
      take: parseInt(limit as string)
    })
    return res.json(moments)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 点赞/取消点赞
router.post('/:momentId/like', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const existing = await prisma.momentLike.findUnique({
      where: { momentId_userId: { momentId: req.params.momentId, userId: req.userId! } }
    })
    if (existing) {
      await prisma.momentLike.delete({ where: { id: existing.id } })
      return res.json({ liked: false })
    } else {
      await prisma.momentLike.create({ data: { momentId: req.params.momentId, userId: req.userId! } })
      return res.json({ liked: true })
    }
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 发表评论
router.post('/:momentId/comment', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { content } = req.body
    const comment = await prisma.momentComment.create({
      data: { content, momentId: req.params.momentId, userId: req.userId! },
      include: { user: { select: { id: true, nickname: true, avatar: true } } }
    })
    return res.status(201).json(comment)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 删除动态
router.delete('/:momentId', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const moment = await prisma.moment.findUnique({ where: { id: req.params.momentId } })
    if (!moment || moment.userId !== req.userId) return res.status(403).json({ message: '无权操作' })
    await prisma.moment.delete({ where: { id: req.params.momentId } })
    return res.json({ message: '已删除' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

export default router
