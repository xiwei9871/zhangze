<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { marketApi } from '@/api/market/intelligence'
import type { MarketReport } from '@/api/market/intelligence/types'

const reports = ref<MarketReport[]>([])

async function load() {
  reports.value = (await marketApi.listReports()).records
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar"><h2>上报记录</h2><el-button type="primary" @click="load">刷新</el-button></div>
    <el-table :data="reports" border>
      <el-table-column prop="reportTitle" label="标题" />
      <el-table-column prop="reportToUserId" label="上报对象" width="120" />
      <el-table-column prop="reportByUserId" label="上报人" width="120" />
      <el-table-column prop="reportTime" label="上报时间" width="210" />
      <el-table-column prop="reportContent" label="内容" show-overflow-tooltip />
    </el-table>
  </section>
</template>
