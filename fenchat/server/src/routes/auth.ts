import { Router, Request, Response } from 'express'
import bcrypt from 'bcryptjs'
import jwt from 'jsonwebtoken'
import prisma from '../config/prisma'
import redis from '../config/redis'
import { authMiddleware, AuthRequest } from '../middleware/auth'

const router = Router()

// 注册
router.post('/register', async (req: Request, res: Response) => {
  try {
    const { username, nickname, password } = req.body
    if (!username || !nickname || !password) {
      return res.status(400).json({ message: '请填写所有字段' })
    }
    const exists = await prisma.user.findUnique({ where: { username } })
    if (exists) return res.status(409).json({ message: '用户名已存在' })

    const hashed = await bcrypt.hash(password, 10)
    const user = await prisma.user.create({
      data: { username, nickname, password: hashed }
    })

    const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET!, { expiresIn: '7d' })
    return res.status(201).json({
      token,
      user: { id: user.id, username: user.username, nickname: user.nickname, avatar: user.avatar }
    })
  } catch (err) {
    console.error(err)
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 登录
router.post('/login', async (req: Request, res: Response) => {
  try {
    const { username, password } = req.body
    const user = await prisma.user.findUnique({ where: { username } })
    if (!user) return res.status(404).json({ message: '用户不存在' })

    const match = await bcrypt.compare(password, user.password)
    if (!match) return res.status(401).json({ message: '密码错误' })

    const token = jwt.sign({ userId: user.id }, process.env.JWT_SECRET!, { expiresIn: '7d' })
    return res.json({
      token,
      user: { id: user.id, username: user.username, nickname: user.nickname, avatar: user.avatar, bio: user.bio }
    })
  } catch (err) {
    console.error(err)
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 获取当前用户信息
router.get('/me', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const user = await prisma.user.findUnique({
      where: { id: req.userId },
      select: { id: true, username: true, nickname: true, avatar: true, bio: true, status: true, createdAt: true }
    })
    return res.json(user)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 更新个人资料
router.put('/profile', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { nickname, bio, avatar } = req.body
    const user = await prisma.user.update({
      where: { id: req.userId },
      data: { nickname, bio, avatar },
      select: { id: true, username: true, nickname: true, avatar: true, bio: true }
    })
    return res.json(user)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

// 搜索用户
router.get('/search', authMiddleware, async (req: AuthRequest, res: Response) => {
  try {
    const { q } = req.query
    const users = await prisma.user.findMany({
      where: {
        OR: [
          { username: { contains: q as string } },
          { nickname: { contains: q as string } }
        ],
        NOT: { id: req.userId }
      },
      select: { id: true, username: true, nickname: true, avatar: true, status: true },
      take: 20
    })
    return res.json(users)
  } catch {
    return res.status(500).json({ message: '服务器错误' })
  }
})

export default router
