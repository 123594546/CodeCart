CREATE DATABASE IF NOT EXISTS code_cart
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE code_cart;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  username VARCHAR(50) NOT NULL COMMENT '登录用户名',
  password VARCHAR(255) NOT NULL COMMENT '加密后的密码哈希',
  nickname VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  role_code VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色编码：USER/ADMIN',
  status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '用户状态',
  last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_sys_user_username (username),
  UNIQUE KEY uk_sys_user_phone (phone),
  UNIQUE KEY uk_sys_user_email (email),
  KEY idx_sys_user_role_status (role_code, status),
  KEY idx_sys_user_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='系统用户表';

CREATE TABLE IF NOT EXISTS product_category (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号，越小越靠前',
  status VARCHAR(20) NOT NULL DEFAULT 'ENABLED' COMMENT '分类状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_product_category_name (category_name),
  KEY idx_product_category_status_sort (status, sort_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';

CREATE TABLE IF NOT EXISTS product (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  category_id BIGINT UNSIGNED NOT NULL COMMENT '分类ID',
  product_name VARCHAR(100) NOT NULL COMMENT '商品名称',
  product_cover VARCHAR(255) DEFAULT NULL COMMENT '商品封面',
  product_desc TEXT COMMENT '商品详情描述',
  price DECIMAL(10,2) NOT NULL COMMENT '售价',
  original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  total_stock INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '总库存，对应有效兑换码总量',
  available_stock INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '可用库存，对应可售兑换码数量',
  sold_count INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '已售数量',
  status VARCHAR(20) NOT NULL DEFAULT 'OFF_SALE' COMMENT '商品状态',
  sort_no INT NOT NULL DEFAULT 0 COMMENT '排序号',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  KEY idx_product_category_status_sort (category_id, status, sort_no, id),
  KEY idx_product_status_sort (status, sort_no, id),
  KEY idx_product_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品表';

CREATE TABLE IF NOT EXISTS cart_item (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '购买数量',
  checked_flag TINYINT NOT NULL DEFAULT 1 COMMENT '是否勾选结算：0否1是',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_cart_item_user_product (user_id, product_id),
  KEY idx_cart_item_user_checked (user_id, checked_flag, updated_at),
  KEY idx_cart_item_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='购物车项表';

CREATE TABLE IF NOT EXISTS biz_order (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '下单用户ID',
  total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '订单总金额',
  pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '实付金额',
  order_status VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT' COMMENT '订单状态',
  pay_status VARCHAR(20) NOT NULL DEFAULT 'UNPAID' COMMENT '支付状态',
  source_type VARCHAR(20) NOT NULL DEFAULT 'DIRECT' COMMENT '下单来源：DIRECT/CART',
  pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  cancel_time DATETIME DEFAULT NULL COMMENT '取消时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_biz_order_order_no (order_no),
  KEY idx_biz_order_user_created (user_id, created_at),
  KEY idx_biz_order_user_status_created (user_id, order_status, created_at),
  KEY idx_biz_order_status_created (order_status, created_at),
  KEY idx_biz_order_pay_status_created (pay_status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';

CREATE TABLE IF NOT EXISTS biz_order_item (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  product_name_snapshot VARCHAR(100) NOT NULL COMMENT '商品名称快照',
  product_cover_snapshot VARCHAR(255) DEFAULT NULL COMMENT '商品封面快照',
  price_snapshot DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品单价快照',
  quantity INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '购买数量',
  subtotal_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '小计金额',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_biz_order_item_order_product (order_id, product_id),
  KEY idx_biz_order_item_order_no (order_no),
  KEY idx_biz_order_item_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';

CREATE TABLE IF NOT EXISTS pay_record (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  payment_no VARCHAR(32) NOT NULL COMMENT '支付流水号',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  pay_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '支付金额',
  pay_method VARCHAR(20) NOT NULL DEFAULT 'MOCK' COMMENT '支付方式',
  pay_status VARCHAR(20) NOT NULL DEFAULT 'WAIT_PAY' COMMENT '支付状态',
  third_party_no VARCHAR(64) DEFAULT NULL COMMENT '第三方流水号，模拟支付可为空',
  paid_at DATETIME DEFAULT NULL COMMENT '实际支付完成时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_pay_record_payment_no (payment_no),
  UNIQUE KEY uk_pay_record_order_id (order_id),
  KEY idx_pay_record_order_no (order_no),
  KEY idx_pay_record_user_created (user_id, created_at),
  KEY idx_pay_record_status_created (pay_status, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录表';

CREATE TABLE IF NOT EXISTS redeem_code_batch (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  batch_no VARCHAR(32) NOT NULL COMMENT '导入批次号',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '对应商品ID',
  import_total INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '导入总数',
  success_total INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '成功导入数',
  fail_total INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '失败导入数',
  batch_status VARCHAR(20) NOT NULL DEFAULT 'PROCESSING' COMMENT '批次状态',
  imported_by BIGINT UNSIGNED DEFAULT NULL COMMENT '导入人ID',
  imported_at DATETIME DEFAULT NULL COMMENT '导入完成时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_redeem_code_batch_batch_no (batch_no),
  KEY idx_redeem_code_batch_product_id (product_id),
  KEY idx_redeem_code_batch_imported_by_created (imported_by, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兑换码导入批次表';

CREATE TABLE IF NOT EXISTS redeem_code (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '所属商品ID',
  batch_id BIGINT UNSIGNED NOT NULL COMMENT '所属导入批次ID',
  code_value VARCHAR(512) COLLATE utf8mb4_bin NOT NULL COMMENT '兑换码原文，区分大小写',
  code_status VARCHAR(20) NOT NULL DEFAULT 'UNUSED' COMMENT '兑换码状态',
  bind_order_id BIGINT UNSIGNED DEFAULT NULL COMMENT '绑定订单ID',
  bind_order_no VARCHAR(32) DEFAULT NULL COMMENT '绑定订单号',
  bind_user_id BIGINT UNSIGNED DEFAULT NULL COMMENT '绑定用户ID',
  locked_time DATETIME DEFAULT NULL COMMENT '锁定时间',
  issued_time DATETIME DEFAULT NULL COMMENT '发放完成时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_redeem_code_code_value (code_value),
  KEY idx_redeem_code_product_status_id (product_id, code_status, id),
  KEY idx_redeem_code_batch_id (batch_id),
  KEY idx_redeem_code_bind_order_id (bind_order_id),
  KEY idx_redeem_code_bind_user_issued (bind_user_id, issued_time),
  KEY idx_redeem_code_status_locked_time (code_status, locked_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='兑换码表';

CREATE TABLE IF NOT EXISTS code_issue_record (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  order_id BIGINT UNSIGNED NOT NULL COMMENT '订单ID',
  order_no VARCHAR(32) NOT NULL COMMENT '订单编号',
  order_item_id BIGINT UNSIGNED DEFAULT NULL COMMENT '订单明细ID，便于定位具体商品行',
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  product_id BIGINT UNSIGNED NOT NULL COMMENT '商品ID',
  redeem_code_id BIGINT UNSIGNED DEFAULT NULL COMMENT '兑换码ID，失败时可为空',
  issue_status VARCHAR(20) NOT NULL DEFAULT 'SUCCESS' COMMENT '发码状态',
  issue_time DATETIME DEFAULT NULL COMMENT '发码时间',
  error_message VARCHAR(500) DEFAULT NULL COMMENT '发码失败原因',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  deleted TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除标记：0否1是',
  remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id),
  UNIQUE KEY uk_code_issue_record_redeem_code_id (redeem_code_id),
  KEY idx_code_issue_record_order_id (order_id),
  KEY idx_code_issue_record_order_no (order_no),
  KEY idx_code_issue_record_order_item_id (order_item_id),
  KEY idx_code_issue_record_user_issue_time (user_id, issue_time),
  KEY idx_code_issue_record_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发码记录表';
