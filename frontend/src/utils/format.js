export function formatPrice(value) {
  const amount = Number(value || 0)
  return new Intl.NumberFormat('zh-CN', {
    style: 'currency',
    currency: 'CNY',
    minimumFractionDigits: 2
  }).format(amount)
}

export function formatDateTime(value) {
  if (!value) return '--'
  return value.replace('T', ' ')
}

export function orderStatusLabel(status) {
  const mapper = {
    PENDING_PAYMENT: '待支付',
    COMPLETED: '已完成',
    PAY_FAILED: '支付失败',
    CANCELLED: '已取消'
  }
  return mapper[status] || status
}

export function payStatusLabel(status) {
  const mapper = {
    UNPAID: '未支付',
    PAID: '已支付',
    FAILED: '失败'
  }
  return mapper[status] || status
}
