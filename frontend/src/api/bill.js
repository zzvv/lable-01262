import request from './request'

export function getBillPage(params) {
  return request.get('/bill/page', { params })
}

export function getBillsByCheckIn(checkInId) {
  return request.get(`/bill/checkin/${checkInId}`)
}

export function getBill(id) {
  return request.get(`/bill/${id}`)
}

export function payBill(id, paymentMethod) {
  return request.put(`/bill/${id}/pay`, null, { params: { paymentMethod } })
}

export function getTodayRevenue() {
  return request.get('/bill/stats/today')
}

export function getMonthRevenue() {
  return request.get('/bill/stats/month')
}

export function getBillTypeStats() {
  return request.get('/bill/stats/type')
}

export function exportBills(params) {
  return request.get('/bill/export', {
    params,
    responseType: 'blob'
  })
}
