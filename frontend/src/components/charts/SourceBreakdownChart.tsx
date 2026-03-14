import {
  PieChart, Pie, Cell, Tooltip, ResponsiveContainer, Legend,
} from 'recharts'

interface Props {
  data: Record<string, number>
  title?: string
}

const COLORS = ['#3b82f6', '#8b5cf6', '#10b981', '#f59e0b', '#ef4444', '#06b6d4']

const CustomTooltip = ({ active, payload }: any) => {
  if (!active || !payload?.length) return null
  return (
    <div className="card-sm text-xs shadow-xl">
      <p className="font-medium text-white">{payload[0].name}</p>
      <p className="text-slate-400">{payload[0].value?.toLocaleString()} events</p>
    </div>
  )
}

const renderCustomLabel = ({ percent }: any) =>
  percent > 0.05 ? `${(percent * 100).toFixed(0)}%` : ''

export default function SourceBreakdownChart({ data, title = 'Events by Source' }: Props) {
  const chartData = Object.entries(data).map(([name, value]) => ({ name, value }))

  if (chartData.length === 0) {
    return (
      <div className="card flex items-center justify-center h-52 text-slate-500 text-sm">
        No data available
      </div>
    )
  }

  return (
    <div className="card">
      <h2 className="text-sm font-semibold text-slate-300 mb-4">{title}</h2>
      <ResponsiveContainer width="100%" height={200}>
        <PieChart>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            innerRadius={55}
            outerRadius={80}
            paddingAngle={3}
            dataKey="value"
            label={renderCustomLabel}
            labelLine={false}
          >
            {chartData.map((_, i) => (
              <Cell key={i} fill={COLORS[i % COLORS.length]} stroke="transparent" />
            ))}
          </Pie>
          <Tooltip content={<CustomTooltip />} />
          <Legend
            formatter={(value) => (
              <span className="text-xs text-slate-400">{value}</span>
            )}
            iconType="circle"
            iconSize={8}
          />
        </PieChart>
      </ResponsiveContainer>
    </div>
  )
}
