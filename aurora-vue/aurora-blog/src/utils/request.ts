import axios from 'axios'
import type { AxiosResponse } from 'axios'

// ============================================================
// Encapsulated Axios Instance
// ============================================================
// All API calls should use this instance instead of the raw axios.
// Request interceptor: injects JWT Bearer token from sessionStorage.
// Response interceptor: unwraps errors and rejects on non-success codes.
// ============================================================

// ----- Response Type -----
export interface ApiResult<T = unknown> {
  code: number
  message: string
  data: T
  flag?: boolean
}

// ----- Axios instance -----
const request = axios.create({
  timeout: 10000
})

// ----- Request interceptor: inject auth token -----
request.interceptors.request.use(
  (config: any) => {
    const token = sessionStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// ----- Response interceptor: unwrap errors -----
request.interceptors.response.use(
  (response: AxiosResponse<ApiResult>) => {
    const { data } = response
    // If the backend returns a non-success code, treat it as an error
    if (data && data.code && data.code !== 20000) {
      return Promise.reject(new Error(data.message || 'Request failed'))
    }
    return response
  },
  (error) => {
    if (error.response) {
      const status = error.response.status
      const messages: Record<number, string> = {
        400: 'Bad request',
        401: 'Not authenticated',
        403: 'Access denied',
        404: 'Resource not found',
        500: 'Internal server error',
        502: 'Bad gateway',
        503: 'Service unavailable'
      }
      return Promise.reject(new Error(messages[status] || 'HTTP ' + status))
    }
    if (error.code === 'ECONNABORTED') {
      return Promise.reject(new Error('Request timed out'))
    }
    return Promise.reject(error)
  }
)

export default request
