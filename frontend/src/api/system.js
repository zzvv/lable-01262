import request from './request'

// 用户管理
export function getUserPage(params) {
  return request.get('/system/user/page', { params })
}

export function getUser(id) {
  return request.get(`/system/user/${id}`)
}

export function createUser(data, roleIds) {
  return request.post('/system/user', data, { params: { roleIds } })
}

export function updateUser(data, roleIds) {
  return request.put('/system/user', data, { params: { roleIds } })
}

export function deleteUser(id) {
  return request.delete(`/system/user/${id}`)
}

export function resetPassword(id, newPassword) {
  return request.put(`/system/user/${id}/password`, null, { params: { newPassword } })
}

// 角色管理
export function getRolePage(params) {
  return request.get('/system/role/page', { params })
}

export function getRoleList() {
  return request.get('/system/role/list')
}

export function getRole(id) {
  return request.get(`/system/role/${id}`)
}

export function getRoleMenuIds(id) {
  return request.get(`/system/role/${id}/menus`)
}

export function createRole(data, menuIds) {
  return request.post('/system/role', data, { params: { menuIds } })
}

export function updateRole(data, menuIds) {
  return request.put('/system/role', data, { params: { menuIds } })
}

export function deleteRole(id) {
  return request.delete(`/system/role/${id}`)
}

// 菜单管理
export function getMenuTree() {
  return request.get('/system/menu/tree')
}

export function getMenuList() {
  return request.get('/system/menu/list')
}

export function getMenu(id) {
  return request.get(`/system/menu/${id}`)
}

export function createMenu(data) {
  return request.post('/system/menu', data)
}

export function updateMenu(data) {
  return request.put('/system/menu', data)
}

export function deleteMenu(id) {
  return request.delete(`/system/menu/${id}`)
}
