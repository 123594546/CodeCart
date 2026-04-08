import http from './http'

export function fetchCategories() {
  return http.get('/categories')
}

export function fetchProducts(params) {
  return http.get('/products', { params })
}

export function fetchProductDetail(id) {
  return http.get(`/products/${id}`)
}

export function fetchCart() {
  return http.get('/cart')
}

export function addToCart(payload) {
  return http.post('/cart', payload)
}

export function updateCart(payload) {
  return http.patch('/cart', payload)
}

export function deleteCartItem(cartItemId) {
  return http.delete(`/cart/${cartItemId}`)
}

export function createDirectOrder(payload) {
  return http.post('/orders/direct', payload)
}

export function createCartOrder() {
  return http.post('/orders/cart')
}

export function payOrder(orderNo) {
  return http.post(`/orders/${orderNo}/pay`)
}

export function fetchOrders() {
  return http.get('/orders')
}

export function fetchOrderDetail(orderNo) {
  return http.get(`/orders/${orderNo}`)
}

export function fetchPurchasedCodes() {
  return http.get('/orders/codes')
}
