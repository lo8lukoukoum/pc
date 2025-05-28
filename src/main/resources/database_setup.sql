-- 设置数据库字符集和排序规则以支持中文
CREATE DATABASE IF NOT EXISTS flower_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE flower_shop;

-- 用户信息表
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码', -- 实际应用中应存储哈希后的密码
    `role` VARCHAR(10) NOT NULL DEFAULT 'customer' COMMENT '角色 (customer/admin)',
    `registration_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '注册日期',
    `email` VARCHAR(100) UNIQUE COMMENT '电子邮箱',
    `phone_number` VARCHAR(20) COMMENT '电话号码',
    `personal_signature` TEXT COMMENT '个人签名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS `categories` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品信息表
CREATE TABLE IF NOT EXISTS `products` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `name` VARCHAR(200) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
    `category_id` INT COMMENT '分类ID',
    `image_url` VARCHAR(255) COMMENT '商品图片路径',
    `status` VARCHAR(20) DEFAULT '下架' COMMENT '商品状态 (e.g., 上架/新品/下架/热销推荐)',
    `creation_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '上架日期',
    `sales_count` INT DEFAULT 0 COMMENT '销量',
    `views` INT DEFAULT 0 COMMENT '浏览量',
    FOREIGN KEY (`category_id`) REFERENCES `categories`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品信息表';

-- 订单信息表
CREATE TABLE IF NOT EXISTS `orders` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `order_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '订单日期',
    `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '订单总金额',
    `status` VARCHAR(20) NOT NULL COMMENT '订单状态 (e.g., 待付款/处理中/待发货/已发货/已完成/已取消)',
    `shipping_address` TEXT NOT NULL COMMENT '收货地址',
    `contact_phone` VARCHAR(20) COMMENT '联系电话',
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单信息表';

-- 订单项表
CREATE TABLE IF NOT EXISTS `order_items` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `order_id` INT NOT NULL COMMENT '订单ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL COMMENT '数量',
    `price_at_purchase` DECIMAL(10, 2) NOT NULL COMMENT '购买时价格',
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单项表';

-- 购物车信息表
CREATE TABLE IF NOT EXISTS `cart` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `quantity` INT NOT NULL COMMENT '数量',
    `added_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '添加日期',
    UNIQUE KEY `user_product_unique` (`user_id`, `product_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车信息表';

-- 评价信息表
CREATE TABLE IF NOT EXISTS `reviews` (
    `id` INT PRIMARY KEY AUTO_INCREMENT,
    `user_id` INT NOT NULL COMMENT '用户ID',
    `product_id` INT NOT NULL COMMENT '商品ID',
    `order_id` INT NOT NULL COMMENT '关联订单ID',
    `rating` INT COMMENT '评分 (1-5)',
    `comment` TEXT COMMENT '评论内容',
    `image_url` VARCHAR(255) COMMENT '评价图片路径',
    `video_url` VARCHAR(255) COMMENT '评价视频路径',
    `review_date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '评价日期',
    `is_anonymous` BOOLEAN DEFAULT FALSE COMMENT '是否匿名',
    `status` VARCHAR(20) DEFAULT '待审核' COMMENT '审核状态 (e.g., 待审核/已批准/已拒绝)',
    CHECK (rating >= 1 AND rating <= 5),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`product_id`) REFERENCES `products`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价信息表';

-- 插入示例数据

-- 用户
INSERT INTO `users` (`username`, `password`, `role`, `email`, `phone_number`, `personal_signature`) VALUES
('admin', 'adminpass', 'admin', 'admin@example.com', '13800138000', '管理员大佬'),
('zhangsan', 'password123', 'customer', 'zhangsan@example.com', '13900139000', '喜欢鲜花'),
('lisi', 'securepass', 'customer', 'lisi@example.com', '13700137000', '花卉爱好者'),
('wangwu', 'testpass', 'customer', 'wangwu@example.com', '13600136000', '生活要有仪式感');

-- 分类
INSERT INTO `categories` (`name`) VALUES
('玫瑰'),
('康乃馨'),
('百合'),
('节日鲜花'),
('绿植盆栽');

-- 商品
INSERT INTO `products` (`name`, `description`, `price`, `stock`, `category_id`, `image_url`, `status`, `sales_count`, `views`) VALUES
('红玫瑰花束', '经典红玫瑰，浪漫的象征，适合各种爱情场合。精心挑选A级玫瑰，搭配精致包装。', 199.00, 99, 1, 'images/red_roses.jpg', '上架', 150, 500),
('粉色康乃馨', '温馨的粉色康乃馨，适合母亲节、教师节等感恩场合。', 88.00, 150, 2, 'images/pink_carnations.jpg', '上架', 120, 350),
('白色香水百合', '纯洁的白色百合，带有淡淡清香，适合祝福、探望等。', 128.00, 60, 3, 'images/white_lilies.jpg', '新品', 30, 200),
('七夕限定礼盒', '七夕情人节特别设计，包含多种花材，传达浓浓爱意。', 299.00, 30, 4, 'images/qixi_giftbox.jpg', '热销推荐', 80, 600),
('多肉植物组合', '可爱的多肉植物，易于养护，为生活增添一抹绿色。', 59.00, 200, 5, 'images/succulents_combo.jpg', '上架', 200, 450),
('向日葵花束', '充满阳光活力的向日葵，带来积极向上的力量。', 79.00, 80, 4, 'images/sunflowers.jpg', '上架', 90, 300),
('蓝色妖姬单支', '神秘高贵的蓝色妖姬，送给特别的TA。', 45.00, 40, 1, 'images/blue_rose.jpg', '新品', 25, 180),
('感恩教师节花篮', '精心搭配的花篮，献给辛勤的园丁。', 158.00, 50, 4, 'images/teachers_day_basket.jpg', '热销推荐', 60, 400),
('发财树盆栽', '寓意美好的发财树，适合开业、乔迁等喜庆场合。', 188.00, 70, 5, 'images/money_tree.jpg', '下架', 50, 280),
('永生花音乐盒', '将爱意永恒珍藏，搭配动听音乐，是浪漫的礼物选择。', 258.00, 20, 4, 'images/preserved_flower_music_box.jpg', '上架', 15, 150);

-- 订单
-- 张三的订单
INSERT INTO `orders` (`user_id`, `total_amount`, `status`, `shipping_address`, `contact_phone`) VALUES
(2, 287.00, '已完成', '北京市朝阳区幸福小区1号楼2单元301室', '13900139000'),
(2, 59.00, '待付款', '北京市朝阳区幸福小区1号楼2单元301室', '13900139000');

-- 李四的订单
INSERT INTO `orders` (`user_id`, `total_amount`, `status`, `shipping_address`, `contact_phone`) VALUES
(3, 128.00, '已发货', '上海市浦东新区世纪大道88号', '13700137000');

-- 订单项
-- 张三的第一个订单项
INSERT INTO `order_items` (`order_id`, `product_id`, `quantity`, `price_at_purchase`) VALUES
(1, 1, 1, 199.00), -- 红玫瑰花束
(1, 2, 1, 88.00);  -- 粉色康乃馨

-- 张三的第二个订单项 (待付款)
INSERT INTO `order_items` (`order_id`, `product_id`, `quantity`, `price_at_purchase`) VALUES
(2, 5, 1, 59.00); -- 多肉植物组合

-- 李四的订单项
INSERT INTO `order_items` (`order_id`, `product_id`, `quantity`, `price_at_purchase`) VALUES
(3, 3, 1, 128.00); -- 白色香水百合


-- 评价
-- 张三对红玫瑰的评价
INSERT INTO `reviews` (`user_id`, `product_id`, `order_id`, `rating`, `comment`, `status`) VALUES
(2, 1, 1, 5, '非常漂亮的红玫瑰，女朋友很喜欢！配送也很快。', '已批准');

-- 李四对白色百合的评价 (匿名)
INSERT INTO `reviews` (`user_id`, `product_id`, `order_id`, `rating`, `comment`, `is_anonymous`, `status`, `image_url`) VALUES
(3, 3, 3, 4, '百合花很新鲜，香味宜人。包装如果再精致一点就更好了。', TRUE, '待审核', 'images/review_lily.jpg');

-- 王五对多肉的评价 (假设他买过，但订单未在此处列出，实际应有关联订单)
-- 为了示例，我们假设一个已存在的订单ID (例如订单1，尽管用户不匹配，仅作评价表示例)
INSERT INTO `reviews` (`user_id`, `product_id`, `order_id`, `rating`, `comment`, `status`) VALUES
(4, 5, 1, 5, '多肉组合非常可爱，物流给力！', '已批准');

-- 购物车示例 (可选，根据业务逻辑决定是否需要预置)
INSERT INTO `cart` (`user_id`, `product_id`, `quantity`) VALUES
(2, 4, 1), -- 张三购物车里有七夕限定礼盒
(3, 1, 2); -- 李四购物车里有两束红玫瑰
