import request from './request'

export function getConsumptionPage(params) {
  return request({
    url: '/api/consumption/page',
    method: 'get',
    params
  })
}

export function getConsumptionByCheckInId(checkInId) {
  return request({
    url: `/api/consumption/checkin/${checkInId}`,
    method: 'get'
  })
}

export function getConsumptionById(id) {
  return request({
    url: `/api/consumption/${id}`,
    method: 'get'
  })
}

export function createConsumption(data) {
  return request({
    url: '/api/consumption',
    method: 'post',
    data
  })
}

export function cancelConsumption(id, cancelRemark) {
  return request({
    url: `/api/consumption/${id}/cancel`,
    method: 'put',
    data: { cancelRemark }
  })
}

export function settleToBill(checkInId, paymentMethod) {
  return request({
    url: `/api/consumption/settle/${checkInId}`,
    method: 'post',
    data: { paymentMethod }
  })
}
