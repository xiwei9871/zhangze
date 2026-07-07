export type SourceType = 'PUBLIC_WEB' | 'TENDER' | 'NEWS' | 'INDUSTRY_DATA' | 'COMPETITOR' | 'OTHER'
export type FetchMode = 'HTML' | 'RSS' | 'API' | 'SEARCH_PAGE' | 'EXTERNAL_IMPORT' | 'MANUAL'
export type MatchScope = 'TITLE' | 'CONTENT' | 'TITLE_AND_CONTENT'
export type ImportanceLevel = 'LOW' | 'MEDIUM' | 'HIGH' | 'URGENT'
export type IntelligenceStatus = 'PENDING_SUMMARY' | 'SUMMARY_FAILED' | 'PENDING_VERIFY' | 'VALID' | 'INVALID' | 'DUPLICATE' | 'FOLLOW_UP' | 'REPORTED'

export interface PageResult<T> {
  records: T[]
  total: number
  pageNo: number
  pageSize: number
}

export interface MarketInfoSource {
  id?: number
  sourceName: string
  sourceCode: string
  sourceType: SourceType
  url: string
  fetchMode: FetchMode
  fetchIntervalMinutes: number
  enabled: boolean
  defaultOwnerUserId?: number
  defaultManagerUserId?: number
  lastFetchTime?: string
  lastFetchStatus?: string
  lastErrorMessage?: string
  fetchConfig?: Record<string, unknown>
  lastCrawlCursor?: string
  remark?: string
}

export interface TestFetchResponse {
  sourceId: number
  status: string
  fetchedCount: number
  sampleItems: Array<{
    title: string
    url: string
    publishTime?: string
    contentExcerpt?: string
  }>
  errorMessage?: string
}

export interface MarketKeywordTopic {
  id?: number
  topicName: string
  includeKeywords: string[]
  excludeKeywords: string[]
  matchScope: MatchScope
  importanceLevel: ImportanceLevel
  ownerUserId?: number
  managerUserId?: number
  enabled: boolean
  remark?: string
}

export interface MarketIntelligence {
  id: number
  sourceName: string
  sourceType: SourceType
  title: string
  url: string
  publishTime?: string
  crawlTime?: string
  contentExcerpt?: string
  rawContent?: string
  matchedKeywords: string[]
  matchedTopicIds: number[]
  aiSummary?: string
  aiKeyPoints: string[]
  aiOpportunity?: string
  aiRisk?: string
  aiSuggestion?: string
  aiErrorMessage?: string
  aiImportanceScore?: number
  aiConfidence?: number
  manualImportanceLevel: ImportanceLevel
  status: IntelligenceStatus
  ownerUserId?: number
  managerUserId?: number
  reported: boolean
  verifyRemark?: string
}

export interface MarketCrawlLog {
  id: number
  sourceId?: number
  batchNo: string
  startTime: string
  endTime?: string
  status: string
  fetchedCount: number
  createdCount: number
  duplicatedCount: number
  matchedCount: number
  failedCount: number
  errorMessage?: string
}

export interface MarketReport {
  id: number
  intelligenceId: number
  reportTitle: string
  reportContent: string
  reportToUserId: number
  reportByUserId: number
  reportTime: string
}

export interface ExternalImportItem {
  title: string
  url: string
  publishTime: string
  author?: string
  content: string
  html?: string
  tags: string[]
}

export interface DashboardSummary {
  todayNewCount: number
  pendingVerifyCount: number
  validCount: number
  followUpCount: number
  reportedCount: number
  highImportanceCount: number
}
