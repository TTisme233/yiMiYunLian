# 登录模块（JWT + Java + MySQL）

这是一个基于Spring Boot的登录模块，使用JWT进行身份验证，MySQL作为数据库存储用户信息。

## 技术栈

- Java 8
- Spring Boot 2.7.5
- Spring Security
- JWT (JSON Web Token)
- Spring Data JPA
- MySQL
- BCrypt (密码加密)

## 功能特性

- 用户注册
- 用户登录
- JWT令牌生成和验证
- 受保护的API端点
- 密码加密存储

## 项目结构

```
src/main/java/com/yimiyunlian/
├── LoginApplication.java          # 应用程序入口
├── controller/
│   ├── AuthController.java        # 认证相关控制器
│   └── ProtectedController.java   # 受保护资源控制器
├── entity/
│   └── User.java                  # 用户实体类
├── repository/
│   └── UserRepository.java        # 用户数据访问接口
├── security/
│   ├── JwtAuthenticationFilter.java  # JWT认证过滤器
│   └── WebSecurityConfig.java     # Spring Security配置
└── util/
    ├── JwtUtils.java              # JWT工具类
    └── PasswordUtils.java         # 密码工具类
```

## 配置说明

### 数据库配置

在`application.properties`文件中配置MySQL数据库连接：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/login_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### JWT配置

```properties
jwt.secret=your-secret-key-change-this-in-production
jwt.expiration=3600000  # 令牌过期时间（毫秒），默认为1小时
```

## API接口

### 认证接口

#### 用户注册

```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123"
}
```

#### 用户登录

```
POST /api/auth/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

登录成功后，将返回JWT令牌和用户信息：

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "username": "testuser",
  "email": "test@example.com"
}
```

### 受保护的接口

使用获取的JWT令牌访问受保护的接口：

```
GET /api/protected/profile
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

```
GET /api/protected/data
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

## 使用方法

1. 确保已安装MySQL数据库，并创建名为`login_db`的数据库
2. 修改`application.properties`中的数据库连接信息和JWT密钥
3. 使用Maven构建项目：`mvn clean install`
4. 运行应用程序：`java -jar target/login-module-1.0-SNAPSHOT.jar`

## 安全注意事项

- 在生产环境中，务必修改JWT密钥
- 考虑增加令牌刷新机制
- 可以添加更多的安全措施，如防暴力破解、验证码等
- 考虑实现角色和权限控制