import { createRouter, createWebHistory } from 'vue-router'

import { sessionStore } from '@/stores/session'

const HomeView = () => import('@/views/HomeView.vue')
const AuthView = () => import('@/views/AuthView.vue')
const ProductDetailView = () => import('@/views/ProductDetailView.vue')
const CartView = () => import('@/views/CartView.vue')
const OrdersView = () => import('@/views/OrdersView.vue')
const CodesView = () => import('@/views/CodesView.vue')
const AdminDashboardView = () => import('@/views/AdminDashboardView.vue')

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { title: '发现虚拟商品' }
    },
    {
      path: '/auth',
      name: 'auth',
      component: AuthView,
      meta: { guestOnly: true, title: '登录与注册' }
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: ProductDetailView,
      props: true,
      meta: { title: '商品详情' }
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
      meta: { requiresAuth: true, title: '购物车' }
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrdersView,
      meta: { requiresAuth: true, title: '我的订单' }
    },
    {
      path: '/codes',
      name: 'codes',
      component: CodesView,
      meta: { requiresAuth: true, title: '我的兑换码' }
    },
    {
      path: '/admin',
      name: 'admin-dashboard',
      component: AdminDashboardView,
      meta: { requiresAuth: true, requiresAdmin: true, title: '管理工作台' }
    }
  ],
  scrollBehavior() {
    return { top: 0 }
  }
})

router.beforeEach((to) => {
  document.title = `${to.meta.title ?? 'CodeCart'} | CodeCart`

  if (to.meta.requiresAuth && !sessionStore.isAuthenticated) {
    return {
      name: 'auth',
      query: { redirect: to.fullPath }
    }
  }

  if (to.meta.requiresAdmin && !sessionStore.isAdmin) {
    return { name: 'home' }
  }

  if (to.meta.guestOnly && sessionStore.isAuthenticated) {
    return { name: 'home' }
  }

  return true
})

export default router
