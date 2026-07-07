import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: '/api/v1/market/intelligence',
  timeout: 15000
})

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body && typeof body.code === 'number' && body.code !== 0) {
      ElMessage.error(body.message || '请求失败')
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body?.data ?? body
  },
  error => {
    ElMessage.error(error?.message || '网络异常')
    return Promise.reject(error)
  }
)

export default request
