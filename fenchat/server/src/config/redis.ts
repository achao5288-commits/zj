import Redis from 'ioredis'
import dotenv from 'dotenv'
dotenv.config()

// 内存降级 Map（Redis 不可用时使用）
const memoryStore = new Map<string, string>()

let redis: Redis | null = null
let useMemory = false

try {
  redis = new Redis(process.env.REDIS_URL || 'redis://localhost:6379', {
    lazyConnect: true,
    connectTimeout: 3000,
    maxRetriesPerRequest: 1,
    retryStrategy: () => null // 不重试
  })

  redis.on('connect', () => {
    useMemory = false
    console.log('✅ Redis 已连接')
  })

  redis.on('error', () => {
    if (!useMemory) {
      useMemory = true
      console.warn('⚠️  Redis 不可用，已切换为内存模式（在线状态不支持多节点同步）')
    }
  })

  redis.connect().catch(() => {
    useMemory = true
    console.warn('⚠️  Redis 连接失败，使用内存模式')
  })
} catch {
  useMemory = true
  console.warn('⚠️  Redis 初始化失败，使用内存模式')
}

// 统一接口：优先 Redis，降级内存
export const redisStore = {
  async set(key: string, value: string, mode?: string, ttl?: number): Promise<void> {
    if (!useMemory && redis) {
      try {
        if (mode === 'EX' && ttl) await redis.set(key, value, 'EX', ttl)
        else await redis.set(key, value)
        return
      } catch { useMemory = true }
    }
    memoryStore.set(key, value)
  },

  async get(key: string): Promise<string | null> {
    if (!useMemory && redis) {
      try { return await redis.get(key) } catch { useMemory = true }
    }
    return memoryStore.get(key) || null
  },

  async del(key: string): Promise<void> {
    if (!useMemory && redis) {
      try { await redis.del(key); return } catch { useMemory = true }
    }
    memoryStore.delete(key)
  }
}

export default redisStore
