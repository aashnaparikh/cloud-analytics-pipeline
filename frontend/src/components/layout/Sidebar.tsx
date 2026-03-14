import { NavLink } from 'react-router-dom'
import { BarChart3, Activity, Zap, LogOut } from 'lucide-react'
import { useAuthStore } from '../../store/authStore'
import clsx from 'clsx'

const navItems = [
  { to: '/dashboard', icon: BarChart3, label: 'Dashboard' },
  { to: '/events',    icon: Zap,       label: 'Events' },
  { to: '/metrics',   icon: Activity,  label: 'Metrics' },
]

export default function Sidebar() {
  const { user, logout } = useAuthStore()

  return (
    <aside className="flex flex-col w-56 bg-surface-card border-r border-surface-border shrink-0">
      {/* Logo */}
      <div className="flex items-center gap-3 px-5 py-5 border-b border-surface-border">
        <div className="w-8 h-8 rounded-lg bg-brand-600 flex items-center justify-center">
          <BarChart3 className="w-4 h-4 text-white" />
        </div>
        <div>
          <p className="text-sm font-semibold text-white leading-none">CloudAnalytics</p>
          <p className="text-[10px] text-slate-500 mt-0.5 uppercase tracking-wider">Pipeline</p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1">
        {navItems.map(({ to, icon: Icon, label }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              clsx(
                'flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all duration-150',
                isActive
                  ? 'bg-brand-600/20 text-brand-400 border border-brand-600/30'
                  : 'text-slate-400 hover:text-slate-100 hover:bg-surface-border',
              )
            }
          >
            <Icon className="w-4 h-4 shrink-0" />
            {label}
          </NavLink>
        ))}
      </nav>

      {/* User footer */}
      <div className="px-3 py-4 border-t border-surface-border space-y-1">
        <div className="flex items-center gap-3 px-3 py-2 mb-1">
          <div className="w-7 h-7 rounded-full bg-brand-700 flex items-center justify-center text-xs font-bold text-white shrink-0">
            {user?.firstName?.[0]}{user?.lastName?.[0]}
          </div>
          <div className="min-w-0">
            <p className="text-xs font-medium text-slate-200 truncate">
              {user?.firstName} {user?.lastName}
            </p>
            <p className="text-[10px] text-slate-500 uppercase tracking-wider">{user?.role}</p>
          </div>
        </div>
        <button
          onClick={logout}
          className="flex items-center gap-3 w-full px-3 py-2 rounded-lg text-sm text-slate-400
                     hover:text-red-400 hover:bg-red-400/10 transition-all duration-150"
        >
          <LogOut className="w-4 h-4 shrink-0" />
          Sign Out
        </button>
      </div>
    </aside>
  )
}
