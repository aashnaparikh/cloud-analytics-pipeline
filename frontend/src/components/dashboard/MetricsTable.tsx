import type { MetricSummary } from '../../types'

interface Props {
  summaries: MetricSummary[]
}

export default function MetricsTable({ summaries }: Props) {
  if (summaries.length === 0) {
    return (
      <div className="card flex items-center justify-center h-32 text-slate-500 text-sm">
        No metrics recorded yet
      </div>
    )
  }

  return (
    <div className="card overflow-hidden">
      <h2 className="text-sm font-semibold text-slate-300 mb-4">Metric Aggregates (Last 24h)</h2>
      <div className="overflow-x-auto">
        <table className="w-full text-xs">
          <thead>
            <tr className="text-slate-500 uppercase tracking-wider">
              {['Metric', 'Avg', 'Min', 'Max', 'Count'].map((h) => (
                <th key={h} className="text-left py-2 px-3 font-medium">{h}</th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y divide-surface-border">
            {summaries.map((m) => (
              <tr key={m.metricName} className="hover:bg-surface/50 transition-colors">
                <td className="py-2.5 px-3 font-medium text-slate-200 font-mono">{m.metricName}</td>
                <td className="py-2.5 px-3 text-brand-400">{Number(m.avg).toFixed(3)}</td>
                <td className="py-2.5 px-3 text-emerald-400">{Number(m.min).toFixed(3)}</td>
                <td className="py-2.5 px-3 text-amber-400">{Number(m.max).toFixed(3)}</td>
                <td className="py-2.5 px-3 text-slate-400">{m.count.toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  )
}
