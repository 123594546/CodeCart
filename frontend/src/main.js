import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

import App from './App.vue'
import router from './router'
import { bootstrapSession } from './stores/session'
import './styles/main.css'

async function bootstrap() {
  await bootstrapSession()

  const app = createApp(App)
  app.use(router)
  app.use(ElementPlus)
  app.mount('#app')
}

bootstrap()
