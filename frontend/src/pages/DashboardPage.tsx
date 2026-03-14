import { useQuery } from '@tanstack/react-query'
import { Activity, Zap, Clock, TrendingUp } from 'lucide-react'
import { analyticsService } from '../services/analyticsService'
import StatCard from '../components/common/StatCard'
import EventTimeSeriesChart from '../components/charts/EventTimeSeriesChart'
import SourceBreakdownChart from '../components/charts/SourceBreakdownChart'
import MetricsTable from '../components/dashboard/MetricsTable'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorBanner from '../components/common/ErrorBanner'
import { formatDistanceToNow } from 'date-fns'

export default function DashboardPage() {
  const { data, isLoading, error, dataUpdatedAt } = useQuery({
    queryKey: ['analytics', 'summary'],
    queryFn: analyticsService.getSummary,
    refetchInterval: 30_000,
  })

  if (isLoading) return <LoadingSpinner className="h-64" />
  if (error) return <ErrorBanner message="Failed to load analytics summary. Is the backend running?" />

  const summary = data!

  return (
    <div className="space-y-6 max-w-7xl">
      {/* KPI row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          label="Total Events"
          value={summary.totalEvents}
          icon={Zap}
          color="blue"
          subtext="All-time"
        />
        <StatCard
          label="Last 24 Hours"
          value={summary.eventsLast24h}
          icon={TrendingUp}
          color="green"
          subtext="Events ingested"
        />
        <StatCard
          label="Last Hour"
          value={summary.eventsLastHour}
          icon={Clock}
          color="amber"
          subtext="Events ingested"
        />
        <StatCard
          label="Metric Types"
          value={summary.metricSummaries.length}
          icon={Activity}
          color="purple"
          subtext="Distinct metrics"
        />
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <div className="lg:col-span-2">
          <EventTimeSeriesChart data={summary.eventTimeSeries} />
        </div>
        <SourceBreakdownChart data={summary.eventsBySource} />
      </div>

      {/* Event type breakdown + Metrics table */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">
        <SourceBreakdownChart data={summary.eventsByType} title="Events by Type" />
        <div className="lg:col-span-2">
          <MetricsTable summaries={summary.metricSummaries} />
        </div>
      </div>

      {/* Footer */}
      {dataUpdatedAt > 0 && (
        <p className="text-xs text-slate-600 text-right">
          Last refreshed {formatDistanceToNow(dataUpdatedAt, { addSuffix: true })}
        </p>
      )}
    </div>
  )
}
