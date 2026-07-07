<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { marketApi } from '@/api/market/intelligence'
import type { MarketInfoSource, TestFetchResponse } from '@/api/market/intelligence/types'

const sources = ref<MarketInfoSource[]>([])
const dialogVisible = ref(false)
const testResult = ref<TestFetchResponse | null>(null)
const runningId = ref<number>()
const form = ref<MarketInfoSource>(emptyForm())

const htmlConfigVisible = computed(() => form.value.fetchMode === 'HTML' || form.value.fetchMode === 'SEARCH_PAGE')

async function load() {
  sources.value = (await marketApi.listSources()).records
}

function create() {
  form.value = emptyForm()
  testResult.value = null
  dialogVisible.value = true
}

function edit(row: MarketInfoSource) {
  form.value = { ...row, fetchConfig: { ...(row.fetchConfig ?? {}) } }
  testResult.value = null
  dialogVisible.value = true
}

async function save() {
  normalizeFetchConfig()
  await marketApi.saveSource(form.value)
  dialogVisible.value = false
  ElMessage.success('信息源已保存')
  await load()
}

async function toggle(row: MarketInfoSource) {
  if (!row.id) return
  row.enabled ? await marketApi.disableSource(row.id) : await marketApi.enableSource(row.id)
  await load()
}

async function testFetch(row: MarketInfoSource) {
  if (!row.id) return
  const result = await marketApi.testFetch(row.id)
  ElMessage.success(`测试抓取完成：${result.fetchedCount} 条`)
  await load()
}

async function runSource(row: MarketInfoSource) {
  if (!row.id) return
  runningId.value = row.id
  try {
    const log = await marketApi.runSourceCrawl(row.id)
    ElMessage.success(`抓取完成：新增 ${log.createdCount} 条，重复 ${log.duplicatedCount} 条`)
    await load()
  } finally {
    runningId.value = undefined
  }
}

async function runAll() {
  const log = await marketApi.runCrawl()
  ElMessage.success(`全部抓取完成：新增 ${log.createdCount} 条`)
  await load()
}

async function testCurrentSource() {
  if (!form.value.id) {
    ElMessage.warning('请先保存信息源')
    return
  }
  normalizeFetchConfig()
  await marketApi.saveSource(form.value)
  testResult.value = await marketApi.testFetch(form.value.id)
  ElMessage.success(`测试抓取完成：${testResult.value.fetchedCount} 条`)
}

function configValue(key: string) {
  return String(form.value.fetchConfig?.[key] ?? '')
}

function setConfigValue(key: string, value: string) {
  form.value.fetchConfig = { ...(form.value.fetchConfig ?? {}), [key]: value }
}

function normalizeFetchConfig() {
  if (form.value.fetchMode === 'RSS') {
    form.value.fetchConfig = {}
  }
}

function emptyForm(): MarketInfoSource {
  return {
    sourceName: '',
    sourceCode: '',
    sourceType: 'NEWS',
    url: '',
    fetchMode: 'RSS',
    fetchIntervalMinutes: 60,
    enabled: true,
    fetchConfig: {}
  }
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar">
      <h2>信息源管理</h2>
      <div>
        <el-button @click="runAll">运行全部抓取</el-button>
        <el-button type="primary" @click="create">新增</el-button>
      </div>
    </div>
    <el-table :data="sources" border>
      <el-table-column prop="sourceName" label="名称" />
      <el-table-column prop="sourceCode" label="编码" />
      <el-table-column prop="sourceType" label="类型" />
      <el-table-column prop="fetchMode" label="抓取方式" />
      <el-table-column prop="lastFetchStatus" label="最近结果" width="110">
        <template #default="{ row }">
          <el-tag v-if="row.lastFetchStatus" :type="row.lastFetchStatus === 'SUCCESS' ? 'success' : 'danger'">{{ row.lastFetchStatus }}</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="lastFetchTime" label="最近抓取" min-width="170" />
      <el-table-column prop="enabled" label="状态"><template #default="{ row }"><el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="270">
        <template #default="{ row }">
          <el-button link @click="edit(row)">编辑</el-button>
          <el-button link @click="toggle(row)">{{ row.enabled ? '停用' : '启用' }}</el-button>
          <el-button link @click="testFetch(row)">测试</el-button>
          <el-button link :loading="runningId === row.id" @click="runSource(row)">抓取</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="信息源" width="720px">
      <el-form label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.sourceName" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.sourceCode" /></el-form-item>
        <el-form-item label="URL"><el-input v-model="form.url" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.sourceType"><el-option value="NEWS" label="新闻" /><el-option value="TENDER" label="招采" /><el-option value="INDUSTRY_DATA" label="行业数据" /><el-option value="COMPETITOR" label="竞争公司" /><el-option value="OTHER" label="其他" /></el-select></el-form-item>
        <el-form-item label="方式"><el-select v-model="form.fetchMode"><el-option value="HTML" label="HTML" /><el-option value="RSS" label="RSS" /><el-option value="API" label="API" /><el-option value="EXTERNAL_IMPORT" label="外部导入" /></el-select></el-form-item>
        <template v-if="htmlConfigVisible">
          <el-form-item label="列表项"><el-input :model-value="configValue('itemSelector')" @update:model-value="setConfigValue('itemSelector', $event)" /></el-form-item>
          <el-form-item label="标题"><el-input :model-value="configValue('titleSelector')" @update:model-value="setConfigValue('titleSelector', $event)" /></el-form-item>
          <el-form-item label="链接"><el-input :model-value="configValue('linkSelector')" @update:model-value="setConfigValue('linkSelector', $event)" /></el-form-item>
          <el-form-item label="正文"><el-input :model-value="configValue('contentSelector')" @update:model-value="setConfigValue('contentSelector', $event)" /></el-form-item>
          <el-form-item label="时间"><el-input :model-value="configValue('publishTimeSelector')" @update:model-value="setConfigValue('publishTimeSelector', $event)" /></el-form-item>
        </template>
        <el-form-item label="责任人"><el-input-number v-model="form.defaultOwnerUserId" :min="1" /></el-form-item>
        <el-form-item label="管理人"><el-input-number v-model="form.defaultManagerUserId" :min="1" /></el-form-item>
        <el-form-item v-if="form.id" label="测试">
          <el-button @click="testCurrentSource">测试抓取</el-button>
          <span v-if="testResult" class="inline-result">{{ testResult.status }} / {{ testResult.fetchedCount }} 条</span>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </section>
</template>
