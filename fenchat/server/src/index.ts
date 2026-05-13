import express from 'express'
import { createServer } from 'http'
import { Server } from 'socket.io'
import cors from 'cors'
import dotenv from 'dotenv'
import path from 'path'

import authRouter from './routes/auth'
import friendsRouter from './routes/friends'
import messagesRouter from './routes/messages'
import groupsRouter from './routes/groups'
import momentsRouter from './routes/moments'
import { setupSocket } from './socket'

dotenv.config()

const app = express()
const httpServer = createServer(app)

const CORS_ORIGIN = process.env.CLIENT_URL || 'http://localhost:5173'
const corsOptions = {
  origin: (origin: string | undefined, callback: (err: Error | null, allow?: boolean) => void) => {
    // 允许所有 localhost 来源（开发环境）
    if (!origin || origin.includes('localhost')) {
      callback(null, true)
    } else {
      callback(new Error('Not allowed by CORS'))
    }
  },
  credentials: true
}

const io = new Server(httpServer, {
  cors: { origin: (origin, cb) => cb(null, true), methods: ['GET', 'POST'], credentials: true }
})

// 中间件
app.use(cors(corsOptions))
app.use(express.json())
app.use(express.urlencoded({ extended: true }))

// 静态文件（上传目录）
app.use('/uploads', express.static(path.join(__dirname, '../uploads')))

// API 路由
app.use('/api/auth', authRouter)
app.use('/api/friends', friendsRouter)
app.use('/api/messages', messagesRouter)
app.use('/api/groups', groupsRouter)
app.use('/api/moments', momentsRouter)

// 健康检查
app.get('/health', (_, res) => res.json({ status: 'ok', time: new Date().toISOString() }))

// 文件上传路由
import multer from 'multer'
import { v4 as uuidv4 } from 'uuid'
import { authMiddleware, AuthRequest } from './middleware/auth'

const storage = multer.diskStorage({
  destination: (_, __, cb) => cb(null, path.join(__dirname, '../uploads')),
  filename: (_, file, cb) => {
    const ext = path.extname(file.originalname)
    cb(null, uuidv4() + ext)
  }
})
const upload = multer({ storage, limits: { fileSize: 50 * 1024 * 1024 } }) // 50MB

app.post('/api/upload', authMiddleware, upload.single('file'), (req: AuthRequest, res) => {
  if (!req.file) return res.status(400).json({ message: '未上传文件' })
  const url = `${req.protocol}://${req.get('host')}/uploads/${req.file.filename}`
  return res.json({ url, filename: req.file.originalname, size: req.file.size })
})

// 初始化 Socket.IO
setupSocket(io)

const PORT = process.env.PORT || 3001
httpServer.listen(PORT, () => {
  console.log(`FenChat 服务器运行在 http://localhost:${PORT}`)
})

export default app
