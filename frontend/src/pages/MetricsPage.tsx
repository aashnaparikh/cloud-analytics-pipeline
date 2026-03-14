import { useState, useEffect } from 'react'
import { useQuery } from '@tanstack/react-query'
import { analyticsService } from '../services/analyticsService'
import MetricTimeSeriesChart from '../components/charts/MetricTimeSeriesChart'
import MetricsTable from '../components/dashboard/MetricsTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorBanner from '../components/common/ErrorBanner'
import { subHours, formatISO } from 'date-fns'

const TIME_RANGES = [
  { label: '1h',  hours: 1 },
  { label: '6h',  hours: 6 },
  { label: '24h', hours: 24 },
  { label: '7d',  hours: 168 },
]

export default function MetricsPage() {
  const [selectedMetric, setSelectedMetric] = useState<string>('')
  const [rangeHours, setRangeHours] = useState(24)

  const from = formatISO(subHours(new Date(), rangeHours))
  const to = formatISO(new Date())

  const { data: names, isLoading: namesLoading } = useQuery({
    queryKey: ['metric-names'],
    queryFn: analyticsService.getMetricNames,
  })

  // Set default metric once names load
  useEffect(() => {
    if (names && names.length > 0 && !selectedMetric) {
      setSelectedMetric(names[0])
    }
  }, [names, selectedMetric])

  const { data: timeSeries, isLoading: tsLoading, error: tsError } = useQuery({
    queryKey: ['metric-timeseries', selectedMetric, rangeHours],
    queryFn: () => analyticsService.getTimeSeries(selectedMetric, from, to),
    enabled: !!selectedMetric,
  })

  const { data: aggregates, isLoading: aggLoading } = useQuery({
    queryKey: ['metric-aggregates', rangeHours],
    queryFn: () => analyticsService.getAggregates(from, to),
  })

  if (namesLoading) return <LoadingSpinner className="h-48" />
  if (tsError) return <ErrorBanner message="Failed to load metrics data." />

  const metricNames: string[] = names ?? []

  return (
    <div className="space-y-6 max-w-7xl">
      {/* Controls */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Metric selector */}
        <select
          value={selectedMetric}
          onChange={(e) => setSelectedMetric(e.target.value)}
          className="input text-xs py-2 min-w-[220px]"
          disabled={metricNames.length === 0}
        >
          {metricNames.length === 0
            ? <option>No metrics available</option>
            : metricNames.map((n) => <option key={n} value={n}>{n}</option>)}
        </select>

        {/* Time range pills */}
        <div className="flex gap-1">
          {TIME_RANGES.map(({ label, hours }) => (
            <button
              key={label}
              onClick={() => setRangeHours(hours)}
              className={`btn text-xs py-1.5 px-3 ${
                rangeHours === hours
                  ? 'bg-brand-600 text-white border border-brand-500'
                  : 'bg-surface-card border border-surface-border text-slate-400 hover:text-slate-200'
              }`}
            >
              {label}
            </button>
          ))}
        </div>
      </div>

      {/* Time series chart */}
      {selectedMetric && (
        tsLoading
          ? <LoadingSpinner className="h-48" />
          : <MetricTimeSeriesChart data={timeSeries ?? []} metricName={selectedMetric} />
      )}

      {/* Aggregates table */}
      {aggLoading
        ? <LoadingSpinner className="h-32" />
        : <MetricsTable summaries={aggregates ?? []} />
      }
    </div>
  )
}
