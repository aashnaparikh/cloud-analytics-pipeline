import {
  AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer,
} from 'recharts'
import { format } from 'date-fns'
import type { TimeSeriesPoint } from '../../types'

interface Props {
  data: TimeSeriesPoint[]
  title?: string
}

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null
  return (
    <div className="card-sm text-xs space-y-1 shadow-xl">
      <p className="text-slate-400">{label}</p>
      {payload.map((entry: any) => (
        <p key={entry.name} style={{ color: entry.color }} className="font-medium">
          {entry.name}: <span className="text-white">{entry.value?.toLocaleString()}</span>
        </p>
      ))}
    </div>
  )
}

export default function EventTimeSeriesChart({ data, title = 'Events per Hour' }: Props) {
  const formatted = data.map((d) => ({
    ...d,
    time: format(new Date(d.timestamp), 'HH:mm'),
    count: d.count ?? 0,
  }))

  return (
    <div className="card">
      <h2 className="text-sm font-semibold text-slate-300 mb-4">{title}</h2>
      <ResponsiveContainer width="100%" height={220}>
        <AreaChart data={formatted} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
          <defs>
            <linearGradient id="eventGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%"  stopColor="#3b82f6" stopOpacity={0.3} />
              <stop offset="95%" stopColor="#3b82f6" stopOpacity={0} />
            </linearGradient>
          </defs>
          <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
          <XAxis dataKey="time" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
          <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
          <Tooltip content={<CustomTooltip />} />
          <Area
            type="monotone"
            dataKey="count"
            name="Events"
            stroke="#3b82f6"
            strokeWidth={2}
            fill="url(#eventGradient)"
            dot={false}
            activeDot={{ r: 4, fill: '#3b82f6' }}
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
