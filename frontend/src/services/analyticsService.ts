import { api } from './api'
import type { AnalyticsSummary, Event, Metric, MetricSummary, PageResponse, TimeSeriesPoint } from '../types'

export const analyticsService = {
  getSummary: (): Promise<AnalyticsSummary> =>
    api.get('/analytics/summary').then((r) => r.data),

  // Events
  getEvents: (params: {
    page?: number
    size?: number
    source?: string
    eventType?: string
    from?: string
    to?: string
  }): Promise<PageResponse<Event>> =>
    api.get('/events', { params }).then((r) => r.data),

  ingestEvent: (data: {
    source: string
    eventType: string
    numericValue?: number
    stringValue?: string
    payload?: string
    eventTimestamp: string
  }): Promise<Event> => api.post('/events', data).then((r) => r.data),

  getEventSources: (): Promise<string[]> =>
    api.get('/events/sources').then((r) => r.data),

  // Metrics
  getMetricNames: (): Promise<string[]> =>
    api.get('/metrics/names').then((r) => r.data),

  getTimeSeries: (metricName: string, from?: string, to?: string): Promise<TimeSeriesPoint[]> =>
    api.get('/metrics/timeseries', { params: { metricName, from, to } }).then((r) => r.data),

  getAggregates: (from?: string, to?: string): Promise<MetricSummary[]> =>
    api.get('/metrics/aggregates', { params: { from, to } }).then((r) => r.data),

  recordMetric: (data: {
    metricName: string
    value: number
    unit?: string
    recordedAt: string
    aggregationType?: string
  }): Promise<Metric> => api.post('/metrics', data).then((r) => r.data),
}
