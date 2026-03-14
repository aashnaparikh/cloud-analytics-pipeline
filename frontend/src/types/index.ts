export interface User {
  userId: string
  email: string
  firstName: string
  lastName: string
  role: 'ADMIN' | 'ANALYST' | 'VIEWER'
  tenantId: string
}

export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  userId: string
  email: string
  firstName: string
  lastName: string
  role: 'ADMIN' | 'ANALYST' | 'VIEWER'
  tenantId: string
}

export interface Event {
  id: string
  source: string
  eventType: string
  tenantId: string
  payload?: string
  numericValue?: number
  stringValue?: string
  eventTimestamp: string
  ingestedAt: string
  status: 'PENDING' | 'PROCESSED' | 'FAILED'
}

export interface Metric {
  id: string
  metricName: string
  tenantId: string
  value: number
  unit?: string
  recordedAt: string
  aggregationType?: string
  tags?: string
}

export interface TimeSeriesPoint {
  timestamp: string
  count?: number
  value?: number
}

export interface MetricSummary {
  metricName: string
  avg: number
  min: number
  max: number
  sum: number
  count: number
  unit?: string
}

export interface AnalyticsSummary {
  totalEvents: number
  eventsLast24h: number
  eventsLastHour: number
  eventsBySource: Record<string, number>
  eventsByType: Record<string, number>
  eventTimeSeries: TimeSeriesPoint[]
  metricSummaries: MetricSummary[]
  generatedAt: string
}

export interface PageResponse<T> {
  content: T[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  last: boolean
}

export interface ProblemDetail {
  status: number
  detail: string
  errors?: Record<string, string>
}
