import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/css/index.css'
import './assets/css/iconfont.css'
import config from './assets/js/config'
import axios from 'axios'
import VueAxios from 'vue-axios'
import ECharts from 'vue-echarts'
import 'echarts/lib/chart/line'
import 'echarts/lib/chart/pie'
import 'echarts/lib/chart/bar'
import 'echarts/lib/chart/map'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/title'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import mavonEditor from 'mavon-editor'
import 'mavon-editor/dist/css/index.css'
import VueCalendarHeatmap from 'vue-calendar-heatmap'
import tagCloud from './components/tag-cloud'
import dayjs from 'dayjs'
import MdKatex from '@iktakahiro/markdown-it-katex'
import mermaidPlugin from '@agoose77/markdown-it-mermaid'

// 响应状态码常量
const RESPONSE_CODE = {
  TOKEN_INVALID: 40001,
  SYSTEM_ERROR: 50000
}
const LOGIN_PATH = '/login'
const TOKEN_HEADER = 'Authorization'
const TOKEN_STORAGE_KEY = 'token'

Vue.config.productionTip = false
Vue.prototype.config = config
Vue.use(mavonEditor)
Vue.use(ElementUI)
Vue.use(tagCloud)
Vue.use(VueCalendarHeatmap)
Vue.use(VueAxios, axios)
Vue.component('v-chart', ECharts)
Vue.prototype.$moment = dayjs
mavonEditor.markdownIt.set({}).use(MdKatex).use(mermaidPlugin)

Vue.filter('date', (value, formatStr = 'YYYY-MM-DD') => {
  return dayjs(value).format(formatStr)
})

Vue.filter('dateTime', (value, formatStr = 'YYYY-MM-DD HH:mm:ss') => {
  return dayjs(value).format(formatStr)
})

NProgress.configure({
  easing: 'ease',
  speed: 500,
  showSpinner: false,
  trickleSpeed: 200,
  minimum: 0.3
})

// 路由守卫：未登录用户重定向至登录页
router.beforeEach((to, from, next) => {
  NProgress.start()
  if (to.path === LOGIN_PATH) {
    next()
  } else if (!store.state.userInfo) {
    next({ path: LOGIN_PATH })
  } else {
    next()
  }
})

router.afterEach(() => {
  NProgress.done()
})

// 请求拦截器：携带 token
axios.interceptors.request.use((requestConfig) => {
  requestConfig.headers[TOKEN_HEADER] = 'Bearer ' + sessionStorage.getItem(TOKEN_STORAGE_KEY)
  return requestConfig
})

// 响应拦截器：统一处理业务错误码
axios.interceptors.response.use(
  (response) => {
    handleResponseCode(response.data.code, response.data.message)
    return response
  },
  (error) => {
    return Promise.reject(error)
  }
)

function handleResponseCode(code, message) {
  if (code === RESPONSE_CODE.TOKEN_INVALID) {
    Vue.prototype.$message({ type: 'error', message })
    router.push({ path: LOGIN_PATH })
  } else if (code === RESPONSE_CODE.SYSTEM_ERROR) {
    Vue.prototype.$message({ type: 'error', message })
  }
}

new Vue({
  router,
  store,
  render: (h) => h(App)
}).$mount('#app')
