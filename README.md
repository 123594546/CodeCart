# CodeCart

基于 `Spring Boot + Vue 3 + MySQL 8` 的虚拟商品兑换码商城课程项目。

项目围绕虚拟商品销售的核心闭环实现：

`商品展示 -> 加入购物车/立即购买 -> 提交订单 -> 模拟支付 -> 自动发码 -> 查看已购兑换码`

当前仓库已经包含：

- 后端接口工程
- 前端可视化页面
- MySQL 8 建表脚本
- 初始化测试数据脚本
- JWT 登录鉴权
- 管理员后台基础管理页

## 1. 项目定位

这是一个适合课程设计、课程答辩和后续继续扩展开发的轻量项目，重点解决以下问题：

- 普通用户如何浏览虚拟商品、下单、支付和查看兑换码
- 管理员如何维护分类、商品和兑换码库存
- 支付成功后如何自动发码
- 如何保证一个兑换码只发放一次
- 如何保证商品聚合库存与兑换码池状态一致

项目没有引入复杂 RBAC、物流、收货地址、优惠券等非核心模块，设计重点放在虚拟商品商城的主业务链。

## 2. 技术栈

### 后端

- Java 17
- Spring Boot 3.2.4
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JWT
- Maven

### 前端

- Vue 3
- Vite
- Vue Router
- Element Plus
- Axios

### 数据库

- MySQL 8.0
- InnoDB
- utf8mb4

## 3. 已实现功能

### 用户端

- 用户注册
- 用户登录
- JWT 鉴权
- 分类列表
- 商品列表
- 商品详情
- 加入购物车
- 修改购物车项数量与勾选状态
- 删除购物车项
- 立即购买
- 购物车结算
- 模拟支付
- 自动发码
- 订单列表
- 订单详情
- 已购兑换码列表

### 管理端

- 管理员登录
- 分类管理
- 商品管理
- 商品上下架
- 兑换码批次导入
- 导入批次列表
- 导入成功后自动回写商品库存

### 已实现的核心业务规则

- 未支付订单不会发码
- 支付成功后才会触发自动发码
- 一个兑换码只能发放一次
- 商品库存不足时无法下单或支付
- 订单、支付、兑换码、发码记录可以完整追踪
- 普通用户和管理员通过 `role_code` 隔离权限
- 商品库存通过聚合字段和兑换码池双重约束维护

## 4. 当前页面

前端页面位于 `frontend/src/views`，目前已经有可运行界面：

- `/` 首页商品展示
- `/auth` 登录 / 注册
- `/products/:id` 商品详情
- `/cart` 购物车
- `/orders` 我的订单
- `/codes` 我的兑换码
- `/admin` 管理员工作台

## 5. 项目结构

```text
CodeCart
├─ src/main/java/com/codecart
│  ├─ common          通用返回、异常、上下文、工具类
│  ├─ config          MyBatis-Plus、JWT 拦截器、自动填充配置
│  ├─ controller      前台与后台接口
│  ├─ dto             请求参数对象
│  ├─ entity          MyBatis-Plus 实体
│  ├─ mapper          数据访问层
│  ├─ service         业务接口与实现
│  └─ vo              响应对象
├─ src/main/resources
│  └─ application.yml 后端配置
├─ frontend
│  ├─ src/api         前端接口封装
│  ├─ src/components  公共组件
│  ├─ src/router      前端路由
│  ├─ src/stores      会话状态
│  └─ src/views       页面文件
├─ sql
│  ├─ code_cart_schema.sql    数据库建表脚本
│  └─ code_cart_seed_data.sql 初始化测试数据
└─ README.md
```

## 6. 数据库设计概览

核心表共 10 张：

- `sys_user`
- `product_category`
- `product`
- `cart_item`
- `biz_order`
- `biz_order_item`
- `pay_record`
- `redeem_code_batch`
- `redeem_code`
- `code_issue_record`

数据库脚本：

- 建表脚本：[sql/code_cart_schema.sql](./sql/code_cart_schema.sql)
- 测试数据脚本：[sql/code_cart_seed_data.sql](./sql/code_cart_seed_data.sql)

## 7. 初始化测试账号

初始化数据脚本会创建以下账号：

| 角色 | 用户名 | 密码 |
| --- | --- | --- |
| 管理员 | `admin` | `Admin@123` |
| 普通用户 | `alice` | `User@123` |
| 普通用户 | `bob` | `User@123` |
| 普通用户 | `charlie` | `User@123` |

说明：

- 种子数据脚本中的密码按 `SHA2(..., 256)` 写入
- 当前后端登录校验也使用 SHA-256 匹配
- 注册的新用户同样按 SHA-256 入库

## 8. 运行前准备

请先确保本机安装：

- JDK 17
- Maven
- Node.js 18+
- MySQL 8.0

## 9. 数据库初始化

### 9.1 创建表结构

执行：

```sql
SOURCE sql/code_cart_schema.sql;
```

或者使用命令行：

```powershell
mysql -uroot -pmysql < sql/code_cart_schema.sql
```

### 9.2 导入测试数据

```powershell
mysql -uroot -pmysql code_cart < sql/code_cart_seed_data.sql
```

## 10. 后端启动

当前默认数据库配置在 [application.yml](./src/main/resources/application.yml)：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/code_cart?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: mysql
```

如果你的本地数据库账号密码不同，请先修改配置。

启动后端：

```powershell
mvn spring-boot:run
```

默认端口：

```text
http://127.0.0.1:8080
```

## 11. 前端启动

安装依赖：

```powershell
cd frontend
npm install
```

启动开发环境：

```powershell
npm run dev
```

默认地址：

```text
http://127.0.0.1:5173
```

构建前端：

```powershell
npm run build
```

## 12. 一键联调顺序

推荐按这个顺序启动：

1. 启动 MySQL
2. 执行建表脚本
3. 执行测试数据脚本
4. 启动后端 `mvn spring-boot:run`
5. 启动前端 `cd frontend && npm run dev`
6. 浏览器访问 `http://127.0.0.1:5173`

## 13. 主要接口概览

### 认证接口

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

### 商城前台接口

- `GET /api/categories`
- `GET /api/products`
- `GET /api/products/{id}`
- `GET /api/cart`
- `POST /api/cart`
- `PATCH /api/cart`
- `DELETE /api/cart/{cartItemId}`
- `POST /api/orders/direct`
- `POST /api/orders/cart`
- `POST /api/orders/{orderNo}/pay`
- `GET /api/orders`
- `GET /api/orders/{orderNo}`
- `GET /api/orders/codes`

### 管理后台接口

- `GET /api/admin/categories`
- `POST /api/admin/categories`
- `PUT /api/admin/categories/{categoryId}`
- `PATCH /api/admin/categories/{categoryId}/status`
- `GET /api/admin/products`
- `POST /api/admin/products`
- `PUT /api/admin/products/{productId}`
- `PATCH /api/admin/products/{productId}/status`
- `GET /api/admin/code-batches`
- `POST /api/admin/code-batches/import`

## 14. 接口返回格式

项目接口统一返回：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

说明：

- `code = 200` 表示业务成功
- 业务异常时会返回非 200 的 `code`
- 当前项目主要通过响应体中的 `code` 判断业务结果

## 15. 核心业务说明

### 15.1 下单

- 支持立即购买
- 支持购物车结算
- 下单时校验商品状态和可售库存
- 订单明细冗余商品快照字段

### 15.2 支付与自动发码

模拟支付成功后，后端会在事务内完成：

1. 锁定订单
2. 校验支付状态
3. 查询订单明细
4. 从 `redeem_code` 中锁定可用兑换码
5. 将兑换码绑定订单和用户
6. 写入 `code_issue_record`
7. 扣减商品 `available_stock`
8. 更新支付记录和订单状态

### 15.3 后台兑换码导入

管理员在后台导入兑换码时，系统会：

1. 创建导入批次
2. 将文本内容按行拆分
3. 自动过滤空行
4. 统计同批重复码
5. 插入成功的兑换码到 `redeem_code`
6. 根据成功数量回写 `product.total_stock` 和 `product.available_stock`
7. 生成批次状态和成功失败统计

## 16. 构建与验证

当前项目已经验证通过的命令：

```powershell
mvn -DskipTests compile
mvn -DskipTests package
npm run build
```

说明：

- 后端当前没有完整单元测试类
- `mvn test` 可能会显示 `No tests to run`

## 17. 当前已知说明

### 17.1 Vite 路径警告

当前项目路径中包含 `#` 字符，例如：

```text
D:\#java_test\java_test\CodeCart
```

Vite 在启动或构建时会给出警告，但目前不影响本项目的本地运行和构建。

### 17.2 课程项目定位

本项目优先保证课程设计可落地，因此暂未实现：

- 复杂 RBAC 权限模型
- 第三方真实支付
- 后台订单监管页
- 后台用户管理页
- 操作日志、登录日志、统计快照
- 物流、收货地址、售后等非虚拟商品场景功能

## 18. 适合答辩时强调的点

- 采用前后端分离架构，前端 Vue 3，后端 Spring Boot
- 采用 JWT 做登录态校验，普通用户和管理员权限隔离
- 数据库围绕虚拟商品自动发码场景设计
- 通过 `redeem_code`、`redeem_code_batch`、`code_issue_record` 构建完整发码追踪链
- 支付成功后自动发码，并通过事务保证一致性
- 商品聚合库存和兑换码池状态联动维护
- 管理后台可以直接完成分类管理、商品管理和兑换码导入

## 19. 后续扩展建议

如果继续开发，优先建议按下面顺序扩展：

1. 后台订单监管页
2. 后台用户管理
3. 兑换码导入文件上传
4. 库存变更日志
5. 销售统计报表
6. 更安全的密码存储方案，例如 BCrypt

## 20. 演示建议

答辩时可以按这个流程演示：

1. 使用普通用户登录
2. 浏览商品并加入购物车
3. 提交订单并模拟支付
4. 展示支付成功后自动发码
5. 在“我的兑换码”中查看已购卡密
6. 切换管理员登录
7. 在后台新增分类、商品
8. 在后台导入一批新兑换码
9. 展示商品库存随导入结果自动增长

---

如果你接下来还要继续完善文档，建议下一步补：

- 系统架构图
- 数据库 E-R 图
- 接口时序图
- 论文或答辩用的模块说明
