import React, { useState } from 'react'
import { Form, Input, Button, Tabs, Typography } from 'antd'
import { UserOutlined, LockOutlined, SmileOutlined } from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import api from '../../utils/api'
import { useChatStore } from '../../store/chatStore'
import globalMessage from '../../utils/globalMessage'
import './Login.css'

const { Title, Text } = Typography

const LoginPage: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState('login')
  const navigate = useNavigate()
  const setCurrentUser = useChatStore(s => s.setCurrentUser)
  const [loginForm] = Form.useForm()
  const [registerForm] = Form.useForm()

  const handleLogin = async (values: { username: string; password: string }) => {
    setLoading(true)
    try {
      const { data } = await api.post('/auth/login', values)
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
      setCurrentUser(data.user)
      globalMessage.success(`欢迎回来，${data.user.nickname}！`)
      navigate('/chat')
    } catch (err: any) {
      globalMessage.error(err.response?.data?.message || '登录失败')
    } finally {
      setLoading(false)
    }
  }

  const handleRegister = async (values: { username: string; nickname: string; password: string }) => {
    setLoading(true)
    try {
      const { data } = await api.post('/auth/register', values)
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
      setCurrentUser(data.user)
      globalMessage.success('注册成功，欢迎加入 FenChat！')
      navigate('/chat')
    } catch (err: any) {
      globalMessage.error(err.response?.data?.message || '注册失败')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <div className="login-bg" />
      <div className="login-card">
        <div className="login-logo">
          <div className="logo-icon">
            <SmileOutlined />
          </div>
          <Title level={2} className="logo-title">FenChat</Title>
          <Text className="logo-subtitle">分布式即时通讯</Text>
        </div>

        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          centered
          className="login-tabs"
          items={[
            {
              key: 'login',
              label: '登录',
              children: (
                <Form form={loginForm} onFinish={handleLogin} size="large" className="auth-form">
                  <Form.Item name="username" rules={[{ required: true, message: '请输入用户名' }]}>
                    <Input prefix={<UserOutlined />} placeholder="用户名" className="auth-input" />
                  </Form.Item>
                  <Form.Item name="password" rules={[{ required: true, message: '请输入密码' }]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="密码" className="auth-input" />
                  </Form.Item>
                  <Form.Item>
                    <Button
                      type="primary"
                      htmlType="submit"
                      loading={loading}
                      block
                      className="auth-btn"
                    >
                      登录
                    </Button>
                  </Form.Item>
                  <div className="switch-hint">
                    还没有账号？<span onClick={() => setActiveTab('register')}>立即注册</span>
                  </div>
                </Form>
              )
            },
            {
              key: 'register',
              label: '注册',
              children: (
                <Form form={registerForm} onFinish={handleRegister} size="large" className="auth-form">
                  <Form.Item name="username" rules={[
                    { required: true, message: '请输入用户名' },
                    { min: 3, message: '用户名至少3个字符' },
                    { pattern: /^[a-zA-Z0-9_]+$/, message: '只允许字母数字下划线' }
                  ]}>
                    <Input prefix={<UserOutlined />} placeholder="用户名（登录用）" className="auth-input" />
                  </Form.Item>
                  <Form.Item name="nickname" rules={[{ required: true, message: '请输入昵称' }]}>
                    <Input prefix={<SmileOutlined />} placeholder="昵称（展示用）" className="auth-input" />
                  </Form.Item>
                  <Form.Item name="password" rules={[
                    { required: true, message: '请输入密码' },
                    { min: 6, message: '密码至少6个字符' }
                  ]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="密码" className="auth-input" />
                  </Form.Item>
                  <Form.Item name="confirm" dependencies={['password']} rules={[
                    { required: true, message: '请确认密码' },
                    ({ getFieldValue }) => ({
                      validator(_, value) {
                        if (!value || getFieldValue('password') === value) return Promise.resolve()
                        return Promise.reject(new Error('两次密码不一致'))
                      }
                    })
                  ]}>
                    <Input.Password prefix={<LockOutlined />} placeholder="确认密码" className="auth-input" />
                  </Form.Item>
                  <Form.Item>
                    <Button
                      type="primary"
                      htmlType="submit"
                      loading={loading}
                      block
                      className="auth-btn"
                    >
                      注册
                    </Button>
                  </Form.Item>
                  <div className="switch-hint">
                    已有账号？<span onClick={() => setActiveTab('login')}>立即登录</span>
                  </div>
                </Form>
              )
            }
          ]}
        />
      </div>
    </div>
  )
}

export default LoginPage
