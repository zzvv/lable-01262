import request from './request'

export function getCustomerPage(params) {
  return request.get('/customer/page', { params })
}

export function getCustomerByPhone(phone) {
  return request.get(`/customer/phone/${phone}`)
}

export function getCustomerByIdCard(idCard) {
  return request.get(`/customer/idcard/${idCard}`)
}

export function getCustomer(id) {
  return request.get(`/customer/${id}`)
}

export function createCustomer(data) {
  return request.post('/customer', data)
}

export function updateCustomer(data) {
  return request.put('/customer', data)
}

export function deleteCustomer(id) {
  return request.delete(`/customer/${id}`)
}

export function exportCustomers(params) {
  return request.get('/customer/export', {
    params,
    responseType: 'blob'
  })
}
