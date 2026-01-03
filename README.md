# ZX-AI Backend (智学AI 后端系统)

## 📖 项目简介

ZX-AI Backend 是一个功能完善的在线教育与 AI 辅助学习平台的后端系统。项目采用 Maven 多模块架构，基于 Spring Boot 开发，集成了 AI 对话、课程点播、会员体系、支付系统（支付宝）、分销提现以及后台管理等核心功能。

## 🏗️ 架构与模块说明

本项目采用分层解耦的多模块架构：

| 模块名称 | 描述 |
| --- | --- |
| **zx-ai-admin** | **后台管理服务端**。提供给管理员使用，包含用户管理、课程管理、订单处理、数据统计等接口。 |
| **zx-ai-web** | **用户端（客户端）服务端**。提供给终端用户使用，包含 AI 聊天、课程学习、个人中心、钱包支付等接口。 |
| **zx-ai-auth** | **认证鉴权模块**。基于 Sa-Token 封装，负责统一的登录认证与权限控制。 |
| **zx-ai-pay** | **支付模块**。目前主要集成了支付宝（Alipay）支付能力。 |
| **zx-ai-common** | **公共模块**。包含全局配置、工具类、实体类 (PO/VO/DTO)、枚举、异常处理、AOP日志等通用代码。 |

## 🛠️ 技术栈

* **开发语言**: Java
* **核心框架**: Spring Boot
* **ORM 框架**: MyBatis Plus
* **权限认证**: Sa-Token
* **数据库**: MySQL
* **缓存**: Redis
* **消息队列**: RocketMQ (用于订单超时取消、异步处理等)
* **对象存储**: MinIO (用于文件/图片上传)
* **支付集成**: Alipay SDK
* **其他**: Lombok, Hutool, Jackson

## 🌟 核心功能

### 1. 客户端 (zx-ai-web)

* **🤖 AI 助手**:
* 支持 AI 流式对话 (`AIStreamController`)。
* AI 会话历史管理。


* **📚 课程中心**:
* 课程分类与搜索。
* 课程详情与章节目录查看。
* 学习记录追踪 (`LearningRecord`)。
* 课程评价与点赞。


* **💰 资产与交易**:
* **会员体系**: VIP 套餐购买与权益管理。
* **钱包系统**: 余额充值、消费、提现申请。
* **优惠券**: 优惠券领取与核销。
* **订单**: 课程订单、VIP 订单、秒杀功能 (Lua 脚本支持)。


* **👤 用户服务**:
* 用户注册/登录、密码修改。
* 客服消息系统。



### 2. 管理端 (zx-ai-admin)

* **📊 数据看板**: 每日用户、订单趋势、收入统计概览。
* **👥 用户管理**: 用户列表查询、封禁/解封、讲师管理。
* **📝 内容管理**:
* 课程发布与维护（章节、视频）。
* 课程分类管理。
* 营销活动配置。


* **💸 财务管理**:
* 提现审核与打款。
* VIP 订单与充值订单查询。


* **⚙️ 系统配置**:
* 轮播图、公告管理。
* 客服消息回复。
* 意见反馈处理。



## 🚀 快速开始

### 前置要求

* JDK 1.8+ (或更高版本)
* MySQL 5.7/8.0
* Redis
* RocketMQ
* MinIO Server

### 配置说明

请在以下路径修改配置文件以匹配你的本地环境：

* **Web端**: `zx-ai-web/src/main/resources/application-dev.yaml`
* **Admin端**: `zx-ai-admin/src/main/resources/application-dev.yaml`

需要配置的关键项包括：

1. **MySQL**: 数据库连接地址、账号密码。
2. **Redis**: Host、Port、密码。
3. **MinIO**: Endpoint、AccessKey、SecretKey。
4. **RocketMQ**: NameServer 地址。
5. **Alipay**: AppID、私钥、支付宝公钥。

### 运行步骤

1. **克隆项目**:
```bash
git clone <repository-url>

```


2. **构建项目**:
在根目录下运行 Maven 命令安装依赖：
```bash
mvn clean install

```


3. **启动服务**:
* 启动管理后台: 运行 `zx-ai-admin/src/main/java/com/zm/zx/admin/ZxAdminApplication.java`
* 启动客户端API: 运行 `zx-ai-web/src/main/java/com/zm/zx/web/ZxWebApplication.java`



## 📂 目录结构概览

```
zx-ai-back
├── zx-ai-admin      # 后台管理API
├── zx-ai-web        # 前台用户API
├── zx-ai-auth       # 认证鉴权
├── zx-ai-pay        # 支付组件
├── zx-ai-common     # 公共组件
└── pom.xml          # 父工程Maven配置
