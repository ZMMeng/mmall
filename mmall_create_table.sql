CREATE DATABASE IF NOT EXISTS `mmall` ;

use `mmall` ;

-- 建表时不要指定库名，会被当做表名的一部分
-- 用户表
CREATE TABLE IF NOT EXISTS `mmall_user` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户表id',
`username` varchar(50) NOT NULL COMMENT '用户名',
`password` varchar(50) NOT NULL COMMENT '用户密码，MD5加密',
`email` varchar(50) DEFAULT NULL,
`phone` varchar(20) DEFAULT NULL,
`question` varchar(100) DEFAULT NULL COMMENT '找回密码问题',
`answer` varchar(100) DEFAULT NULL COMMENT '找回密码答案',
`role` int(4) NOT NULL COMMENT '角色0-管理员,1-普通用户',
`create_time` datetime NOT NULL COMMENT '创建时间',
`update_time` datetime NOT NULL COMMENT '最后一次更新时间',
PRIMARY KEY (`id`),
UNIQUE KEY `user_name_unique` (`username`) USING BTREE -- 使用B树将用户民设置成唯一索引
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- 分类表
-- 涉及递归，采用树结构，当id=0时，为根节点
CREATE TABLE IF NOT EXISTS `mmall_category` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '类别id',
`parent_id` int(11) DEFAULT NULL COMMENT '父类别id，当id=0时表示根节点，一级类别',
`name` varchar(50) DEFAULT NULL COMMENT '类别名称',
`status` tinyint(1) DEFAULT '1' COMMENT '类别状态1-正常,2-已废弃',
`sort_order` int(4) DEFAULT NULL COMMENT '排序编号，同类展示顺序，数值相等则自然排序',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=100032 DEFAULT CHARSET=utf8 ;

-- 产品表
CREATE TABLE IF NOT EXISTS `mmall_product` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '商品id',
`category_id` int(11) NOT NULL COMMENT '分类id，对应mmall_category表的主键',
`name` varchar(100) NOT NULL COMMENT '商品名称',
`subtitle` varchar(200) DEFAULT NULL COMMENT '商品副标题',
`main_image` varchar(500) DEFAULT NULL COMMENT '产品主图，url相对地址，以防域名迁移',
`sub_image` text COMMENT '图片地址，json格式，扩展用',
`detail` text COMMENT '商品详情',
`price` decimal(20,2) NOT NULL COMMENT '价格，单位-元保留两位小数',
`stock` int(11) NOT NULL COMMENT '库存数量',
`status` int(6) DEFAULT '1' COMMENT '商品状态，1-在售，2-下架，3-删除',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8 ;

-- 购物车表
CREATE TABLE IF NOT EXISTS `mmall_cart` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '购物车id',
`user_id` int(11) NOT NULL COMMENT '购物车所属用户id',
`product_id` int(11) DEFAULT NULL COMMENT '商品id',
`quantity` int(11) DEFAULT NULL COMMENT '数量',
`checked` int(11) DEFAULT NULL COMMENT '是否选择，1=已勾选，0=未勾选',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`),
KEY `user_id_index` (`user_id`) USING BTREE -- 将user_id作为索引
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8 ;

-- 支付信息表
CREATE TABLE IF NOT EXISTS `mmall_pay_info` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '支付信息id',
`user_id` int(11) DEFAULT NULL COMMENT '用户id',
`order_no` bigint(20) DEFAULT NULL COMMENT '订单号',
`pay_platform` int(10) DEFAULT NULL COMMENT '支付平台：1-支付宝，2-微信',
`platform_number` varchar(200) DEFAULT NULL COMMENT '支付宝支付流水号',
`platform_status` varchar(20) DEFAULT NULL COMMENT '支付宝支付状态',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8 ;

-- 订单表
CREATE TABLE IF NOT EXISTS `mmall_order` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单id',
`order_no` bigint(20) DEFAULT NULL COMMENT '订单号',
`user_id` int(11) DEFAULT NULL COMMENT '用户id',
`shipping_id` int(11) DEFAULT NULL COMMENT '订单地址',
`payment` decimal(20,2) DEFAULT NULL COMMENT '实际付款金额，单位是元，保留两位小数',
`payment_type` int(4) DEFAULT NULL COMMENT '支付类型，1-在线支付',
`postage` int(10) DEFAULT NULL COMMENT '运费，单位是元',
`status` int(10) DEFAULT NULL COMMENT '订单状态：0-已取消，10-未付款，20-已付款，40-已发货，50-交易成功，60-交易关闭',
`payment_time` datetime DEFAULT NULL COMMENT '支付时间',
`send_time` datetime DEFAULT NULL COMMENT '发货时间',
`end_time` datetime DEFAULT NULL COMMENT '交易完成时间',
`close_time` datetime DEFAULT NULL COMMENT '交易关闭时间',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`),
KEY `order_no_index` (`order_no`) USING BTREE -- 将order_no作为索引
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8 ;

-- 订单明细表
CREATE TABLE IF NOT EXISTS `mmall_order_item` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单子表id',
`user_id` int(11) DEFAULT NULL COMMENT '订单所属用户id',
`order_no` bigint(20) DEFAULT NULL COMMENT '订单号',
`product_id` int(11) DEFAULT NULL COMMENT '商品id',
`product_name` varchar(100) DEFAULT NULL COMMENT '商品名称',
`product_image` varchar(500) DEFAULT NULL COMMENT '商品图片地址',
`current_unit_price` decimal(20,2) DEFAULT NULL COMMENT '生成订单时的商品单价，单位是元，保留两位小数',
`quantity` int(10) DEFAULT NULL COMMENT '商品数量',
`total_price` decimal(20,2) DEFAULT NULL COMMENT '商品总价，单位是元，保留两位小数',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`),
KEY `order_no_index` (`order_no`) USING BTREE, -- 将order_no作为索引
KEY `order_no_user_id_index` (`user_id`,`order_no`) USING BTREE -- 将user_id和order_no作为组合索引
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8 ;

-- 收货地址表
CREATE TABLE IF NOT EXISTS `mmall_shipping` (
`id` int(11) NOT NULL AUTO_INCREMENT COMMENT '收货地址id',
`user_id` int(11) DEFAULT NULL COMMENT '用户id',
`receiver_name` varchar(20) DEFAULT NULL COMMENT '收货姓名',
`receiver_phone` varchar(20) DEFAULT NULL COMMENT '收货固定电话',
`receiver_mobile` varchar(20) DEFAULT NULL COMMENT '收货移动电话',
`receiver_province` varchar(20) DEFAULT NULL COMMENT '省份',
`receiver_city` varchar(20) DEFAULT NULL COMMENT '城市',
`receiver_district` varchar(20) DEFAULT NULL COMMENT '区/县',
`receiver_address` varchar(200) DEFAULT NULL COMMENT '详细地址',
`receiver_zip` varchar(6) DEFAULT NULL COMMENT '邮编',
`create_time` datetime DEFAULT NULL COMMENT '创建时间',
`update_time` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8 ;




