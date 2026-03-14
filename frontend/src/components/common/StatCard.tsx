import clsx from 'clsx'
import type { LucideIcon } from 'lucide-react'

interface StatCardProps {
  label: string
  value: string | number
  subtext?: string
  icon: LucideIcon
  trend?: { value: number; label: string }
  color?: 'blue' | 'green' | 'amber' | 'red' | 'purple'
}

const colorMap = {
  blue:   { icon: 'text-brand-400 bg-brand-400/10',   badge: 'text-brand-400 bg-brand-400/10' },
  green:  { icon: 'text-emerald-400 bg-emerald-400/10', badge: 'text-emerald-400 bg-emerald-400/10' },
  amber:  { icon: 'text-amber-400 bg-amber-400/10',   badge: 'text-amber-400 bg-amber-400/10' },
  red:    { icon: 'text-red-400 bg-red-400/10',       badge: 'text-red-400 bg-red-400/10' },
  purple: { icon: 'text-purple-400 bg-purple-400/10', badge: 'text-purple-400 bg-purple-400/10' },
}

export default function StatCard({
  label, value, subtext, icon: Icon, trend, color = 'blue',
}: StatCardProps) {
  const colors = colorMap[color]

  return (
    <div className="stat-card animate-slide-up">
      <div className="flex items-center justify-between">
        <span className="text-sm text-slate-400 font-medium">{label}</span>
        <div className={clsx('w-8 h-8 rounded-lg flex items-center justify-center', colors.icon)}>
          <Icon className="w-4 h-4" />
        </div>
      </div>

      <p className="text-3xl font-bold text-white tracking-tight">
        {typeof value === 'number' ? value.toLocaleString() : value}
      </p>

      <div className="flex items-center justify-between mt-1">
        {subtext && <p className="text-xs text-slate-500">{subtext}</p>}
        {trend && (
          <span className={clsx('badge', colors.badge)}>
            {trend.value > 0 ? '+' : ''}{trend.value}% {trend.label}
          </span>
        )}
      </div>
    </div>
  )
}
