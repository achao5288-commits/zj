import React, { useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider, App as AntdApp } from 'antd'
import zhCN from 'antd/locale/zh_CN'
import LoginPage from './pages/Login'
import MainPage from './pages/Main'
import { setGlobalMessage } from './utils/globalMessage'
import './index.css'

// 桥接组件：将 antd App 上下文中的 message API 注入全局单例
const AppMessageBridge: React.FC = () => {
  const { message } = AntdApp.useApp()
  useEffect(() => { setGlobalMessage(message) }, [message])
  return null
}

const theme = {
  token: {
    colorPrimary: '#E8580A',
    colorLink: '#E8580A',
    colorLinkHover: '#FF8C42',
    borderRadius: 8,
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", sans-serif',
  }
}

const PrivateRoute: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/login" replace />
}

function App() {
  return (
    <ConfigProvider theme={theme} locale={zhCN}>
      <AntdApp>
        <AppMessageBridge />
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/chat" element={
            <PrivateRoute>
              <MainPage />
            </PrivateRoute>
          } />
          <Route path="/" element={<Navigate to="/chat" replace />} />
        </Routes>
      </BrowserRouter>
      </AntdApp>
    </ConfigProvider>
  )
}

export default App
