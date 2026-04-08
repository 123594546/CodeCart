SET NAMES utf8mb4;

USE code_cart;

START TRANSACTION;

INSERT INTO sys_user (
  username, password, nickname, phone, email, avatar, role_code, status,
  last_login_time, created_at, updated_at, deleted, remark
)
VALUES
  (
    'admin',
    SHA2('Admin@123', 256),
    '系统管理员',
    '13800000000',
    'admin@codecart.local',
    '/images/avatar/admin.png',
    'ADMIN',
    'ENABLED',
    '2026-04-08 10:00:00',
    '2026-04-08 10:00:00',
    '2026-04-08 10:00:00',
    0,
    '默认管理员账号'
  ),
  (
    'alice',
    SHA2('User@123', 256),
    'Alice',
    '13800000001',
    'alice@codecart.local',
    '/images/avatar/alice.png',
    'USER',
    'ENABLED',
    '2026-04-08 10:05:00',
    '2026-04-08 10:05:00',
    '2026-04-08 10:05:00',
    0,
    '测试普通用户1'
  ),
  (
    'bob',
    SHA2('User@123', 256),
    'Bob',
    '13800000002',
    'bob@codecart.local',
    '/images/avatar/bob.png',
    'USER',
    'ENABLED',
    '2026-04-08 10:06:00',
    '2026-04-08 10:06:00',
    '2026-04-08 10:06:00',
    0,
    '测试普通用户2'
  ),
  (
    'charlie',
    SHA2('User@123', 256),
    'Charlie',
    '13800000003',
    'charlie@codecart.local',
    '/images/avatar/charlie.png',
    'USER',
    'ENABLED',
    '2026-04-08 10:07:00',
    '2026-04-08 10:07:00',
    '2026-04-08 10:07:00',
    0,
    '测试普通用户3'
  )
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  nickname = VALUES(nickname),
  avatar = VALUES(avatar),
  role_code = VALUES(role_code),
  status = VALUES(status),
  last_login_time = VALUES(last_login_time),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

INSERT INTO product_category (
  category_name, sort_no, status, created_at, updated_at, deleted, remark
)
VALUES
  ('游戏点卡', 10, 'ENABLED', '2026-04-08 10:10:00', '2026-04-08 10:10:00', 0, '游戏充值类商品'),
  ('视频会员', 20, 'ENABLED', '2026-04-08 10:10:00', '2026-04-08 10:10:00', 0, '视频平台会员商品'),
  ('音乐会员', 30, 'ENABLED', '2026-04-08 10:10:00', '2026-04-08 10:10:00', 0, '音乐平台会员商品'),
  ('软件授权', 40, 'ENABLED', '2026-04-08 10:10:00', '2026-04-08 10:10:00', 0, '软件激活码商品'),
  ('学习服务', 50, 'ENABLED', '2026-04-08 10:10:00', '2026-04-08 10:10:00', 0, '学习资料与题库服务')
ON DUPLICATE KEY UPDATE
  sort_no = VALUES(sort_no),
  status = VALUES(status),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

SET @admin_id = (SELECT id FROM sys_user WHERE username = 'admin' LIMIT 1);
SET @alice_id = (SELECT id FROM sys_user WHERE username = 'alice' LIMIT 1);
SET @bob_id = (SELECT id FROM sys_user WHERE username = 'bob' LIMIT 1);
SET @charlie_id = (SELECT id FROM sys_user WHERE username = 'charlie' LIMIT 1);

SET @cat_game_id = (SELECT id FROM product_category WHERE category_name = '游戏点卡' LIMIT 1);
SET @cat_video_id = (SELECT id FROM product_category WHERE category_name = '视频会员' LIMIT 1);
SET @cat_music_id = (SELECT id FROM product_category WHERE category_name = '音乐会员' LIMIT 1);
SET @cat_software_id = (SELECT id FROM product_category WHERE category_name = '软件授权' LIMIT 1);
SET @cat_study_id = (SELECT id FROM product_category WHERE category_name = '学习服务' LIMIT 1);

INSERT INTO product (
  category_id, product_name, product_cover, product_desc, price, original_price,
  total_stock, available_stock, sold_count, status, sort_no, created_at, updated_at, deleted, remark
)
SELECT
  @cat_game_id, 'Steam 100元充值卡', '/images/products/steam100.png',
  '适用于 Steam 钱包充值的虚拟点卡，支付成功后自动发码。', 100.00, 110.00,
  0, 0, 0, 'ON_SALE', 10, '2026-04-08 10:12:00', '2026-04-08 10:12:00', 0, '热门游戏点卡'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_name = 'Steam 100元充值卡' AND deleted = 0);

INSERT INTO product (
  category_id, product_name, product_cover, product_desc, price, original_price,
  total_stock, available_stock, sold_count, status, sort_no, created_at, updated_at, deleted, remark
)
SELECT
  @cat_video_id, 'B站大会员月卡', '/images/products/bili-month.png',
  '适用于 B 站大会员月卡开通，支持模拟支付与自动发码演示。', 25.00, 30.00,
  0, 0, 0, 'ON_SALE', 20, '2026-04-08 10:12:00', '2026-04-08 10:12:00', 0, '视频会员测试商品'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_name = 'B站大会员月卡' AND deleted = 0);

INSERT INTO product (
  category_id, product_name, product_cover, product_desc, price, original_price,
  total_stock, available_stock, sold_count, status, sort_no, created_at, updated_at, deleted, remark
)
SELECT
  @cat_music_id, 'QQ音乐绿钻月卡', '/images/products/qqmusic-month.png',
  '适用于 QQ 音乐绿钻月卡兑换，适合测试购物车与立即购买。', 15.00, 18.00,
  0, 0, 0, 'ON_SALE', 30, '2026-04-08 10:12:00', '2026-04-08 10:12:00', 0, '音乐会员测试商品'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_name = 'QQ音乐绿钻月卡' AND deleted = 0);

INSERT INTO product (
  category_id, product_name, product_cover, product_desc, price, original_price,
  total_stock, available_stock, sold_count, status, sort_no, created_at, updated_at, deleted, remark
)
SELECT
  @cat_software_id, 'Office 2021 专业版激活码', '/images/products/office2021-pro.png',
  '适用于 Office 2021 专业版激活的虚拟授权码。', 199.00, 299.00,
  0, 0, 0, 'ON_SALE', 40, '2026-04-08 10:12:00', '2026-04-08 10:12:00', 0, '软件授权测试商品'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_name = 'Office 2021 专业版激活码' AND deleted = 0);

INSERT INTO product (
  category_id, product_name, product_cover, product_desc, price, original_price,
  total_stock, available_stock, sold_count, status, sort_no, created_at, updated_at, deleted, remark
)
SELECT
  @cat_music_id, '网易云黑胶月卡', '/images/products/netease-blackvinyl.png',
  '适用于网易云黑胶月卡兑换，初始化为售罄商品。', 18.00, 20.00,
  0, 0, 0, 'ON_SALE', 50, '2026-04-08 10:12:00', '2026-04-08 10:12:00', 0, '售罄商品测试'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_name = '网易云黑胶月卡' AND deleted = 0);

INSERT INTO product (
  category_id, product_name, product_cover, product_desc, price, original_price,
  total_stock, available_stock, sold_count, status, sort_no, created_at, updated_at, deleted, remark
)
SELECT
  @cat_study_id, '课程题库VIP周卡', '/images/products/question-bank-week.png',
  '适用于课程题库体验的周卡商品，初始化为下架状态。', 9.90, 19.90,
  0, 0, 0, 'OFF_SALE', 60, '2026-04-08 10:12:00', '2026-04-08 10:12:00', 0, '下架商品测试'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM product WHERE product_name = '课程题库VIP周卡' AND deleted = 0);

UPDATE product
SET
  category_id = @cat_game_id,
  product_cover = '/images/products/steam100.png',
  product_desc = '适用于 Steam 钱包充值的虚拟点卡，支付成功后自动发码。',
  price = 100.00,
  original_price = 110.00,
  status = 'ON_SALE',
  sort_no = 10,
  updated_at = '2026-04-08 10:12:00',
  deleted = 0,
  remark = '热门游戏点卡'
WHERE product_name = 'Steam 100元充值卡';

UPDATE product
SET
  category_id = @cat_video_id,
  product_cover = '/images/products/bili-month.png',
  product_desc = '适用于 B 站大会员月卡开通，支持模拟支付与自动发码演示。',
  price = 25.00,
  original_price = 30.00,
  status = 'ON_SALE',
  sort_no = 20,
  updated_at = '2026-04-08 10:12:00',
  deleted = 0,
  remark = '视频会员测试商品'
WHERE product_name = 'B站大会员月卡';

UPDATE product
SET
  category_id = @cat_music_id,
  product_cover = '/images/products/qqmusic-month.png',
  product_desc = '适用于 QQ 音乐绿钻月卡兑换，适合测试购物车与立即购买。',
  price = 15.00,
  original_price = 18.00,
  status = 'ON_SALE',
  sort_no = 30,
  updated_at = '2026-04-08 10:12:00',
  deleted = 0,
  remark = '音乐会员测试商品'
WHERE product_name = 'QQ音乐绿钻月卡';

UPDATE product
SET
  category_id = @cat_software_id,
  product_cover = '/images/products/office2021-pro.png',
  product_desc = '适用于 Office 2021 专业版激活的虚拟授权码。',
  price = 199.00,
  original_price = 299.00,
  status = 'ON_SALE',
  sort_no = 40,
  updated_at = '2026-04-08 10:12:00',
  deleted = 0,
  remark = '软件授权测试商品'
WHERE product_name = 'Office 2021 专业版激活码';

UPDATE product
SET
  category_id = @cat_music_id,
  product_cover = '/images/products/netease-blackvinyl.png',
  product_desc = '适用于网易云黑胶月卡兑换，初始化为售罄商品。',
  price = 18.00,
  original_price = 20.00,
  status = 'ON_SALE',
  sort_no = 50,
  updated_at = '2026-04-08 10:12:00',
  deleted = 0,
  remark = '售罄商品测试'
WHERE product_name = '网易云黑胶月卡';

UPDATE product
SET
  category_id = @cat_study_id,
  product_cover = '/images/products/question-bank-week.png',
  product_desc = '适用于课程题库体验的周卡商品，初始化为下架状态。',
  price = 9.90,
  original_price = 19.90,
  status = 'OFF_SALE',
  sort_no = 60,
  updated_at = '2026-04-08 10:12:00',
  deleted = 0,
  remark = '下架商品测试'
WHERE product_name = '课程题库VIP周卡';

SET @product_steam_id = (SELECT id FROM product WHERE product_name = 'Steam 100元充值卡' LIMIT 1);
SET @product_bili_id = (SELECT id FROM product WHERE product_name = 'B站大会员月卡' LIMIT 1);
SET @product_qqmusic_id = (SELECT id FROM product WHERE product_name = 'QQ音乐绿钻月卡' LIMIT 1);
SET @product_office_id = (SELECT id FROM product WHERE product_name = 'Office 2021 专业版激活码' LIMIT 1);
SET @product_netease_id = (SELECT id FROM product WHERE product_name = '网易云黑胶月卡' LIMIT 1);
SET @product_study_id = (SELECT id FROM product WHERE product_name = '课程题库VIP周卡' LIMIT 1);

INSERT INTO cart_item (
  user_id, product_id, quantity, checked_flag, created_at, updated_at, deleted, remark
)
VALUES
  (@alice_id, @product_qqmusic_id, 1, 1, '2026-04-08 10:15:00', '2026-04-08 10:15:00', 0, 'Alice 已勾选购物车'),
  (@alice_id, @product_office_id, 1, 0, '2026-04-08 10:16:00', '2026-04-08 10:16:00', 0, 'Alice 未勾选购物车'),
  (@charlie_id, @product_bili_id, 2, 1, '2026-04-08 10:17:00', '2026-04-08 10:17:00', 0, 'Charlie 待结算购物车')
ON DUPLICATE KEY UPDATE
  quantity = VALUES(quantity),
  checked_flag = VALUES(checked_flag),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

INSERT INTO biz_order (
  order_no, user_id, total_amount, pay_amount, order_status, pay_status, source_type,
  pay_time, complete_time, cancel_time, created_at, updated_at, deleted, remark
)
VALUES
  (
    'ORD202604080001', @alice_id, 100.00, 100.00, 'COMPLETED', 'PAID', 'DIRECT',
    '2026-04-08 10:20:00', '2026-04-08 10:20:05', NULL, '2026-04-08 10:19:30', '2026-04-08 10:20:05', 0,
    'Alice 立即购买成功订单'
  ),
  (
    'ORD202604080002', @bob_id, 36.00, 36.00, 'COMPLETED', 'PAID', 'CART',
    '2026-04-08 10:25:00', '2026-04-08 10:25:05', NULL, '2026-04-08 10:24:20', '2026-04-08 10:25:05', 0,
    'Bob 购物车结算成功订单'
  ),
  (
    'ORD202604080003', @charlie_id, 50.00, 0.00, 'PENDING_PAYMENT', 'UNPAID', 'CART',
    NULL, NULL, NULL, '2026-04-08 10:30:00', '2026-04-08 10:30:00', 0,
    'Charlie 待支付订单'
  ),
  (
    'ORD202604080004', @alice_id, 199.00, 0.00, 'PAY_FAILED', 'FAILED', 'DIRECT',
    NULL, NULL, NULL, '2026-04-08 10:35:00', '2026-04-08 10:35:30', 0,
    'Alice 模拟支付失败订单'
  )
ON DUPLICATE KEY UPDATE
  user_id = VALUES(user_id),
  total_amount = VALUES(total_amount),
  pay_amount = VALUES(pay_amount),
  order_status = VALUES(order_status),
  pay_status = VALUES(pay_status),
  source_type = VALUES(source_type),
  pay_time = VALUES(pay_time),
  complete_time = VALUES(complete_time),
  cancel_time = VALUES(cancel_time),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

SET @order_1_id = (SELECT id FROM biz_order WHERE order_no = 'ORD202604080001' LIMIT 1);
SET @order_2_id = (SELECT id FROM biz_order WHERE order_no = 'ORD202604080002' LIMIT 1);
SET @order_3_id = (SELECT id FROM biz_order WHERE order_no = 'ORD202604080003' LIMIT 1);
SET @order_4_id = (SELECT id FROM biz_order WHERE order_no = 'ORD202604080004' LIMIT 1);

INSERT INTO biz_order_item (
  order_id, order_no, product_id, product_name_snapshot, product_cover_snapshot, price_snapshot,
  quantity, subtotal_amount, created_at, updated_at, deleted, remark
)
VALUES
  (
    @order_1_id, 'ORD202604080001', @product_steam_id, 'Steam 100元充值卡',
    '/images/products/steam100.png', 100.00, 1, 100.00, '2026-04-08 10:19:30', '2026-04-08 10:19:30', 0,
    '立即购买订单明细'
  ),
  (
    @order_2_id, 'ORD202604080002', @product_netease_id, '网易云黑胶月卡',
    '/images/products/netease-blackvinyl.png', 18.00, 2, 36.00, '2026-04-08 10:24:20', '2026-04-08 10:24:20', 0,
    '购物车结算订单明细'
  ),
  (
    @order_3_id, 'ORD202604080003', @product_bili_id, 'B站大会员月卡',
    '/images/products/bili-month.png', 25.00, 2, 50.00, '2026-04-08 10:30:00', '2026-04-08 10:30:00', 0,
    '待支付订单明细'
  ),
  (
    @order_4_id, 'ORD202604080004', @product_office_id, 'Office 2021 专业版激活码',
    '/images/products/office2021-pro.png', 199.00, 1, 199.00, '2026-04-08 10:35:00', '2026-04-08 10:35:00', 0,
    '支付失败订单明细'
  )
ON DUPLICATE KEY UPDATE
  order_no = VALUES(order_no),
  product_name_snapshot = VALUES(product_name_snapshot),
  product_cover_snapshot = VALUES(product_cover_snapshot),
  price_snapshot = VALUES(price_snapshot),
  quantity = VALUES(quantity),
  subtotal_amount = VALUES(subtotal_amount),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

SET @order_1_item_id = (
  SELECT id FROM biz_order_item WHERE order_id = @order_1_id AND product_id = @product_steam_id LIMIT 1
);
SET @order_2_item_id = (
  SELECT id FROM biz_order_item WHERE order_id = @order_2_id AND product_id = @product_netease_id LIMIT 1
);
SET @order_3_item_id = (
  SELECT id FROM biz_order_item WHERE order_id = @order_3_id AND product_id = @product_bili_id LIMIT 1
);
SET @order_4_item_id = (
  SELECT id FROM biz_order_item WHERE order_id = @order_4_id AND product_id = @product_office_id LIMIT 1
);

INSERT INTO pay_record (
  payment_no, order_id, order_no, user_id, pay_amount, pay_method, pay_status,
  third_party_no, paid_at, created_at, updated_at, deleted, remark
)
VALUES
  (
    'PAY202604080001', @order_1_id, 'ORD202604080001', @alice_id, 100.00, 'MOCK', 'SUCCESS',
    'MOCKTXN202604080001', '2026-04-08 10:20:00', '2026-04-08 10:19:40', '2026-04-08 10:20:00', 0,
    '模拟支付成功'
  ),
  (
    'PAY202604080002', @order_2_id, 'ORD202604080002', @bob_id, 36.00, 'MOCK', 'SUCCESS',
    'MOCKTXN202604080002', '2026-04-08 10:25:00', '2026-04-08 10:24:30', '2026-04-08 10:25:00', 0,
    '模拟支付成功'
  ),
  (
    'PAY202604080003', @order_3_id, 'ORD202604080003', @charlie_id, 50.00, 'MOCK', 'WAIT_PAY',
    NULL, NULL, '2026-04-08 10:30:00', '2026-04-08 10:30:00', 0,
    '待支付记录'
  ),
  (
    'PAY202604080004', @order_4_id, 'ORD202604080004', @alice_id, 199.00, 'MOCK', 'FAILED',
    'MOCKFAIL202604080004', NULL, '2026-04-08 10:35:00', '2026-04-08 10:35:30', 0,
    '模拟支付失败'
  )
ON DUPLICATE KEY UPDATE
  order_id = VALUES(order_id),
  order_no = VALUES(order_no),
  user_id = VALUES(user_id),
  pay_amount = VALUES(pay_amount),
  pay_method = VALUES(pay_method),
  pay_status = VALUES(pay_status),
  third_party_no = VALUES(third_party_no),
  paid_at = VALUES(paid_at),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

INSERT INTO redeem_code_batch (
  batch_no, product_id, import_total, success_total, fail_total, batch_status,
  imported_by, imported_at, created_at, updated_at, deleted, remark
)
VALUES
  ('BATCH202604080001', @product_steam_id, 6, 6, 0, 'COMPLETED', @admin_id, '2026-04-08 10:13:00', '2026-04-08 10:13:00', '2026-04-08 10:13:00', 0, 'Steam 点卡导入批次'),
  ('BATCH202604080002', @product_bili_id, 5, 5, 0, 'COMPLETED', @admin_id, '2026-04-08 10:13:10', '2026-04-08 10:13:10', '2026-04-08 10:13:10', 0, 'B站会员导入批次'),
  ('BATCH202604080003', @product_qqmusic_id, 5, 5, 0, 'COMPLETED', @admin_id, '2026-04-08 10:13:20', '2026-04-08 10:13:20', '2026-04-08 10:13:20', 0, 'QQ音乐导入批次'),
  ('BATCH202604080004', @product_office_id, 4, 4, 0, 'COMPLETED', @admin_id, '2026-04-08 10:13:30', '2026-04-08 10:13:30', '2026-04-08 10:13:30', 0, 'Office 激活码导入批次'),
  ('BATCH202604080005', @product_netease_id, 2, 2, 0, 'COMPLETED', @admin_id, '2026-04-08 10:13:40', '2026-04-08 10:13:40', '2026-04-08 10:13:40', 0, '网易云黑胶导入批次'),
  ('BATCH202604080006', @product_study_id, 5, 5, 0, 'COMPLETED', @admin_id, '2026-04-08 10:13:50', '2026-04-08 10:13:50', '2026-04-08 10:13:50', 0, '题库周卡导入批次')
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  import_total = VALUES(import_total),
  success_total = VALUES(success_total),
  fail_total = VALUES(fail_total),
  batch_status = VALUES(batch_status),
  imported_by = VALUES(imported_by),
  imported_at = VALUES(imported_at),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

SET @batch_steam_id = (SELECT id FROM redeem_code_batch WHERE batch_no = 'BATCH202604080001' LIMIT 1);
SET @batch_bili_id = (SELECT id FROM redeem_code_batch WHERE batch_no = 'BATCH202604080002' LIMIT 1);
SET @batch_qqmusic_id = (SELECT id FROM redeem_code_batch WHERE batch_no = 'BATCH202604080003' LIMIT 1);
SET @batch_office_id = (SELECT id FROM redeem_code_batch WHERE batch_no = 'BATCH202604080004' LIMIT 1);
SET @batch_netease_id = (SELECT id FROM redeem_code_batch WHERE batch_no = 'BATCH202604080005' LIMIT 1);
SET @batch_study_id = (SELECT id FROM redeem_code_batch WHERE batch_no = 'BATCH202604080006' LIMIT 1);

INSERT INTO redeem_code (
  product_id, batch_id, code_value, code_status, bind_order_id, bind_order_no,
  bind_user_id, locked_time, issued_time, created_at, updated_at, deleted, remark
)
VALUES
  (@product_steam_id, @batch_steam_id, 'STEAM100-202604-001', 'ISSUED', @order_1_id, 'ORD202604080001', @alice_id, '2026-04-08 10:20:01', '2026-04-08 10:20:03', '2026-04-08 10:13:00', '2026-04-08 10:20:03', 0, '已发放给 Alice'),
  (@product_steam_id, @batch_steam_id, 'STEAM100-202604-002', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:00', '2026-04-08 10:13:00', 0, '待发放'),
  (@product_steam_id, @batch_steam_id, 'STEAM100-202604-003', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:00', '2026-04-08 10:13:00', 0, '待发放'),
  (@product_steam_id, @batch_steam_id, 'STEAM100-202604-004', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:00', '2026-04-08 10:13:00', 0, '待发放'),
  (@product_steam_id, @batch_steam_id, 'STEAM100-202604-005', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:00', '2026-04-08 10:13:00', 0, '待发放'),
  (@product_steam_id, @batch_steam_id, 'STEAM100-202604-006', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:00', '2026-04-08 10:13:00', 0, '待发放'),
  (@product_bili_id, @batch_bili_id, 'BILI-MONTH-202604-001', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:10', '2026-04-08 10:13:10', 0, '待发放'),
  (@product_bili_id, @batch_bili_id, 'BILI-MONTH-202604-002', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:10', '2026-04-08 10:13:10', 0, '待发放'),
  (@product_bili_id, @batch_bili_id, 'BILI-MONTH-202604-003', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:10', '2026-04-08 10:13:10', 0, '待发放'),
  (@product_bili_id, @batch_bili_id, 'BILI-MONTH-202604-004', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:10', '2026-04-08 10:13:10', 0, '待发放'),
  (@product_bili_id, @batch_bili_id, 'BILI-MONTH-202604-005', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:10', '2026-04-08 10:13:10', 0, '待发放'),
  (@product_qqmusic_id, @batch_qqmusic_id, 'QQMUSIC-MONTH-202604-001', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:20', '2026-04-08 10:13:20', 0, '待发放'),
  (@product_qqmusic_id, @batch_qqmusic_id, 'QQMUSIC-MONTH-202604-002', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:20', '2026-04-08 10:13:20', 0, '待发放'),
  (@product_qqmusic_id, @batch_qqmusic_id, 'QQMUSIC-MONTH-202604-003', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:20', '2026-04-08 10:13:20', 0, '待发放'),
  (@product_qqmusic_id, @batch_qqmusic_id, 'QQMUSIC-MONTH-202604-004', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:20', '2026-04-08 10:13:20', 0, '待发放'),
  (@product_qqmusic_id, @batch_qqmusic_id, 'QQMUSIC-MONTH-202604-005', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:20', '2026-04-08 10:13:20', 0, '待发放'),
  (@product_office_id, @batch_office_id, 'OFFICE2021-PRO-202604-001', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:30', '2026-04-08 10:13:30', 0, '待发放'),
  (@product_office_id, @batch_office_id, 'OFFICE2021-PRO-202604-002', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:30', '2026-04-08 10:13:30', 0, '待发放'),
  (@product_office_id, @batch_office_id, 'OFFICE2021-PRO-202604-003', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:30', '2026-04-08 10:13:30', 0, '待发放'),
  (@product_office_id, @batch_office_id, 'OFFICE2021-PRO-202604-004', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:30', '2026-04-08 10:13:30', 0, '待发放'),
  (@product_netease_id, @batch_netease_id, 'NETEASE-BLACK-202604-001', 'ISSUED', @order_2_id, 'ORD202604080002', @bob_id, '2026-04-08 10:25:01', '2026-04-08 10:25:03', '2026-04-08 10:13:40', '2026-04-08 10:25:03', 0, '已发放给 Bob'),
  (@product_netease_id, @batch_netease_id, 'NETEASE-BLACK-202604-002', 'ISSUED', @order_2_id, 'ORD202604080002', @bob_id, '2026-04-08 10:25:02', '2026-04-08 10:25:04', '2026-04-08 10:13:40', '2026-04-08 10:25:04', 0, '已发放给 Bob'),
  (@product_study_id, @batch_study_id, 'QUESTION-BANK-WEEK-202604-001', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:50', '2026-04-08 10:13:50', 0, '待发放'),
  (@product_study_id, @batch_study_id, 'QUESTION-BANK-WEEK-202604-002', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:50', '2026-04-08 10:13:50', 0, '待发放'),
  (@product_study_id, @batch_study_id, 'QUESTION-BANK-WEEK-202604-003', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:50', '2026-04-08 10:13:50', 0, '待发放'),
  (@product_study_id, @batch_study_id, 'QUESTION-BANK-WEEK-202604-004', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:50', '2026-04-08 10:13:50', 0, '待发放'),
  (@product_study_id, @batch_study_id, 'QUESTION-BANK-WEEK-202604-005', 'UNUSED', NULL, NULL, NULL, NULL, NULL, '2026-04-08 10:13:50', '2026-04-08 10:13:50', 0, '待发放')
ON DUPLICATE KEY UPDATE
  product_id = VALUES(product_id),
  batch_id = VALUES(batch_id),
  code_status = VALUES(code_status),
  bind_order_id = VALUES(bind_order_id),
  bind_order_no = VALUES(bind_order_no),
  bind_user_id = VALUES(bind_user_id),
  locked_time = VALUES(locked_time),
  issued_time = VALUES(issued_time),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

SET @steam_code_1_id = (
  SELECT id FROM redeem_code WHERE code_value = 'STEAM100-202604-001' LIMIT 1
);
SET @netease_code_1_id = (
  SELECT id FROM redeem_code WHERE code_value = 'NETEASE-BLACK-202604-001' LIMIT 1
);
SET @netease_code_2_id = (
  SELECT id FROM redeem_code WHERE code_value = 'NETEASE-BLACK-202604-002' LIMIT 1
);

INSERT INTO code_issue_record (
  order_id, order_no, order_item_id, user_id, product_id, redeem_code_id,
  issue_status, issue_time, error_message, created_at, updated_at, deleted, remark
)
VALUES
  (
    @order_1_id, 'ORD202604080001', @order_1_item_id, @alice_id, @product_steam_id, @steam_code_1_id,
    'SUCCESS', '2026-04-08 10:20:03', NULL, '2026-04-08 10:20:03', '2026-04-08 10:20:03', 0,
    '订单发码成功'
  ),
  (
    @order_2_id, 'ORD202604080002', @order_2_item_id, @bob_id, @product_netease_id, @netease_code_1_id,
    'SUCCESS', '2026-04-08 10:25:03', NULL, '2026-04-08 10:25:03', '2026-04-08 10:25:03', 0,
    '订单发码成功'
  ),
  (
    @order_2_id, 'ORD202604080002', @order_2_item_id, @bob_id, @product_netease_id, @netease_code_2_id,
    'SUCCESS', '2026-04-08 10:25:04', NULL, '2026-04-08 10:25:04', '2026-04-08 10:25:04', 0,
    '订单发码成功'
  )
ON DUPLICATE KEY UPDATE
  order_id = VALUES(order_id),
  order_no = VALUES(order_no),
  order_item_id = VALUES(order_item_id),
  user_id = VALUES(user_id),
  product_id = VALUES(product_id),
  issue_status = VALUES(issue_status),
  issue_time = VALUES(issue_time),
  error_message = VALUES(error_message),
  updated_at = VALUES(updated_at),
  deleted = 0,
  remark = VALUES(remark);

UPDATE product p
SET
  total_stock = (
    SELECT COUNT(1)
    FROM redeem_code rc
    WHERE rc.product_id = p.id
      AND rc.deleted = 0
      AND rc.code_status IN ('UNUSED', 'LOCKED', 'ISSUED')
  ),
  available_stock = (
    SELECT COUNT(1)
    FROM redeem_code rc
    WHERE rc.product_id = p.id
      AND rc.deleted = 0
      AND rc.code_status = 'UNUSED'
  ),
  sold_count = (
    SELECT COUNT(1)
    FROM redeem_code rc
    WHERE rc.product_id = p.id
      AND rc.deleted = 0
      AND rc.code_status = 'ISSUED'
  ),
  updated_at = '2026-04-08 10:40:00'
WHERE p.deleted = 0;

COMMIT;
