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
    PAID: '已支付',
    COMPLETED: '已完成',
    PAY_FAILED: '支付失败',
    CANCELLED: '已取消',
    CLOSED: '已关闭'
  }
  return mapper[status] || status
}

export function payStatusLabel(status) {
  const mapper = {
    UNPAID: '未支付',
    PAID: '已支付',
    FAILED: '失败',
    CLOSED: '已关闭',
    WAIT_PAY: '待支付',
    SUCCESS: '成功'
  }
  return mapper[status] || status
}

export function sourceTypeLabel(status) {
  const mapper = {
    DIRECT: '立即购买',
    CART: '购物车结算'
  }
  return mapper[status] || status
}

export function roleLabel(roleCode) {
  return roleCode === 'ADMIN' ? '管理员' : '普通用户'
}

export function productStatusLabel(status) {
  const mapper = {
    ON_SALE: '上架中',
    OFF_SALE: '已下架',
    DISABLED: '已禁用',
    DRAFT: '草稿'
  }
  return mapper[status] || status
}

export function batchStatusLabel(status) {
  const mapper = {
    PROCESSING: '处理中',
    COMPLETED: '已完成',
    PARTIAL_FAILED: '部分失败',
    FAILED: '导入失败'
  }
  return mapper[status] || status
}
