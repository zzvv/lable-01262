import request from './request'

export function getStatistics() {
  return request.get('/dashboard/statistics')
}

export function getChartData() {
  return request.get('/dashboard/charts')
}
