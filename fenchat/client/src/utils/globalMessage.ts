import type { MessageInstance } from 'antd/es/message/interface'

// 全局消息 API 单例（由 AppMessageBridge 初始化）
let _message: MessageInstance | null = null

export const setGlobalMessage = (api: MessageInstance) => { _message = api }

export const globalMessage = {
  success: (content: string) => _message?.success(content),
  error: (content: string) => _message?.error(content),
  warning: (content: string) => _message?.warning(content),
  info: (content: string) => _message?.info(content),
  loading: (content: string) => _message?.loading(content),
}

export default globalMessage
