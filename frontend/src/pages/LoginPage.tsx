import { useState, FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { BarChart3, Eye, EyeOff, AlertCircle } from 'lucide-react'
import { authService } from '../services/authService'
import { useAuthStore } from '../store/authStore'

export default function LoginPage() {
  const navigate = useNavigate()
  const login = useAuthStore((s) => s.login)

  const [email, setEmail] = useState('admin@demo.com')
  const [password, setPassword] = useState('Demo1234')
  const [showPassword, setShowPassword] = useState(false)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await authService.login(email, password)
      login(res.token, {
        userId: res.userId,
        email: res.email,
        firstName: res.firstName,
        lastName: res.lastName,
        role: res.role,
        tenantId: res.tenantId,
      })
      navigate('/dashboard')
    } catch {
      setError('Invalid email or password. Try admin@demo.com / Demo1234')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-surface flex items-center justify-center p-4">
      {/* Background grid */}
      <div
        className="absolute inset-0 opacity-5"
        style={{
          backgroundImage: 'linear-gradient(#3b82f6 1px, transparent 1px), linear-gradient(90deg, #3b82f6 1px, transparent 1px)',
          backgroundSize: '50px 50px',
        }}
      />

      <div className="relative w-full max-w-sm">
        {/* Logo */}
        <div className="flex flex-col items-center mb-8">
          <div className="w-14 h-14 rounded-2xl bg-brand-600 flex items-center justify-center mb-4 glow">
            <BarChart3 className="w-7 h-7 text-white" />
          </div>
          <h1 className="text-2xl font-bold text-white">CloudAnalytics</h1>
          <p className="text-sm text-slate-500 mt-1">Real-time analytics pipeline</p>
        </div>

        {/* Card */}
        <div className="card glow">
          <h2 className="text-lg font-semibold text-white mb-6">Sign in</h2>

          {error && (
            <div className="flex items-start gap-2 p-3 rounded-lg bg-red-500/10 border border-red-500/20 text-red-400 text-sm mb-4">
              <AlertCircle className="w-4 h-4 shrink-0 mt-0.5" />
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">Email</label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="input"
                placeholder="you@example.com"
                required
              />
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-400 mb-1.5">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="input pr-10"
                  placeholder="••••••••"
                  required
                />
                <button
                  type="button"
                  onClick={() => setShowPassword((v) => !v)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-500 hover:text-slate-300"
                >
                  {showPassword ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full justify-center py-2.5"
            >
              {loading ? (
                <span className="flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  Signing in…
                </span>
              ) : 'Sign In'}
            </button>
          </form>

          {/* Demo credentials hint */}
          <div className="mt-4 p-3 rounded-lg bg-brand-600/10 border border-brand-600/20">
            <p className="text-xs text-slate-400 mb-1 font-medium">Demo credentials</p>
            <p className="text-xs text-slate-500 font-mono">admin@demo.com / Demo1234</p>
            <p className="text-xs text-slate-500 font-mono">analyst@demo.com / Demo1234</p>
          </div>
        </div>

        <p className="text-center text-xs text-slate-600 mt-4">
          Cloud Analytics Pipeline · Spring Boot + React · Docker + AWS
        </p>
      </div>
    </div>
  )
}
