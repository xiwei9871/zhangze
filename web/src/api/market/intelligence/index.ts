import request from '@/utils/request'
import type {
  DashboardSummary,
  ExternalImportItem,
  ImportanceLevel,
  IntelligenceStatus,
  MarketCrawlLog,
  MarketInfoSource,
  MarketIntelligence,
  MarketKeywordTopic,
  MarketReport,
  PageResult,
  TestFetchResponse
} from './types'

export const marketApi = {
  listSources: () => request.get<unknown, PageResult<MarketInfoSource>>('/sources'),
  saveSource: (data: MarketInfoSource) => data.id ? request.put(`/sources/${data.id}`, data) : request.post('/sources', data),
  enableSource: (id: number) => request.post(`/sources/${id}/enable`),
  disableSource: (id: number) => request.post(`/sources/${id}/disable`),
  testFetch: (id: number) => request.post<unknown, TestFetchResponse>(`/sources/${id}/test-fetch`),

  listTopics: () => request.get<unknown, PageResult<MarketKeywordTopic>>('/topics'),
  saveTopic: (data: MarketKeywordTopic) => data.id ? request.put(`/topics/${data.id}`, data) : request.post('/topics', data),
  enableTopic: (id: number) => request.post(`/topics/${id}/enable`),
  disableTopic: (id: number) => request.post(`/topics/${id}/disable`),

  runCrawl: () => request.post<unknown, MarketCrawlLog>('/crawl/run'),
  runSourceCrawl: (id: number) => request.post<unknown, MarketCrawlLog>(`/crawl/run/${id}`),
  listCrawlLogs: () => request.get<unknown, PageResult<MarketCrawlLog>>('/crawl/logs'),

  listItems: () => request.get<unknown, PageResult<MarketIntelligence>>('/items'),
  resummarize: (id: number) => request.post(`/items/${id}/resummarize`),
  assign: (id: number, ownerUserId?: number, managerUserId?: number) => request.post(`/items/${id}/assign`, { ownerUserId, managerUserId }),
  verify: (id: number, data: { afterStatus: IntelligenceStatus; importanceLevel: ImportanceLevel; verifyRemark: string; actionSuggestion: string; operatorId: number }) =>
    request.post(`/items/${id}/verify`, data),
  report: (id: number, data: { reportTitle: string; reportContent: string; reportToUserId: number; reportByUserId: number }) =>
    request.post(`/items/${id}/report`, data),

  listReports: () => request.get<unknown, PageResult<MarketReport>>('/reports'),
  summary: () => request.get<unknown, DashboardSummary>('/dashboard/summary'),
  trend: () => request.get<unknown, Record<string, number>>('/dashboard/trend'),
  sourceDistribution: () => request.get<unknown, Record<string, number>>('/dashboard/source-distribution'),
  topicRank: () => request.get<unknown, Record<string, number>>('/dashboard/topic-rank'),
  keywordRank: () => request.get<unknown, Record<string, number>>('/dashboard/keyword-rank'),
  highValueItems: () => request.get<unknown, MarketIntelligence[]>('/dashboard/high-value-items'),
  pendingItems: () => request.get<unknown, MarketIntelligence[]>('/dashboard/pending-items'),

  externalImport: (sourceCode: string, items: ExternalImportItem[]) => request.post('/external/items/import', { sourceCode, items })
}
