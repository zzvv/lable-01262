# 酒店管理系统 - 数据库ER图

## 1. ER图概览

```
┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    系统管理模块                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│   ┌──────────────┐         ┌──────────────────┐         ┌──────────────┐               │
│   │   sys_user   │         │  sys_user_role   │         │   sys_role   │               │
│   ├──────────────┤    1:N  ├──────────────────┤  N:1    ├──────────────┤               │
│   │ PK: id       │◄────────│ FK: user_id      │────────►│ PK: id       │               │
│   │ username     │         │ FK: role_id      │         │ role_name    │               │
│   │ password     │         └──────────────────┘         │ role_key     │               │
│   │ nickname     │                                      │ status       │               │
│   │ email        │                                      └──────┬───────┘               │
│   │ phone        │                                             │                       │
│   │ status       │                                             │ 1:N                   │
│   └──────────────┘                                             ▼                       │
│                                                        ┌──────────────────┐            │
│                                                        │  sys_role_menu   │            │
│                                                        ├──────────────────┤            │
│                                                        │ FK: role_id      │            │
│   ┌──────────────┐                                     │ FK: menu_id      │            │
│   │   sys_log    │                                     └────────┬─────────┘            │
│   ├──────────────┤                                              │ N:1                  │
│   │ PK: id       │                                              ▼                      │
│   │ FK: user_id  │                                     ┌──────────────┐                │
│   │ operation    │                                     │   sys_menu   │                │
│   │ method       │                                     ├──────────────┤                │
│   │ ip           │                                     │ PK: id       │                │
│   │ create_time  │                                     │ parent_id    │◄───┐ 自关联    │
│   └──────────────┘                                     │ menu_name    │────┘           │
│                                                        │ path         │                │
│                                                        │ component    │                │
│                                                        │ perms        │                │
│                                                        │ menu_type    │                │
│                                                        └──────────────┘                │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    业务核心模块                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│   ┌──────────────┐         ┌──────────────┐         ┌──────────────┐                   │
│   │  room_type   │    1:N  │     room     │    1:N  │   check_in   │                   │
│   ├──────────────┤◄────────├──────────────┤◄────────├──────────────┤                   │
│   │ PK: id       │         │ PK: id       │         │ PK: id       │                   │
│   │ type_name    │         │ room_number  │         │ check_in_no  │                   │
│   │ price        │         │FK:room_type_id         │FK:reservation_id                │
│   │ bed_type     │         │ floor        │         │ FK:customer_id│                  │
│   │ max_guests   │         │ status       │         │ FK: room_id  │                   │
│   │ area         │         │ remark       │         │ check_in_time│                   │
│   │ description  │         └──────────────┘         │ check_out_time                  │
│   │ status       │                │                 │ room_price   │                   │
│   └──────────────┘                │                 │ total_price  │                   │
│          │                        │                 │ deposit      │                   │
│          │ 1:N                    │ 1:N             │ status       │                   │
│          ▼                        ▼                 └──────┬───────┘                   │
│   ┌──────────────┐         ┌──────────────┐                │                          │
│   │ reservation  │         │              │                │ 1:N                      │
│   ├──────────────┤         │              │                ▼                          │
│   │ PK: id       │         │              │        ┌────────────────┐                 │
│   │reservation_no│         │              │        │ check_in_guest │                 │
│   │FK:customer_id│         │              │        ├────────────────┤                 │
│   │FK:room_type_id         │              │        │ PK: id         │                 │
│   │ FK: room_id  │         │              │        │ FK:check_in_id │                 │
│   │check_in_date │         │              │        │ name           │                 │
│   │check_out_date│         │              │        │ id_card        │                 │
│   │ total_price  │         │              │        │ phone          │                 │
│   │ status       │         │              │        │ is_primary     │                 │
│   │ source       │         │              │        └────────────────┘                 │
│   └──────┬───────┘         │              │                                           │
│          │                 │              │                                           │
│          │ N:1             │              │                                           │
│          ▼                 │              │                                           │
│   ┌──────────────┐         │              │                                           │
│   │   customer   │◄────────┘              │                                           │
│   ├──────────────┤                        │                                           │
│   │ PK: id       │                        │                                           │
│   │ name         │                        │                                           │
│   │ id_card      │                        │                                           │
│   │ phone        │                        │                                           │
│   │ email        │                        │                                           │
│   │ member_level │                        │                                           │
│   │ points       │                        │                                           │
│   │total_consumption                      │                                           │
│   │ visit_count  │                        │                                           │
│   └──────┬───────┘                        │                                           │
│          │                                │                                           │
│          │ 1:N                            │                                           │
│          ▼                                │                                           │
│   ┌──────────────┐                        │                                           │
│   │     bill     │◄───────────────────────┘                                           │
│   ├──────────────┤         1:N (check_in -> bill)                                     │
│   │ PK: id       │                                                                    │
│   │ bill_no      │                                                                    │
│   │FK:check_in_id│                                                                    │
│   │FK:customer_id│                                                                    │
│   │ bill_type    │                                                                    │
│   │ item_name    │                                                                    │
│   │ amount       │                                                                    │
│   │payment_method│                                                                    │
│   │payment_status│                                                                    │
│   └──────────────┘                                                                    │
└─────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────────┐
│                                    报表统计模块                                          │
├─────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                         │
│   ┌──────────────────┐                    ┌──────────────────┐                         │
│   │   daily_report   │                    │  monthly_report  │                         │
│   ├──────────────────┤                    ├──────────────────┤                         │
│   │ PK: id           │                    │ PK: id           │                         │
│   │ report_date (UK) │                    │ report_year      │                         │
│   │ total_revenue    │                    │ report_month     │                         │
│   │ room_revenue     │                    │ total_revenue    │                         │
│   │ consume_revenue  │                    │ room_revenue     │                         │
│   │ deposit_revenue  │                    │ consume_revenue  │                         │
│   │ refund_amount    │                    │ deposit_revenue  │                         │
│   │ check_in_count   │                    │ refund_amount    │                         │
│   │ check_out_count  │                    │ check_in_count   │                         │
│   │ reservation_count│                    │ check_out_count  │                         │
│   │ new_customer_count                    │ reservation_count│                         │
│   │ occupancy_rate   │                    │ new_customer_count                         │
│   │ create_time      │                    │ avg_occupancy_rate                         │
│   └──────────────────┘                    │ avg_daily_revenue│                         │
│                                           │ create_time      │                         │
│                                           └──────────────────┘                         │
└─────────────────────────────────────────────────────────────────────────────────────────┘
```

## 2. 表关系说明

### 2.1 系统管理模块

| 关系 | 说明 |
|------|------|
| sys_user ↔ sys_role | 多对多关系，通过 sys_user_role 关联 |
| sys_role ↔ sys_menu | 多对多关系，通过 sys_role_menu 关联 |
| sys_menu 自关联 | parent_id 指向父菜单，实现树形结构 |
| sys_log → sys_user | 多对一关系，记录用户操作日志 |

### 2.2 业务核心模块

| 关系 | 说明 |
|------|------|
| room_type → room | 一对多，一个房型对应多个房间 |
| room → check_in | 一对多，一个房间可有多次入住记录 |
| customer → reservation | 一对多，一个客户可有多个预订 |
| customer → check_in | 一对多，一个客户可有多次入住 |
| customer → bill | 一对多，一个客户可有多个账单 |
| check_in → check_in_guest | 一对多，一次入住可有多个住客 |
| check_in → bill | 一对多，一次入住可产生多个账单 |
| reservation → check_in | 一对一，预订转入住 |

## 3. 数据库表清单

| 序号 | 表名 | 中文名 | 说明 |
|------|------|--------|------|
| 1 | sys_user | 用户表 | 系统用户信息 |
| 2 | sys_role | 角色表 | 角色定义 |
| 3 | sys_menu | 菜单表 | 菜单权限 |
| 4 | sys_user_role | 用户角色关联表 | 用户-角色多对多 |
| 5 | sys_role_menu | 角色菜单关联表 | 角色-菜单多对多 |
| 6 | sys_log | 操作日志表 | 系统操作记录 |
| 7 | room_type | 房型表 | 房间类型定义 |
| 8 | room | 房间表 | 房间信息 |
| 9 | customer | 客户表 | 客户信息 |
| 10 | reservation | 预订表 | 预订记录 |
| 11 | check_in | 入住记录表 | 入住信息 |
| 12 | check_in_guest | 入住人员表 | 同住人信息 |
| 13 | bill | 账单表 | 财务账单 |
| 14 | daily_report | 日报表 | 每日统计 |
| 15 | monthly_report | 月报表 | 月度统计 |

## 4. 核心字段索引设计

```sql
-- 用户表索引
UNIQUE KEY uk_username (username)

-- 房间表索引
UNIQUE KEY uk_room_number (room_number)
KEY idx_room_type (room_type_id)
KEY idx_status (status)

-- 客户表索引
KEY idx_phone (phone)
KEY idx_id_card (id_card)

-- 预订表索引
UNIQUE KEY uk_reservation_no (reservation_no)
KEY idx_customer (customer_id)
KEY idx_check_in_date (check_in_date)
KEY idx_status (status)

-- 入住表索引
UNIQUE KEY uk_check_in_no (check_in_no)
KEY idx_customer (customer_id)
KEY idx_room (room_id)
KEY idx_check_in_time (check_in_time)
KEY idx_status (status)

-- 账单表索引
UNIQUE KEY uk_bill_no (bill_no)
KEY idx_check_in (check_in_id)
KEY idx_customer (customer_id)
KEY idx_create_time (create_time)
```

## 5. 状态码定义

### 5.1 房间状态 (room.status)
| 值 | 含义 |
|----|------|
| 0 | 空闲 |
| 1 | 已预订 |
| 2 | 入住中 |
| 3 | 清洁中 |
| 4 | 维修中 |

### 5.2 预订状态 (reservation.status)
| 值 | 含义 |
|----|------|
| 0 | 待确认 |
| 1 | 已确认 |
| 2 | 已入住 |
| 3 | 已完成 |
| 4 | 已取消 |

### 5.3 入住状态 (check_in.status)
| 值 | 含义 |
|----|------|
| 0 | 入住中 |
| 1 | 已退房 |
| 2 | 续住中 |

### 5.4 账单类型 (bill.bill_type)
| 值 | 含义 |
|----|------|
| 0 | 房费 |
| 1 | 消费 |
| 2 | 押金 |
| 3 | 退款 |

### 5.5 支付状态 (bill.payment_status)
| 值 | 含义 |
|----|------|
| 0 | 未支付 |
| 1 | 已支付 |
| 2 | 已退款 |
