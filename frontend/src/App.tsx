import { useState, useEffect } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { authService } from './services/authService'
import { useAuthStore } from './store/authStore'
import DashboardPage from './pages/DashboardPage'
import EventsPage from './pages/EventsPage'
import MetricsPage from './pages/MetricsPage'
import Layout from './components/layout/Layout'
import LoadingSpinner from './components/common/LoadingSpinner'

export default function App() {
  const { token, login } = useAuthStore()
  const [ready, setReady] = useState(!!token)

  useEffect(() => {
    if (token) return
    authService
      .login('admin@demo.com', 'Demo1234')
      .then((res) => {
        login(res.token, {
          userId: res.userId,
          email: res.email,
          firstName: res.firstName,
          lastName: res.lastName,
          role: res.role,
          tenantId: res.tenantId,
        })
      })
      .finally(() => setReady(true))
  }, [])

  if (!ready) {
    return (
      <div className="min-h-screen bg-surface flex items-center justify-center">
        <LoadingSpinner className="h-16" />
      </div>
    )
  }

  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="events" element={<EventsPage />} />
        <Route path="metrics" element={<MetricsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}
