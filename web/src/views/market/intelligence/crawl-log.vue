<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { marketApi } from '@/api/market/intelligence'
import type { MarketCrawlLog } from '@/api/market/intelligence/types'

const logs = ref<MarketCrawlLog[]>([])

async function load() {
  logs.value = (await marketApi.listCrawlLogs()).records
}

async function run() {
  await marketApi.runCrawl()
  ElMessage.success('抓取任务已触发')
  await load()
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar"><h2>抓取日志</h2><el-button type="primary" @click="run">手动抓取</el-button></div>
    <el-table :data="logs" border>
      <el-table-column prop="batchNo" label="批次" min-width="240" />
      <el-table-column prop="status" label="状态" width="110" />
      <el-table-column prop="fetchedCount" label="抓取" width="90" />
      <el-table-column prop="createdCount" label="新增" width="90" />
      <el-table-column prop="duplicatedCount" label="重复" width="90" />
      <el-table-column prop="matchedCount" label="命中" width="90" />
      <el-table-column prop="failedCount" label="失败" width="90" />
      <el-table-column prop="errorMessage" label="说明" />
    </el-table>
  </section>
</template>
