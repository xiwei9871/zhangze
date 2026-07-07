<script setup lang="ts">
import type { MarketIntelligence } from '@/api/market/intelligence/types'

defineProps<{ item: MarketIntelligence }>()

function formatDateTime(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}
</script>

<template>
  <el-descriptions :column="1" border>
    <el-descriptions-item label="标题">{{ item.title }}</el-descriptions-item>
    <el-descriptions-item label="来源">{{ item.sourceName }}</el-descriptions-item>
    <el-descriptions-item label="发布时间">{{ formatDateTime(item.publishTime) }}</el-descriptions-item>
    <el-descriptions-item label="抓取时间">{{ formatDateTime(item.crawlTime) }}</el-descriptions-item>
    <el-descriptions-item label="原文链接">
      <el-link :href="item.url" target="_blank" type="primary">{{ item.url }}</el-link>
    </el-descriptions-item>
    <el-descriptions-item label="AI 摘要">{{ item.aiSummary }}</el-descriptions-item>
    <el-descriptions-item label="机会点">{{ item.aiOpportunity }}</el-descriptions-item>
    <el-descriptions-item label="风险点">{{ item.aiRisk }}</el-descriptions-item>
    <el-descriptions-item label="建议动作">{{ item.aiSuggestion }}</el-descriptions-item>
  </el-descriptions>
</template>

<style scoped>
:deep(.el-descriptions__content .el-link) {
  max-width: 100%;
  overflow-wrap: anywhere;
}
</style>
