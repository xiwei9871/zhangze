<script setup lang="ts">
import * as echarts from 'echarts'
import { onMounted, ref } from 'vue'
import { marketApi } from '@/api/market/intelligence'
import type { DashboardSummary, MarketIntelligence } from '@/api/market/intelligence/types'

const summary = ref<DashboardSummary>({
  todayNewCount: 0,
  pendingVerifyCount: 0,
  validCount: 0,
  followUpCount: 0,
  reportedCount: 0,
  highImportanceCount: 0
})
const highValueItems = ref<MarketIntelligence[]>([])
const pendingItems = ref<MarketIntelligence[]>([])
const trendChart = ref<HTMLDivElement>()
const sourceChart = ref<HTMLDivElement>()

async function load() {
  summary.value = await marketApi.summary()
  highValueItems.value = await marketApi.highValueItems()
  pendingItems.value = await marketApi.pendingItems()
  const trend = await marketApi.trend()
  const sources = await marketApi.sourceDistribution()
  renderLine(trend)
  renderPie(sources)
}

function renderLine(data: Record<string, number>) {
  if (!trendChart.value) return
  echarts.init(trendChart.value).setOption({
    tooltip: {},
    xAxis: { type: 'category', data: Object.keys(data) },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: Object.values(data), smooth: true }]
  })
}

function renderPie(data: Record<string, number>) {
  if (!sourceChart.value) return
  echarts.init(sourceChart.value).setOption({
    tooltip: {},
    series: [{ type: 'pie', radius: '60%', data: Object.entries(data).map(([name, value]) => ({ name, value })) }]
  })
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar">
      <h2>市场情报看板</h2>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>
    <div class="stat-grid">
      <div class="stat">今日新增<strong>{{ summary.todayNewCount }}</strong></div>
      <div class="stat">待核验<strong>{{ summary.pendingVerifyCount }}</strong></div>
      <div class="stat">有效<strong>{{ summary.validCount }}</strong></div>
      <div class="stat">待跟进<strong>{{ summary.followUpCount }}</strong></div>
      <div class="stat">已上报<strong>{{ summary.reportedCount }}</strong></div>
      <div class="stat">高重要级别<strong>{{ summary.highImportanceCount }}</strong></div>
    </div>
    <el-row :gutter="12">
      <el-col :span="14"><el-card><div ref="trendChart" class="chart" /></el-card></el-col>
      <el-col :span="10"><el-card><div ref="sourceChart" class="chart" /></el-card></el-col>
    </el-row>
    <el-row :gutter="12" style="margin-top: 12px">
      <el-col :span="12">
        <el-card>
          <template #header>高价值情报</template>
          <el-table :data="highValueItems" height="260">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="manualImportanceLevel" label="级别" width="100" />
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>待核验情报</template>
          <el-table :data="pendingItems" height="260">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="ownerUserId" label="责任人" width="100" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </section>
</template>
