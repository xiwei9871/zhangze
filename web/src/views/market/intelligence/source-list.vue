<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { marketApi } from '@/api/market/intelligence'
import type { MarketInfoSource } from '@/api/market/intelligence/types'

const sources = ref<MarketInfoSource[]>([])
const dialogVisible = ref(false)
const form = ref<MarketInfoSource>({ sourceName: '', sourceCode: '', sourceType: 'NEWS', url: '', fetchMode: 'HTML', fetchIntervalMinutes: 60, enabled: true })

async function load() {
  sources.value = (await marketApi.listSources()).records
}

function create() {
  form.value = { sourceName: '', sourceCode: '', sourceType: 'NEWS', url: '', fetchMode: 'HTML', fetchIntervalMinutes: 60, enabled: true }
  dialogVisible.value = true
}

function edit(row: MarketInfoSource) {
  form.value = { ...row }
  dialogVisible.value = true
}

async function save() {
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
  await marketApi.testFetch(row.id)
  ElMessage.success('测试抓取已执行')
  await load()
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar"><h2>信息源管理</h2><el-button type="primary" @click="create">新增</el-button></div>
    <el-table :data="sources" border>
      <el-table-column prop="sourceName" label="名称" />
      <el-table-column prop="sourceCode" label="编码" />
      <el-table-column prop="sourceType" label="类型" />
      <el-table-column prop="fetchMode" label="抓取方式" />
      <el-table-column prop="enabled" label="状态"><template #default="{ row }"><el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="220"><template #default="{ row }"><el-button link @click="edit(row)">编辑</el-button><el-button link @click="toggle(row)">{{ row.enabled ? '停用' : '启用' }}</el-button><el-button link @click="testFetch(row)">测试</el-button></template></el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="信息源" width="560px">
      <el-form label-width="100px">
        <el-form-item label="名称"><el-input v-model="form.sourceName" /></el-form-item>
        <el-form-item label="编码"><el-input v-model="form.sourceCode" /></el-form-item>
        <el-form-item label="URL"><el-input v-model="form.url" /></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.sourceType"><el-option value="NEWS" label="新闻" /><el-option value="TENDER" label="招采" /><el-option value="INDUSTRY_DATA" label="行业数据" /><el-option value="COMPETITOR" label="竞争公司" /><el-option value="OTHER" label="其他" /></el-select></el-form-item>
        <el-form-item label="方式"><el-select v-model="form.fetchMode"><el-option value="HTML" label="HTML" /><el-option value="RSS" label="RSS" /><el-option value="API" label="API" /><el-option value="EXTERNAL_IMPORT" label="外部导入" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </section>
</template>
