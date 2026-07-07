<script setup lang="ts">
import { computed, ref } from 'vue'
import Dashboard from './views/market/intelligence/dashboard.vue'
import ItemList from './views/market/intelligence/item-list.vue'
import SourceList from './views/market/intelligence/source-list.vue'
import TopicList from './views/market/intelligence/topic-list.vue'
import CrawlLog from './views/market/intelligence/crawl-log.vue'
import ReportList from './views/market/intelligence/report-list.vue'

const active = ref('dashboard')
const tabs = [
  { key: 'dashboard', label: '市场情报看板', component: Dashboard },
  { key: 'items', label: '情报列表', component: ItemList },
  { key: 'sources', label: '信息源管理', component: SourceList },
  { key: 'topics', label: '关键词主题', component: TopicList },
  { key: 'crawl', label: '抓取日志', component: CrawlLog },
  { key: 'reports', label: '上报记录', component: ReportList }
]
const current = computed(() => tabs.find(tab => tab.key === active.value)?.component ?? Dashboard)
</script>

<template>
  <el-container class="app-shell">
    <el-aside width="220px" class="side">
      <h1>市场情报监测</h1>
      <el-menu :default-active="active" @select="active = String($event)">
        <el-menu-item v-for="tab in tabs" :key="tab.key" :index="tab.key">{{ tab.label }}</el-menu-item>
      </el-menu>
    </el-aside>
    <el-main>
      <component :is="current" />
    </el-main>
  </el-container>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
}

.side {
  border-right: 1px solid #e5e7eb;
  background: #fff;
}

h1 {
  margin: 0;
  padding: 18px 16px;
  font-size: 18px;
}
</style>
