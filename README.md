# 酒店管理系统 (Hotel Management System)

## How to Run

### Docker 方式（推荐）

```bash
# 克隆项目后，在根目录执行
docker compose up --build -d

# 等待所有服务启动完成（约2-3分钟）
docker compose ps

# 查看日志
docker compose logs -f
```

访问地址：
- 前端界面：http://localhost:8081
- 后端API：http://localhost:8080
- Swagger文档：http://localhost:8080/swagger-ui.html

### 本地运行方式

#### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

#### 1. 数据库配置

```bash
# 创建数据库
mysql -u root -p
CREATE DATABASE hotel_management DEFAULT CHARACTER SET utf8mb4;

# 导入数据
mysql -u root -p hotel_management < database/hotel_management_full.sql
```

#### 2. 启动后端

```bash
cd backend

# 修改配置（如需要）
# 编辑 src/main/resources/application-dev.yml 中的数据库和Redis连接信息

# 运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

后端默认配置：
- MySQL: localhost:3306, 用户名 root, 密码 root123456
- Redis: localhost:6379

#### 3. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev
```

访问地址：
- 前端界面：http://localhost:5173
- 后端API：http://localhost:8080
- Swagger文档：http://localhost:8080/swagger-ui.html

## Services

| 服务 | 端口 | 说明 |
|------|------|------|
| frontend | 8081 | Vue3 前端服务 |
| backend | 8080 | SpringBoot 后端服务 |
| mysql | 3262 | MySQL 8.0 数据库 |
| redis | 6379 | Redis 缓存服务 |

## 测试账号

| 角色 | 用户名 | 密码 | 权限说明 |
|------|--------|------|----------|
| 超级管理员 | admin | admin123 | 所有权限 |
| 前台人员 | reception | 123456 | 预订、入住、退房管理 |
| 普通用户 | user | 123456 | 在线预订、个人信息管理 |

## 题目内容

Java SpringBoot + MyBatis + MySQL + Vue 前后端分离项目大作业要求 

一、项目背景与目标 

本项目旨在通过SpringBoot、MyBatis、MySQL和Vue等技术栈，开发一个完整的前后端分离企业级应用（如电商后台管理系统、智能考勤系统或在线教育平台）。要求实现高内聚低耦合的架构设计，掌握RESTful API开发、权限控制、数据可视化等核心能力。 
二、技术栈要求 

后端技术栈 
框架：SpringBoot 2.7+ 

ORM：mybatis(MyBatis Plus（支持代码生成器）) 
数据库：MySQL 8.0+（需设计ER图,需数据库表结构） 
其他：Redis缓存、Swagger接口文档、Lombok简化代码 
前端技术栈 
框架：Vue 3 + Element-UI（跨端可选） 
工具：Vue Router、Axios、ECharts数据可视化 
工程化：Vite构建工具、ES6+语法 

三、核心功能模块 

1. 基础功能模块 
用户管理：注册/登录（JWT认证）、【角色权限控制（RBAC模型）】 
数据管理：CRUD操作（分页查询、条件筛选）、【Excel导入导出】 

2. 进阶功能模块（可选1-2个） 
微服务扩展：Spring Cloud Alibaba实现服务拆分（Nacos注册中心） 
实时通信：WebSocket实现消息推送（如订单状态变更） 
数据分析：定时任务生成报表（Quartz集成） 
多端适配：Uni-app开发移动端（H5/小程序） 

四、开发规范与要求 
1. 代码规范 
后端：遵循《阿里巴巴Java开发手册》，Controller层返回统一JSON格式 
前端：组件化开发，使用Vuex管理状态，axios拦截器处理请求 
数据库：字段命名规范（小写+下划线），索引优化查询性能 

2. 文档要求 
技术文档：包含ER图、API接口文档（Swagger）、部署手册 
系统设计文档：说明架构设计、权限流程、异常处理机制 
演示视频：5分钟功能演示（含关键代码讲解） 

3. 质量要求 
代码质量：通过SonarQube检测（无严重漏洞，重复率<15%） 
性能测试：使用JMeter进行并发测试（响应时间<2s） 
安全性：防范SQL注入、XSS攻击，密码加密存储 

五、项目交付物 

源代码：完整前后端工程（Git仓库提交记录） 
数据库脚本：DDL和DML语句（含测试数据） 
部署包：Docker镜像或War/Jar包 
演示PPT：10页内，包含技术难点与解决方案 
word文档：主要功能说明/数据库设计说明 



提示：建议选择与企业实际业务相关的场景（如库存管理、学生成绩系统），避免开发过于通用的功能。可参考RuoYi框架的模块设计，但需根据需求进行定制化改造。 （AI生成） 

帮我按照要求做一个酒店管理系统，数据库生成可以直接在nevicat运行

### 项目背景与目标
本项目旨在通过SpringBoot、MyBatis、MySQL和Vue等技术栈，开发一个完整的前后端分离企业级应用——酒店管理系统。实现高内聚低耦合的架构设计，掌握RESTful API开发、权限控制、数据可视化等核心能力。

### 技术栈要求
- 后端：SpringBoot 2.7+、MyBatis Plus、MySQL 8.0+、Redis缓存、Swagger接口文档、Lombok
- 前端：Vue 3 + Element Plus、Vue Router、Pinia、Axios、ECharts数据可视化、Vite构建工具

### 核心功能模块
1. 基础功能模块
   - 用户管理：注册/登录（JWT认证）、角色权限控制（RBAC模型）
   - 数据管理：CRUD操作（分页查询、条件筛选）、Excel导入导出

2. 业务功能模块
   - 房型管理：房型CRUD、价格设置
   - 房间管理：房间状态管理、房间分配
   - 预订管理：在线预订、预订查询、取消预订
   - 入住管理：办理入住、退房结算
   - 客户管理：客户信息、入住历史
   - 财务管理：账单管理、统计报表

3. 进阶功能模块
   - 数据可视化：ECharts展示入住率、营收趋势
   - Excel导入导出：客户数据、财务报表导出

---

## 项目介绍

### 系统架构

```
┌─────────────────────────────────────────────────────────────┐
│                        Nginx (前端)                          │
│                      Port: 8081                              │
└─────────────────────────┬───────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────┐
│                   SpringBoot (后端)                          │
│                      Port: 8080                              │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
│  │Controller│  │ Service │  │ Mapper  │  │Security │        │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘        │
└───────────────────┬─────────────────┬───────────────────────┘
                    │                 │
                    ▼                 ▼
┌───────────────────────┐  ┌─────────────────────────────────┐
│    MySQL 8.0          │  │         Redis                    │
│    Port: 3262         │  │         Port: 6379               │
└───────────────────────┘  └─────────────────────────────────┘
```

### 功能模块

1. **系统管理**
   - 用户管理：用户增删改查、角色分配
   - 角色管理：角色权限配置
   - 菜单管理：动态菜单配置

2. **房间管理**
   - 房型管理：房型信息维护、价格设置
   - 房间管理：房间状态监控、房间信息维护

3. **预订管理**
   - 在线预订：客户预订房间
   - 预订查询：查询预订记录
   - 预订处理：确认/取消预订

4. **入住管理**
   - 办理入住：预订转入住、散客入住
   - 退房结算：费用结算、退房处理

5. **客户管理**
   - 客户信息：客户档案管理
   - 入住历史：历史记录查询

6. **财务管理**
   - 账单管理：账单查询、收款
   - 统计报表：营收统计、入住率分析

### 数据库设计

详见 `backend/src/main/resources/db/schema.sql`

### API文档

启动项目后访问：http://localhost:8080/swagger-ui.html

## 项目结构

```
hotel-management/
├── backend/                          # 后端 SpringBoot 项目
│   ├── src/main/java/com/hotel/
│   │   ├── common/                   # 通用类（Result、PageResult、异常等）
│   │   ├── config/                   # 配置类（CORS、Redis、Swagger等）
│   │   ├── controller/               # 控制器层
│   │   ├── dto/                      # 数据传输对象
│   │   ├── entity/                   # 实体类
│   │   ├── job/                      # 定时任务（日报、月报生成）
│   │   ├── mapper/                   # MyBatis Mapper 接口
│   │   ├── security/                 # 安全相关（JWT、Spring Security）
│   │   └── service/                  # 服务层
│   ├── src/main/resources/
│   │   ├── db/                       # 数据库脚本
│   │   ├── mapper/                   # MyBatis XML 映射文件
│   │   └── application*.yml          # 配置文件
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                         # 前端 Vue3 项目
│   ├── src/
│   │   ├── api/                      # API 接口封装
│   │   ├── layout/                   # 布局组件
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # Pinia 状态管理
│   │   ├── styles/                   # 全局样式
│   │   └── views/                    # 页面组件
│   │       ├── checkin/              # 入住管理
│   │       ├── customer/             # 客户管理
│   │       ├── dashboard/            # 仪表盘
│   │       ├── finance/              # 财务管理
│   │       ├── login/                # 登录页
│   │       ├── reservation/          # 预订管理
│   │       ├── room/                 # 房间管理
│   │       └── system/               # 系统管理
│   ├── Dockerfile
│   └── package.json
├── database/                         # 数据库完整脚本
│   └── hotel_management_full.sql
├── docs/                             # 项目文档
├── docker-compose.yml
└── README.md
```

## 文档说明

`docs/` 目录包含项目相关文档：

| 文档 | 说明 |
|------|------|
| [系统设计文档.md](docs/系统设计文档.md) | 系统架构设计、技术选型、模块设计 |
| [系统功能说明书.md](docs/系统功能说明书.md) | 各功能模块详细说明 |
| [数据库设计说明书.md](docs/数据库设计说明书.md) | 数据库表结构、字段说明 |
| [ER图.md](docs/ER图.md) | 数据库实体关系图 |
| [异常处理机制说明.md](docs/异常处理机制说明.md) | 全局异常处理、错误码定义 |
| [部署手册.md](docs/部署手册.md) | Docker 部署、本地部署指南 |
| [测试报告.md](docs/测试报告.md) | 功能测试、性能测试结果 |
| [演示PPT大纲.md](docs/演示PPT大纲.md) | 项目演示 PPT 内容大纲 |
| [功能演示视频脚本.md](docs/功能演示视频脚本.md) | 演示视频录制脚本 |
