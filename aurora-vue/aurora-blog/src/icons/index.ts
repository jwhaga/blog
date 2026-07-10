import SvgIcon from '@/components/SvgIcon/index.vue' // svg component
import { App } from 'vue'

// register globally
export const registerSvgIcon = (app: App): void => {
  app.component('svg-icon', SvgIcon)
  const req = require.context('./svg', false, /\.svg$/)
  // eslint-disable-next-line
  // 使用 any 是因为 requireContext 为 webpack 提供的 require.context 返回类型，无标准 TS 类型定义
  const requireAll = (requireContext: any) =>
    requireContext.keys().map(requireContext)
  requireAll(req)
}
