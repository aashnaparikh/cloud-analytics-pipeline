import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { Search, Filter, ChevronLeft, ChevronRight, CheckCircle, AlertCircle, Clock } from 'lucide-react'
import { analyticsService } from '../services/analyticsService'
import LoadingSpinner from '../components/common/LoadingSpinner'
import ErrorBanner from '../components/common/ErrorBanner'
import { format } from 'date-fns'
import clsx from 'clsx'
import type { Event } from '../types'

const STATUS_STYLES: Record<Event['status'], string> = {
  PROCESSED: 'bg-emerald-400/10 text-emerald-400',
  PENDING:   'bg-amber-400/10 text-amber-400',
  FAILED:    'bg-red-400/10 text-red-400',
}

const STATUS_ICONS: Record<Event['status'], React.FC<{ className?: string }>> = {
  PROCESSED: CheckCircle,
  PENDING:   Clock,
  FAILED:    AlertCircle,
}

export default function EventsPage() {
  const [page, setPage] = useState(0)
  const [sourceFilter, setSourceFilter] = useState('')
  const [typeFilter, setTypeFilter] = useState('')

  const { data: sources } = useQuery({
    queryKey: ['event-sources'],
    queryFn: analyticsService.getEventSources,
  })

  const { data, isLoading, error } = useQuery({
    queryKey: ['events', page, sourceFilter, typeFilter],
    queryFn: () => analyticsService.getEvents({
      page,
      size: 20,
      source: sourceFilter || undefined,
      eventType: typeFilter || undefined,
    }),
  })

  if (error) return <ErrorBanner message="Failed to load events." />

  return (
    <div className="space-y-4 max-w-7xl">
      {/* Filters */}
      <div className="flex flex-wrap items-center gap-3">
        <div className="relative">
          <Filter className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-500" />
          <select
            value={sourceFilter}
            onChange={(e) => { setSourceFilter(e.target.value); setPage(0) }}
            className="input pl-9 pr-8 py-2 text-xs appearance-none cursor-pointer min-w-[160px]"
          >
            <option value="">All Sources</option>
            {sources?.map((s) => <option key={s} value={s}>{s}</option>)}
          </select>
        </div>

        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-500" />
          <input
            value={typeFilter}
            onChange={(e) => { setTypeFilter(e.target.value); setPage(0) }}
            placeholder="Filter event type…"
            className="input pl-9 py-2 text-xs min-w-[200px]"
          />
        </div>

        {(sourceFilter || typeFilter) && (
          <button
            onClick={() => { setSourceFilter(''); setTypeFilter(''); setPage(0) }}
            className="btn-ghost text-xs py-1.5 px-3 text-slate-500"
          >
            Clear filters
          </button>
        )}

        <span className="ml-auto text-xs text-slate-500">
          {data ? `${data.totalElements.toLocaleString()} events` : '—'}
        </span>
      </div>

      {/* Table */}
      <div className="card overflow-hidden p-0">
        {isLoading ? (
          <LoadingSpinner className="h-48" />
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-xs">
              <thead className="border-b border-surface-border">
                <tr className="text-slate-500 uppercase tracking-wider">
                  {['Status', 'Source', 'Event Type', 'Value', 'Timestamp', 'Ingested'].map((h) => (
                    <th key={h} className="text-left py-3 px-4 font-medium">{h}</th>
                  ))}
                </tr>
              </thead>
              <tbody className="divide-y divide-surface-border">
                {data?.content.map((event) => {
                  const StatusIcon = STATUS_ICONS[event.status]
                  return (
                    <tr key={event.id} className="hover:bg-surface/60 transition-colors">
                      <td className="py-3 px-4">
                        <span className={clsx('badge gap-1', STATUS_STYLES[event.status])}>
                          <StatusIcon className="w-3 h-3" />
                          {event.status}
                        </span>
                      </td>
                      <td className="py-3 px-4 font-mono text-brand-400">{event.source}</td>
                      <td className="py-3 px-4 text-slate-300">{event.eventType}</td>
                      <td className="py-3 px-4 text-slate-400 font-mono">
                        {event.numericValue != null
                          ? Number(event.numericValue).toFixed(2)
                          : event.stringValue ?? <span className="text-slate-600">—</span>}
                      </td>
                      <td className="py-3 px-4 text-slate-500 font-mono">
                        {format(new Date(event.eventTimestamp), 'yyyy-MM-dd HH:mm:ss')}
                      </td>
                      <td className="py-3 px-4 text-slate-600 font-mono">
                        {format(new Date(event.ingestedAt), 'HH:mm:ss')}
                      </td>
                    </tr>
                  )
                })}
                {data?.content.length === 0 && (
                  <tr>
                    <td colSpan={6} className="py-12 text-center text-slate-500">
                      No events match your filters
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Pagination */}
      {data && data.totalPages > 1 && (
        <div className="flex items-center justify-between text-xs text-slate-500">
          <span>
            Page {data.page + 1} of {data.totalPages}
          </span>
          <div className="flex gap-2">
            <button
              onClick={() => setPage((p) => Math.max(0, p - 1))}
              disabled={page === 0}
              className="btn-ghost py-1.5 px-3 disabled:opacity-30"
            >
              <ChevronLeft className="w-4 h-4" />
            </button>
            <button
              onClick={() => setPage((p) => p + 1)}
              disabled={data.last}
              className="btn-ghost py-1.5 px-3 disabled:opacity-30"
            >
              <ChevronRight className="w-4 h-4" />
            </button>
          </div>
        </div>
      )}
    </div>
  )
}
