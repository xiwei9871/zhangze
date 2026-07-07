<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { marketApi } from '@/api/market/intelligence'
import type { MarketKeywordTopic } from '@/api/market/intelligence/types'

const topics = ref<MarketKeywordTopic[]>([])
const dialogVisible = ref(false)
const form = ref<MarketKeywordTopic>({ topicName: '', includeKeywords: [], excludeKeywords: [], matchScope: 'TITLE_AND_CONTENT', importanceLevel: 'MEDIUM', enabled: true })
const includeText = ref('')
const excludeText = ref('')

async function load() {
  topics.value = (await marketApi.listTopics()).records
}

function create() {
  form.value = { topicName: '', includeKeywords: [], excludeKeywords: [], matchScope: 'TITLE_AND_CONTENT', importanceLevel: 'MEDIUM', enabled: true }
  includeText.value = ''
  excludeText.value = ''
  dialogVisible.value = true
}

function edit(row: MarketKeywordTopic) {
  form.value = { ...row }
  includeText.value = row.includeKeywords.join('\n')
  excludeText.value = row.excludeKeywords.join('\n')
  dialogVisible.value = true
}

async function save() {
  form.value.includeKeywords = includeText.value.split(/\s+/).filter(Boolean)
  form.value.excludeKeywords = excludeText.value.split(/\s+/).filter(Boolean)
  await marketApi.saveTopic(form.value)
  dialogVisible.value = false
  ElMessage.success('关键词主题已保存')
  await load()
}

async function toggle(row: MarketKeywordTopic) {
  if (!row.id) return
  row.enabled ? await marketApi.disableTopic(row.id) : await marketApi.enableTopic(row.id)
  await load()
}

onMounted(load)
</script>

<template>
  <section class="page">
    <div class="toolbar"><h2>关键词主题管理</h2><el-button type="primary" @click="create">新增</el-button></div>
    <el-table :data="topics" border>
      <el-table-column prop="topicName" label="主题" />
      <el-table-column prop="includeKeywords" label="包含词"><template #default="{ row }">{{ row.includeKeywords.join('、') }}</template></el-table-column>
      <el-table-column prop="importanceLevel" label="级别" width="110" />
      <el-table-column prop="enabled" label="状态" width="90"><template #default="{ row }"><el-tag>{{ row.enabled ? '启用' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="150"><template #default="{ row }"><el-button link @click="edit(row)">编辑</el-button><el-button link @click="toggle(row)">{{ row.enabled ? '停用' : '启用' }}</el-button></template></el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="关键词主题" width="560px">
      <el-form label-width="100px">
        <el-form-item label="主题"><el-input v-model="form.topicName" /></el-form-item>
        <el-form-item label="包含词"><el-input v-model="includeText" type="textarea" :rows="4" /></el-form-item>
        <el-form-item label="排除词"><el-input v-model="excludeText" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="匹配范围"><el-select v-model="form.matchScope"><el-option value="TITLE" label="标题" /><el-option value="CONTENT" label="正文" /><el-option value="TITLE_AND_CONTENT" label="标题+正文" /></el-select></el-form-item>
        <el-form-item label="级别"><el-select v-model="form.importanceLevel"><el-option value="LOW" label="低" /><el-option value="MEDIUM" label="中" /><el-option value="HIGH" label="高" /><el-option value="URGENT" label="紧急" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </section>
</template>
