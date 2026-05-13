import { Router, Response } from 'express'
import prisma from '../config/prisma'
import { authMiddleware, AuthRequest } from '../middleware/auth'

const router = Router()

// 获取好友列表
router.get('/', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const friends = await prisma.friend.findMany({
      where: { OR: [{ userAId: req.userId }, { userBId: req.userId }] },
      include: {
        userA: { select: { id: true, username: true, nickname: true, avatar: true, status: true } },
        userB: { select: { id: true, username: true, nickname: true, avatar: true, status: true } }
      }
    })
    const list = friends.map(f => f.userAId === req.userId ? f.userB : f.userA)
    return res.json(list)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 发送好友申请
router.post('/request', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { toId, message } = req.body
    if (toId === req.userId) return res.status(400).json({ message: '不能添加自己' })

    // 检查是否已是好友
    const alreadyFriend = await prisma.friend.findFirst({
      where: { OR: [{ userAId: req.userId, userBId: toId }, { userAId: toId, userBId: req.userId }] }
    })
    if (alreadyFriend) return res.status(409).json({ message: '已是好友' })

    // 检查是否已有申请
    const existingReq = await prisma.friendRequest.findFirst({
      where: { fromId: req.userId, toId, status: 'pending' }
    })
    if (existingReq) return res.status(409).json({ message: '申请已发送' })

    const request = await prisma.friendRequest.create({
      data: { fromId: req.userId!, toId, message },
      include: { from: { select: { id: true, username: true, nickname: true, avatar: true } } }
    })
    return res.status(201).json(request)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取好友申请列表
router.get('/requests', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const requests = await prisma.friendRequest.findMany({
      where: { toId: req.userId, status: 'pending' },
      include: { from: { select: { id: true, username: true, nickname: true, avatar: true } } },
      orderBy: { createdAt: 'desc' }
    })
    return res.json(requests)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 处理好友申请 (accept / reject)
router.put('/request/:id', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { id } = req.params
    const { action } = req.body // 'accept' | 'reject'
    const request = await prisma.friendRequest.findUnique({ where: { id } })
    if (!request || request.toId !== req.userId) return res.status(404).json({ message: '申请不存在' })

    await prisma.friendRequest.update({ where: { id }, data: { status: action === 'accept' ? 'accepted' : 'rejected' } })

    if (action === 'accept') {
      await prisma.friend.create({ data: { userAId: request.fromId, userBId: req.userId! } })
    }
    return res.json({ message: action === 'accept' ? '已添加好友' : '已拒绝' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 删除好友
router.delete('/:friendId', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { friendId } = req.params
    await prisma.friend.deleteMany({
      where: { OR: [{ userAId: req.userId, userBId: friendId }, { userAId: friendId, userBId: req.userId }] }
    })
    return res.json({ message: '已删除好友' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

export default router
