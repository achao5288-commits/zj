import { Router, Response } from 'express'
import prisma from '../config/prisma'
import { authMiddleware, AuthRequest } from '../middleware/auth'

const router = Router()

// 创建群组
router.post('/', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { name, memberIds } = req.body
    const group = await prisma.group.create({
      data: {
        name,
        creatorId: req.userId!,
        members: {
          create: [
            { userId: req.userId!, role: 'owner' },
            ...((memberIds as string[]) || []).map((id: string) => ({ userId: id, role: 'member' }))
          ]
        }
      },
      include: { members: { include: { user: { select: { id: true, nickname: true, avatar: true } } } } }
    })
    return res.status(201).json(group)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取我的群组列表
router.get('/mine', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const groups = await prisma.groupMember.findMany({
      where: { userId: req.userId },
      include: {
        group: {
          include: { members: { include: { user: { select: { id: true, nickname: true, avatar: true } } } } }
        }
      }
    })
    return res.json(groups.map(g => g.group))
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取群组详情
router.get('/:groupId', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const group = await prisma.group.findUnique({
      where: { id: req.params.groupId },
      include: { members: { include: { user: { select: { id: true, nickname: true, avatar: true, status: true } } } } }
    })
    if (!group) return res.status(404).json({ message: '群组不存在' })
    return res.json(group)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 邀请成员
router.post('/:groupId/invite', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { userIds } = req.body
    await prisma.groupMember.createMany({
      data: (userIds as string[]).map((uid: string) => ({ groupId: req.params.groupId, userId: uid })),
      skipDuplicates: true
    })
    return res.json({ message: '邀请成功' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 退出群组
router.delete('/:groupId/leave', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    await prisma.groupMember.deleteMany({
      where: { groupId: req.params.groupId, userId: req.userId }
    })
    return res.json({ message: '已退出群组' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 更新群公告
router.put('/:groupId/announcement', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { announcement } = req.body
    await prisma.group.update({
      where: { id: req.params.groupId },
      data: { announcement }
    })
    return res.json({ message: '公告已更新' })
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

export default router
