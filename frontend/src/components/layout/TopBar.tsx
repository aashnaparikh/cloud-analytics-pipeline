import { useLocation } from 'react-router-dom'
import { RefreshCw } from 'lucide-react'
import { useQueryClient } from '@tanstack/react-query'
import { useAuthStore } from '../../store/authStore'

const PAGE_TITLES: Record<string, string> = {
  '/dashboard': 'Overview Dashboard',
  '/events':    'Event Stream',
  '/metrics':   'Metrics Explorer',
}

export default function TopBar() {
  const { pathname } = useLocation()
  const queryClient = useQueryClient()
  const user = useAuthStore((s) => s.user)

  return (
    <header className="flex items-center justify-between px-6 py-3.5 border-b border-surface-border bg-surface-card/50 backdrop-blur-sm">
      <div>
        <h1 className="text-base font-semibold text-white">
          {PAGE_TITLES[pathname] ?? 'Cloud Analytics'}
        </h1>
        <p className="text-xs text-slate-500 mt-0.5">Tenant: {user?.tenantId}</p>
      </div>

      <div className="flex items-center gap-3">
        <div className="flex items-center gap-1.5">
          <span className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse-slow" />
          <span className="text-xs text-slate-400">Live</span>
        </div>

        <button
          onClick={() => queryClient.invalidateQueries()}
          className="btn-ghost text-xs py-1.5 px-3"
          title="Refresh all data"
        >
          <RefreshCw className="w-3.5 h-3.5" />
          Refresh
        </button>
      </div>
    </header>
  )
}
