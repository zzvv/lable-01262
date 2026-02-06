import request from './request'

export function getCheckInPage(params) {
  return request.get('/checkin/page', { params })
}

export function getCheckIn(id) {
  return request.get(`/checkin/${id}`)
}

export function checkInFromReservation(data) {
  return request.post('/checkin/from-reservation', data)
}

export function walkInCheckIn(data) {
  return request.post('/checkin/walk-in', data)
}

export function checkOut(id, data) {
  return request.post(`/checkin/${id}/checkout`, data)
}

export function updateCheckIn(data) {
  return request.put('/checkin', data)
}
