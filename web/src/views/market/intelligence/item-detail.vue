<script setup lang="ts">
import type { MarketIntelligence } from '@/api/market/intelligence/types'

defineProps<{ item: MarketIntelligence }>()
</script>

<template>
  <el-descriptions :column="1" border>
    <el-descriptions-item label="标题">{{ item.title }}</el-descriptions-item>
    <el-descriptions-item label="原文"><el-link :href="item.url" target="_blank">{{ item.url }}</el-link></el-descriptions-item>
    <el-descriptions-item label="AI 摘要">{{ item.aiSummary }}</el-descriptions-item>
    <el-descriptions-item label="机会点">{{ item.aiOpportunity }}</el-descriptions-item>
    <el-descriptions-item label="风险点">{{ item.aiRisk }}</el-descriptions-item>
    <el-descriptions-item label="建议动作">{{ item.aiSuggestion }}</el-descriptions-item>
  </el-descriptions>
</template>
