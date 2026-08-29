# Digital Copyright Registration & Verification System

基于 SpringBoot 的数字版权登记与验证系统，通过 SHA-256 哈希算法生成作品数字指纹，模拟区块链存证机制，实现版权登记、验证、查询、存证证明等完整功能。

## 功能特点

- **用户管理**：用户注册、登录、JWT 身份认证
- **版权登记**：上传作品内容，系统自动生成 SHA-256 数字指纹，生成区块哈希模拟链上存证
- **版权验证**：上传作品内容比对哈希，秒级验证是否已登记
- **版权查询**：按登记号查询、按用户查询、关键词搜索
- **存证证明**：生成包含哈希、时间戳、区块信息的版权存证证明
- **接口文档**：集成 Knife4j（Swagger），自动生成 API 文档

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| SpringBoot | 3.2.5 | 后端框架 |
| MyBatis-Plus | 3.5.6 | ORM 框架 |
| MySQL | 8.0 | 关系型数据库 |
| JWT (jjwt) | 0.12.5 | 身份认证 |
| Knife4j | 4.5.0 | API 文档 |
| Lombok | - | 简化代码 |

## 项目结构

```
copyright-system/
├── src/main/java/com/syzua/copyright/
│   ├── CopyrightApplication.java          # 启动类
│   ├── config/
│   │   ├── WebMvcConfig.java              # 跨域+拦截器配置
│   │   ├── MyBatisPlusConfig.java          # MyBatis-Plus配置
│   │   └── GlobalExceptionHandler.java    # 全局异常处理
│   ├── controller/
│   │   ├── UserController.java            # 用户接口
│   │   └── CopyrightController.java       # 版权接口
│   ├── entity/
│   │   ├── User.java                      # 用户实体
│   │   └── CopyrightRecord.java           # 版权记录实体
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   └── CopyrightRecordMapper.java
│   ├── service/
│   │   ├── UserService.java               # 用户服务
│   │   └── CopyrightService.java           # 版权服务
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── RegisterRequest.java
│   │   ├── CopyrightRegisterRequest.java
│   │   └── Result.java                    # 统一响应
│   ├── interceptor/
│   │   └── JwtInterceptor.java            # JWT认证拦截器
│   └── utils/
│       ├── JwtUtils.java                   # JWT工具
│       └── HashUtils.java                  # SHA-256哈希工具
├── src/main/resources/
│   ├── application.yml                     # 配置文件
│   └── sql/
│       └── init.sql                        # 数据库初始化脚本
└── pom.xml
```

## 快速部署

### 1. 环境准备
- JDK 17+
- MySQL 8.0+
- Maven 3.8+

### 2. 初始化数据库
```bash
mysql -u root -p < src/main/resources/sql/init.sql
```

### 3. 修改配置
编辑 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/copyright_db
    username: your_username
    password: your_password
```

### 4. 启动项目
```bash
mvn spring-boot:run
```

### 5. 访问接口文档
```
http://localhost:8080/doc.html
```

## API 接口

| 接口 | 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|------|
| 用户注册 | POST | /api/v1/user/register | 注册新用户 | 否 |
| 用户登录 | POST | /api/v1/user/login | 登录获取Token | 否 |
| 获取用户信息 | GET | /api/v1/user/info | 获取当前登录用户 | 是 |
| 版权登记 | POST | /api/v1/copyright/register | 上传作品存证 | 是 |
| 版权验证 | POST | /api/v1/copyright/verify | 验证作品是否已登记 | 否 |
| 按登记号查询 | GET | /api/v1/copyright/query/{regNo} | 查询版权详情 | 否 |
| 我的版权 | GET | /api/v1/copyright/my-list | 分页查询用户版权 | 是 |
| 搜索版权 | GET | /api/v1/copyright/search | 按标题/作者搜索 | 否 |
| 存证证明 | GET | /api/v1/copyright/proof/{id} | 获取区块链存证证明 | 否 |

## 存证原理

```
作品内容 ──SHA-256──> 内容哈希(contentHash)
                              │
              ┌───────────────┼───────────────┐
              │               │               │
         作者姓名         时间戳           盐值Salt
              │               │               │
              └───────SHA-256─┴───────────────┘
                              │
                        区块哈希(blockHash)
                              │
                        存入数据库(模拟链上存储)
```

- **内容哈希**：作品原始内容经 SHA-256 生成，任何篡改导致哈希不匹配
- **区块哈希**：内容哈希 + 作者 + 时间戳 + 盐值再次 SHA-256，模拟区块链区块
- **验证流程**：用户上传内容 → 重新计算 SHA-256 → 与数据库记录比对 → 匹配则验证通过

## 使用示例

### 注册
```bash
curl -X POST http://localhost:8080/api/v1/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","email":"test@test.com"}'
```

### 登录
```bash
curl -X POST http://localhost:8080/api/v1/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'
```

### 版权登记
```bash
curl -X POST http://localhost:8080/api/v1/copyright/register \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"title":"我的文章","authorName":"张三","content":"这是原创内容...","workType":"TEXT"}'
```

### 版权验证
```bash
curl -X POST http://localhost:8080/api/v1/copyright/verify \
  -H "Content-Type: application/json" \
  -d '{"content":"这是原创内容..."}'
```
