import { describe, expect, it } from 'vitest'
import { formatImportance, formatStatus } from './market-format'

describe('market-format', () => {
  it('formats importance labels for business users', () => {
    expect(formatImportance('URGENT')).toBe('紧急')
    expect(formatImportance('HIGH')).toBe('高')
  })

  it('formats intelligence status labels for review workflow', () => {
    expect(formatStatus('PENDING_VERIFY')).toBe('待核验')
    expect(formatStatus('REPORTED')).toBe('已上报')
  })
})
