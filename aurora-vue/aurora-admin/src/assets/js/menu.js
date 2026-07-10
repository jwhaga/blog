import Layout from '@/layout/index.vue'
import router from '@/router'
import store from '@/store'
import axios from 'axios'
import Vue from 'vue'

const LAYOUT_COMPONENT = 'Layout'
const ICONFONT_PREFIX = 'iconfont '
const LOGIN_PATH = '/login'

export function generaMenu() {
  axios
    .get('/api/admin/user/menus')
    .then(({ data }) => {
      if (!data.flag) {
        Vue.prototype.$message.error(data.message)
        router.push({ path: LOGIN_PATH })
        return
      }
      const userMenus = data.data
      userMenus.forEach((item) => {
        if (item.icon != null) {
          item.icon = ICONFONT_PREFIX + item.icon
        }
        if (item.component === LAYOUT_COMPONENT) {
          item.component = Layout
        }
        if (item.children && item.children.length > 0) {
          item.children.forEach((route) => {
            route.icon = ICONFONT_PREFIX + route.icon
            route.component = loadView(route.component)
          })
        }
      })
      store.commit('saveUserMenus', userMenus)
      userMenus.forEach((item) => {
        router.addRoute(item)
      })
    })
    .catch(() => {})
}

export const loadView = (view) => {
  return (resolve) => require([`@/views${view}`], resolve)
}
