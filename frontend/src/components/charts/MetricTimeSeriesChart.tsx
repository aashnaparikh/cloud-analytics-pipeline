import {
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip,
  ResponsiveContainer,
} from 'recharts'
import { format } from 'date-fns'
import type { TimeSeriesPoint } from '../../types'

interface Props {
  data: TimeSeriesPoint[]
  metricName: string
}

const CustomTooltip = ({ active, payload, label }: any) => {
  if (!active || !payload?.length) return null
  return (
    <div className="card-sm text-xs space-y-1 shadow-xl">
      <p className="text-slate-400">{label}</p>
      <p className="text-emerald-400 font-medium">
        Value: <span className="text-white">{Number(payload[0]?.value).toFixed(3)}</span>
      </p>
    </div>
  )
}

export default function MetricTimeSeriesChart({ data, metricName }: Props) {
  const formatted = data.map((d) => ({
    time: format(new Date(d.timestamp), 'HH:mm'),
    value: d.value ?? 0,
  }))

  return (
    <div className="card">
      <h2 className="text-sm font-semibold text-slate-300 mb-4">
        {metricName} — Time Series
      </h2>
      <ResponsiveContainer width="100%" height={200}>
        <LineChart data={formatted} margin={{ top: 5, right: 10, left: -20, bottom: 0 }}>
          <CartesianGrid strokeDasharray="3 3" stroke="#334155" vertical={false} />
          <XAxis dataKey="time" tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
          <YAxis tick={{ fill: '#64748b', fontSize: 11 }} axisLine={false} tickLine={false} />
          <Tooltip content={<CustomTooltip />} />
          <Line
            type="monotone"
            dataKey="value"
            stroke="#10b981"
            strokeWidth={2}
            dot={false}
            activeDot={{ r: 4, fill: '#10b981' }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  )
}
