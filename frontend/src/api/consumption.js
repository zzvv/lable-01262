import request from './request'

export function getConsumptionPage(params) {
  return request.get('/consumption/page', { params })
}

export function getConsumption(id) {
  return request.get(`/consumption/${id}`)
}

export function getConsumptionByCheckInId(checkInId) {
  return request.get(`/consumption/checkin/${checkInId}`)
}

export function createConsumption(data) {
  return request.post('/consumption', data)
}

export function updateConsumption(data) {
  return request.put('/consumption', data)
}

export function deleteConsumption(id) {
  return request.delete(`/consumption/${id}`)
}

export function cancelConsumption(id) {
  return request.put(`/consumption/${id}/cancel`)
}

export function payConsumption(id, paymentMethod) {
  return request.put(`/consumption/${id}/pay`, null, { params: { paymentMethod } })
}
