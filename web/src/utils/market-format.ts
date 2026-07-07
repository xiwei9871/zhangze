import type { ImportanceLevel, IntelligenceStatus } from '@/api/market/intelligence/types'

const importanceLabels: Record<ImportanceLevel, string> = {
  LOW: '低',
  MEDIUM: '中',
  HIGH: '高',
  URGENT: '紧急'
}

const statusLabels: Record<IntelligenceStatus, string> = {
  PENDING_SUMMARY: '待摘要',
  SUMMARY_FAILED: '摘要失败',
  PENDING_VERIFY: '待核验',
  VALID: '有效',
  INVALID: '无效',
  DUPLICATE: '重复',
  FOLLOW_UP: '待跟进',
  REPORTED: '已上报'
}

export function formatImportance(level: ImportanceLevel) {
  return importanceLabels[level]
}

export function formatStatus(status: IntelligenceStatus) {
  return statusLabels[status]
}
