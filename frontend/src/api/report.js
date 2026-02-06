import request from './request'

export function getDailyReportPage(params) {
  return request.get('/report/daily/page', { params })
}

export function getMonthlyReportPage(params) {
  return request.get('/report/monthly/page', { params })
}

export function generateDailyReport(date) {
  return request.post('/report/daily/generate', null, { params: { date } })
}

export function generateMonthlyReport(year, month) {
  return request.post('/report/monthly/generate', null, { params: { year, month } })
}
