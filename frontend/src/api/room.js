import request from './request'

// 房型管理
export function getRoomTypePage(params) {
  return request.get('/room/type/page', { params })
}

export function getRoomTypeList() {
  return request.get('/room/type/list')
}

export function getRoomType(id) {
  return request.get(`/room/type/${id}`)
}

export function createRoomType(data) {
  return request.post('/room/type', data)
}

export function updateRoomType(data) {
  return request.put('/room/type', data)
}

export function deleteRoomType(id) {
  return request.delete(`/room/type/${id}`)
}

// 房间管理
export function getRoomPage(params) {
  return request.get('/room/page', { params })
}

export function getAvailableRooms(roomTypeId) {
  return request.get('/room/available', { params: { roomTypeId } })
}

export function getRoom(id) {
  return request.get(`/room/${id}`)
}

export function createRoom(data) {
  return request.post('/room', data)
}

export function updateRoom(data) {
  return request.put('/room', data)
}

export function updateRoomStatus(id, status) {
  return request.put(`/room/${id}/status`, null, { params: { status } })
}

export function deleteRoom(id) {
  return request.delete(`/room/${id}`)
}

export function getRoomStatusStats() {
  return request.get('/room/stats/status')
}

export function getRoomFloorStats() {
  return request.get('/room/stats/floor')
}
