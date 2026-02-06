# 酒店管理系统 - 演示PPT大纲

> 本文档为PPT制作大纲，可根据此内容制作演示文稿

---

## 第1页：封面

**酒店管理系统**

基于 SpringBoot + Vue3 的前后端分离酒店管理系统

- 项目名称：酒店管理系统
- 技术栈：SpringBoot 2.7 + Vue 3 + MySQL + Redis
- 日期：2026年2月

---

## 第2页：项目背景与目标

**项目背景**
- 传统酒店管理依赖人工，效率低下
- 信息孤岛，数据难以统计分析
- 客户体验有待提升

**项目目标**
- 实现酒店业务全流程数字化管理
- 提供实时数据统计和分析
- 提升运营效率和客户满意度

---

## 第3页：技术架构

```
┌─────────────────────────────────────────┐
│              前端 (Vue 3)               │
│   Element Plus + Vite + ECharts         │
├─────────────────────────────────────────┤
│              后端 (SpringBoot)          │
│   MyBatis Plus + Spring Security        │
├─────────────────────────────────────────┤
│              数据层                      │
│        MySQL 8.0 + Redis 7.x            │
└─────────────────────────────────────────┘
```

**技术选型理由**
- SpringBoot：快速开发、生态完善
- Vue 3：响应式、组件化、性能优秀
- MyBatis Plus：简化CRUD、分页插件
- Redis：Token缓存、提升性能

---

## 第4页：系统功能模块

```
                    酒店管理系统
                         │
    ┌────────┬────────┬──┴──┬────────┬────────┐
    │        │        │     │        │        │
  房间管理  预订管理  入住管理  客户管理  财务管理  系统管理
    │        │        │     │        │        │
  ·房型    ·预订列表 ·入住列表 ·客户列表 ·账单管理 ·用户管理
  ·房间    ·新建预订 ·办理入住 ·会员管理 ·统计报表 ·角色管理
  ·房态    ·预订确认 ·退房结算          ·日/月报  ·菜单管理
```

---

## 第5页：核心业务流程

**预订-入住-退房 全流程**

```
创建预订 → 确认预订 → 分配房间 → 办理入住 → 客房消费 → 退房结算
   │          │          │          │          │          │
   ▼          ▼          ▼          ▼          ▼          ▼
 生成订单   更新状态   锁定房间   登记住客   生成账单   费用结算
```

**状态流转**
- 预订：待确认 → 已确认 → 已入住 → 已完成
- 房间：空闲 → 已预订 → 入住中 → 清洁中 → 空闲

---

## 第6页：技术难点一 - RBAC权限控制

**问题**：如何实现灵活的权限控制？

**解决方案**：基于RBAC模型的权限设计

```
用户 ←→ 角色 ←→ 菜单/权限
  │       │        │
sys_user  sys_role  sys_menu
     \      │      /
      sys_user_role
      sys_role_menu
```

**实现要点**
- JWT Token 认证
- Spring Security 权限校验
- 动态菜单加载
- 按钮级权限控制

---

## 第7页：技术难点二 - JWT认证流程

**问题**：前后端分离如何实现安全认证？

**解决方案**：JWT + Redis 双重验证

```
登录请求 → 验证密码 → 生成JWT → 存入Redis → 返回Token
                                    ↓
请求API → 携带Token → 验证JWT → 查Redis → 执行业务
```

**关键代码**
```java
// JWT生成
String token = Jwts.builder()
    .setSubject(username)
    .claim("userId", userId)
    .setExpiration(expireDate)
    .signWith(SignatureAlgorithm.HS512, secret)
    .compact();

// Redis缓存
redisTemplate.opsForValue().set(
    "login:" + userId, loginUser, 24, TimeUnit.HOURS);
```

---

## 第8页：技术难点三 - 全局异常处理

**问题**：如何统一处理各类异常？

**解决方案**：@RestControllerAdvice 全局异常处理

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public Result<?> handleAccessDeniedException() {
        return Result.error(403, "权限不足");
    }
}
```

**统一响应格式**
```json
{ "code": 200, "message": "成功", "data": {...} }
```

---

## 第9页：技术难点四 - 定时报表生成

**问题**：如何自动生成日报/月报？

**解决方案**：Quartz 定时任务框架

```java
@Component
public class DailyReportJob implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        // 统计昨日数据
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        // 查询营收、入住、预订等数据
        BigDecimal revenue = billMapper.sumByDate(yesterday);
        int checkInCount = checkInMapper.countByDate(yesterday);
        
        // 保存日报表
        dailyReportMapper.insert(report);
    }
}
```

**Cron配置**
- 日报：`0 0 1 * * ?` (每天凌晨1点)
- 月报：`0 0 2 1 * ?` (每月1号凌晨2点)

---

## 第10页：技术难点五 - Swagger兼容性

**问题**：Springfox 3.0 与 SpringBoot 2.7 不兼容

**错误现象**
```
NullPointerException at springfox...
```

**解决方案**：BeanPostProcessor 过滤 HandlerMapping

```java
@Bean
public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
    return new BeanPostProcessor() {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            if (bean instanceof WebMvcRequestHandlerProvider) {
                customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
            }
            return bean;
        }
    };
}
```

---

## 第11页：数据库设计亮点

**设计原则**
- 逻辑删除：deleted 字段，保留历史数据
- 状态机：status 字段管理业务状态
- 审计字段：create_time, update_time 自动维护

**索引优化**
```sql
-- 高频查询字段建立索引
KEY idx_status (status)
KEY idx_check_in_date (check_in_date)
KEY idx_customer (customer_id)

-- 唯一约束保证数据完整性
UNIQUE KEY uk_room_number (room_number)
UNIQUE KEY uk_reservation_no (reservation_no)
```

**表数量**：15张表，覆盖完整业务场景

---

## 第12页：前端技术亮点

**Vue 3 Composition API**
```javascript
// 响应式数据
const tableData = ref([])
const loading = ref(false)

// 组合式函数
const { data, refresh } = useTable(fetchList)
```

**Element Plus 组件**
- 表格分页、搜索、排序
- 表单验证
- 弹窗交互
- 图表展示 (ECharts)

**路由权限**
- 动态路由生成
- 路由守卫鉴权
- 菜单权限过滤

---

## 第13页：系统演示

**演示流程**
1. 登录系统（不同角色）
2. 首页仪表盘
3. 房间管理 → 房态总览
4. 新建预订 → 确认预订
5. 办理入住 → 登记住客
6. 客房消费 → 退房结算
7. 查看报表统计
8. 系统管理（用户/角色/菜单）

---

## 第14页：项目总结

**完成功能**
- ✅ 完整的RBAC权限系统
- ✅ 预订-入住-退房全流程
- ✅ 财务账单管理
- ✅ 自动报表生成
- ✅ Docker容器化部署

**技术收获**
- SpringBoot + Vue 前后端分离实践
- JWT + Spring Security 安全认证
- MyBatis Plus 高效数据访问
- Quartz 定时任务调度

**可优化方向**
- 添加更多单元测试
- 引入消息队列处理异步任务
- 增加数据导入导出功能
- 移动端适配

---

## 第15页：Q&A

**感谢观看！**

欢迎提问交流

---

## 附录：关键代码位置

| 功能 | 文件路径 |
|------|----------|
| JWT工具类 | backend/src/.../security/JwtUtils.java |
| 全局异常处理 | backend/src/.../config/GlobalExceptionHandler.java |
| 权限配置 | backend/src/.../security/SecurityConfig.java |
| 定时任务 | backend/src/.../job/DailyReportJob.java |
| 前端路由 | frontend/src/router/index.js |
| API请求封装 | frontend/src/api/request.js |
