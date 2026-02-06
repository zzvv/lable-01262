import request from './request'

export function getReservationPage(params) {
  return request.get('/reservation/page', { params })
}

export function getReservation(id) {
  return request.get(`/reservation/${id}`)
}

export function createReservation(data) {
  return request.post('/reservation', data)
}

export function confirmReservation(id) {
  return request.put(`/reservation/${id}/confirm`)
}

export function cancelReservation(id, reason) {
  return request.put(`/reservation/${id}/cancel`, null, { params: { reason } })
}

export function updateReservation(data) {
  return request.put('/reservation', data)
}

export function deleteReservation(id) {
  return request.delete(`/reservation/${id}`)
}
