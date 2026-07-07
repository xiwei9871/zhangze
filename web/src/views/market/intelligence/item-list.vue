<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { marketApi } from '@/api/market/intelligence'
import type { ImportanceLevel, IntelligenceStatus, MarketIntelligence } from '@/api/market/intelligence/types'
import { formatImportance, formatStatus } from '@/utils/market-format'

const items = ref<MarketIntelligence[]>([])
const current = ref<MarketIntelligence>()
const drawerVisible = ref(false)
const verifyVisible = ref(false)
const reportVisible = ref(false)
const verifyForm = ref({ afterStatus: 'FOLLOW_UP' as IntelligenceStatus, importanceLevel: 'HIGH' as ImportanceLevel, verifyRemark: '', actionSuggestion: '', operatorId: 901 })
const reportForm = ref({ reportTitle: '', reportContent: '', reportToUserId: 202, reportByUserId: 901 })

async function load() {
  const page = await marketApi.listItems()
  items.value = page.records
}

function openDetail(row: MarketIntelligence) {
  current.value = row
  drawerVisible.value = true
}

function openOriginal(row: MarketIntelligence) {
  globalThis.open(row.url, '_blank', 'noopener,noreferrer')
}

function sourceHost(url: string) {
  try {
    return new URL(url).host
  } catch {
    return url
  }
}

function formatDateTime(value?: string) {
  if (!value) return '-'
  return value.replace('T', ' ').slice(0, 16)
}

async function resummarize(row: MarketIntelligence) {
  await marketApi.resummarize(row.id)
  ElMessage.success('已重新摘要')
  await load()
}

async function submitVerify() {
  if (!current.value) return
  await marketApi.verify(current.value.id, verifyForm.value)
  verifyVisible.value = false
  ElMessage.success('核验已保存')
  await load()
}

async function submitReport() {
  if (!current.value) return
  await marketApi.report(current.value.id, reportForm.value)
  reportVisible.value = false
  ElMessage.success('已上报管理账号')
  await load()
}

function prepareReport(row: MarketIntelligence) {
  current.value = row
  reportForm.value.reportTitle = row.title
  reportForm.value.reportContent = `${row.aiSummary ?? ''}\n${row.aiOpportunity ?? ''}\n${row.aiSuggestion ?? ''}`
  reportVisible.value = true
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar">
      <h2>情报列表</h2>
      <el-button type="primary" @click="load">刷新</el-button>
    </div>
    <el-table :data="items" border>
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="sourceName" label="来源" width="140" />
      <el-table-column label="原文链接" min-width="230">
        <template #default="{ row }">
          <div class="source-link-cell">
            <el-link :href="row.url" target="_blank" type="primary" :underline="false">
              {{ sourceHost(row.url) }}
            </el-link>
            <span class="url-text">{{ row.url }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="matchedKeywords" label="关键词" width="180">
        <template #default="{ row }">{{ row.matchedKeywords.join('、') }}</template>
      </el-table-column>
      <el-table-column prop="aiSummary" label="AI 摘要" min-width="240" show-overflow-tooltip />
      <el-table-column prop="manualImportanceLevel" label="级别" width="110">
        <template #default="{ row }"><el-tag>{{ formatImportance(row.manualImportanceLevel) }}</el-tag></template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="130">
        <template #default="{ row }"><el-tag type="info">{{ formatStatus(row.status) }}</el-tag></template>
      </el-table-column>
      <el-table-column label="操作" width="300" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDetail(row)">详情</el-button>
          <el-button link @click="openOriginal(row)">原文</el-button>
          <el-button link @click="current = row; verifyVisible = true">核验</el-button>
          <el-button link @click="prepareReport(row)">上报</el-button>
          <el-button link @click="resummarize(row)">重摘</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-drawer v-model="drawerVisible" size="45%" title="情报详情">
      <template v-if="current">
        <div class="detail-title-row">
          <h3>{{ current.title }}</h3>
          <el-button type="primary" @click="openOriginal(current)">打开原文</el-button>
        </div>
        <p class="detail-excerpt">{{ current.contentExcerpt }}</p>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="来源">{{ current.sourceName }}</el-descriptions-item>
          <el-descriptions-item label="发布时间">{{ formatDateTime(current.publishTime) }}</el-descriptions-item>
          <el-descriptions-item label="抓取时间">{{ formatDateTime(current.crawlTime) }}</el-descriptions-item>
          <el-descriptions-item label="原文链接">
            <el-link :href="current.url" target="_blank" type="primary">{{ current.url }}</el-link>
          </el-descriptions-item>
          <el-descriptions-item label="AI 摘要">{{ current.aiSummary }}</el-descriptions-item>
          <el-descriptions-item label="机会点">{{ current.aiOpportunity }}</el-descriptions-item>
          <el-descriptions-item label="风险点">{{ current.aiRisk }}</el-descriptions-item>
          <el-descriptions-item label="建议动作">{{ current.aiSuggestion }}</el-descriptions-item>
          <el-descriptions-item label="核验备注">{{ current.verifyRemark }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>

    <el-dialog v-model="verifyVisible" title="人工核验" width="520px">
      <el-form label-width="90px">
        <el-form-item label="状态"><el-select v-model="verifyForm.afterStatus"><el-option label="有效" value="VALID" /><el-option label="无效" value="INVALID" /><el-option label="重复" value="DUPLICATE" /><el-option label="待跟进" value="FOLLOW_UP" /></el-select></el-form-item>
        <el-form-item label="重要级别"><el-select v-model="verifyForm.importanceLevel"><el-option label="低" value="LOW" /><el-option label="中" value="MEDIUM" /><el-option label="高" value="HIGH" /><el-option label="紧急" value="URGENT" /></el-select></el-form-item>
        <el-form-item label="核验备注"><el-input v-model="verifyForm.verifyRemark" type="textarea" /></el-form-item>
        <el-form-item label="建议动作"><el-input v-model="verifyForm.actionSuggestion" type="textarea" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="verifyVisible = false">取消</el-button><el-button type="primary" @click="submitVerify">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="reportVisible" title="上报管理账号" width="560px">
      <el-form label-width="90px">
        <el-form-item label="标题"><el-input v-model="reportForm.reportTitle" /></el-form-item>
        <el-form-item label="对象"><el-input-number v-model="reportForm.reportToUserId" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="reportForm.reportContent" type="textarea" :rows="5" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="reportVisible = false">取消</el-button><el-button type="primary" @click="submitReport">上报</el-button></template>
    </el-dialog>
  </section>
</template>

<style scoped>
.source-link-cell {
  display: grid;
  gap: 2px;
  min-width: 0;
}

.url-text {
  color: #6b7280;
  font-size: 12px;
  line-height: 1.35;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-title-row {
  display: flex;
  gap: 12px;
  align-items: flex-start;
  justify-content: space-between;
}

.detail-title-row h3 {
  margin: 0 0 10px;
  line-height: 1.4;
}

.detail-excerpt {
  margin: 0 0 14px;
  color: #4b5563;
  line-height: 1.7;
}

:deep(.el-descriptions__content .el-link) {
  max-width: 100%;
  overflow-wrap: anywhere;
}
</style>
