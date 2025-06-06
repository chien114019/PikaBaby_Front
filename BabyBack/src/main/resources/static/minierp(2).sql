-- --------------------------------------------------------
-- 主機:                           
-- 伺服器版本:                        8.0.36 - MySQL Community Server - GPL
-- 伺服器作業系統:                      Win64
-- HeidiSQL 版本:                  12.7.0.6850
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- 傾印 pikababy 的資料庫結構
CREATE DATABASE IF NOT EXISTS `pikababy` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `pikababy`;

-- 傾印  資料表 pikababy.bank_no 結構
CREATE TABLE IF NOT EXISTS `bank_no` (
  `id` int NOT NULL AUTO_INCREMENT,
  `b_code` varchar(255) DEFAULT NULL,
  `b_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在傾印表格  pikababy.bank_no 的資料：~0 rows (近似值)
DELETE FROM `bank_no`;
INSERT INTO `bank_no` (`id`, `b_code`, `b_name`) VALUES
	(1, '004', '臺灣銀行'),
	(2, '005', '臺灣土地銀行'),
	(3, '006', '合作金庫銀行'),
	(4, '007', '第一銀行'),
	(5, '008', '華南銀行'),
	(6, '009', '彰化銀行'),
	(7, '011', '上海儲蓄銀行'),
	(8, '012', '台北富邦銀行'),
	(9, '013', '國泰世華銀行'),
	(10, '016', '高雄銀行'),
	(11, '017', '兆豐銀行'),
	(12, '021', '花旗銀行'),
	(13, '048', '王道銀行'),
	(14, '050', '臺灣企業銀行'),
	(15, '052', '渣打銀行'),
	(16, '053', '台中銀行'),
	(17, '054', '京城銀行'),
	(18, '081', '滙豐銀行'),
	(19, '101', '瑞興銀行'),
	(20, '102', '華泰銀行'),
	(21, '103', '新光銀行'),
	(22, '108', '陽信銀行'),
	(23, '118', '板信銀行'),
	(24, '147', '三信銀行'),
	(25, '803', '聯邦銀行'),
	(26, '805', '遠東商銀'),
	(27, '806', '元大銀行'),
	(28, '807', '永豐銀行'),
	(29, '808', '玉山銀行'),
	(30, '809', '凱基銀行'),
	(31, '810', '星展銀行'),
	(32, '812', '台新銀行'),
	(33, '816', '安泰銀行'),
	(34, '822', '中國信託銀行'),
	(35, '823', '將來銀行'),
	(36, '824', '連線銀行'),
	(37, '826', '樂天銀行');

-- 傾印  資料表 pikababy.consignment 結構
CREATE TABLE IF NOT EXISTS `consignment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `custId` int NOT NULL,
  `applyDate` date NOT NULL,
  `productName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` int NOT NULL,
  `pic1` varbinary(255) DEFAULT NULL,
  `pic2` varbinary(255) DEFAULT NULL,
  `pic3` varbinary(255) DEFAULT NULL,
  `pCondition` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品狀況概述(100字內)',
  `quantity` int NOT NULL,
  `delivery` int NOT NULL COMMENT '0: 到府取貨；1: 物流宅配；2: 超商寄件；3: 已交貨',
  `deliveryDate` date DEFAULT NULL COMMENT '預約交貨日期(面交)',
  `review` int NOT NULL DEFAULT '0' COMMENT '0:未審核；1:通過；-1:未通過',
  `price` int DEFAULT NULL COMMENT '預估價格',
  `withdrawId` int DEFAULT NULL COMMENT '提款申請 id',
  `apply_date` date DEFAULT NULL,
  `delivery_date` date DEFAULT NULL,
  `p_condition` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `product_name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `cust_id` bigint DEFAULT NULL,
  `withdraw_id` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_con_withdraw` (`withdrawId`),
  KEY `FK_con_cust` (`custId`),
  KEY `FKf5vosb4ytld9d8l8pvmg54dtf` (`type`),
  KEY `FKd0rmk1lpmru5f1b5s1ijant5o` (`withdraw_id`),
  CONSTRAINT `FK_con_cust` FOREIGN KEY (`custId`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_con_pType` FOREIGN KEY (`type`) REFERENCES `producttype` (`id`),
  CONSTRAINT `FK_con_withdraw` FOREIGN KEY (`withdrawId`) REFERENCES `withdrawal` (`id`),
  CONSTRAINT `FKd0rmk1lpmru5f1b5s1ijant5o` FOREIGN KEY (`withdraw_id`) REFERENCES `with_drawal` (`id`),
  CONSTRAINT `FKf5vosb4ytld9d8l8pvmg54dtf` FOREIGN KEY (`type`) REFERENCES `product_type` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='二手託售申請紀錄';

-- 正在傾印表格  pikababy.consignment 的資料：~0 rows (近似值)
DELETE FROM `consignment`;

-- 傾印  資料表 pikababy.customer 結構
CREATE TABLE IF NOT EXISTS `customer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='顧客資料表';

-- 正在傾印表格  pikababy.customer 的資料：~0 rows (近似值)
DELETE FROM `customer`;
INSERT INTO `customer` (`id`, `name`, `phone`, `email`, `address`) VALUES
	(1, '資展', '04-12345678', 'ispan520@gmail.com', '台中市南屯區公益路'),
	(2, '王小寶', '0988-999777', 'bao123@gmail.com', '台中市南屯區大墩路');

-- 傾印  資料表 pikababy.product 結構
CREATE TABLE IF NOT EXISTS `product` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `price` double DEFAULT NULL,
  `stock` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品資料表';

-- 正在傾印表格  pikababy.product 的資料：~4 rows (近似值)
DELETE FROM `product`;
INSERT INTO `product` (`id`, `name`, `price`, `stock`) VALUES
	(2, '筆記型電腦', 39900, 5),
	(3, '滑鼠', 980, 6),
	(4, '鍵盤', 1990, 5),
	(5, '滑鼠墊', 990, 10);

-- 傾印  資料表 pikababy.product_type 結構
CREATE TABLE IF NOT EXISTS `product_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在傾印表格  pikababy.product_type 的資料：~0 rows (近似值)
DELETE FROM `product_type`;
INSERT INTO `product_type` (`id`, `type_name`) VALUES
	(1, '嬰兒服裝'),
	(2, '嬰兒推車'),
	(3, '嬰兒床'),
	(4, '嬰兒用品'),
	(5, '玩具'),
	(6, '汽座'),
	(7, '學步用品'),
	(8, '幼兒服裝'),
	(9, '幼教用品'),
	(10, '兒童服裝'),
	(11, '餐椅'),
	(12, '電器用品');

-- 傾印  資料表 pikababy.purchase_order 結構
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `supplier_id` int DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `supplier_id` (`supplier_id`),
  CONSTRAINT `purchase_order_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `supplier` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='採購單';

-- 正在傾印表格  pikababy.purchase_order 的資料：~0 rows (近似值)
DELETE FROM `purchase_order`;

-- 傾印  資料表 pikababy.purchase_order_detail 結構
CREATE TABLE IF NOT EXISTS `purchase_order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `unit_price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `purchase_order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `purchase_order` (`id`),
  CONSTRAINT `purchase_order_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='採購詳細資料表';

-- 正在傾印表格  pikababy.purchase_order_detail 的資料：~0 rows (近似值)
DELETE FROM `purchase_order_detail`;

-- 傾印  資料表 pikababy.return_order 結構
CREATE TABLE IF NOT EXISTS `return_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `reason` varchar(255) DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `return_no` varchar(255) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在傾印表格  pikababy.return_order 的資料：~0 rows (近似值)
DELETE FROM `return_order`;

-- 傾印  資料表 pikababy.return_order_detail 結構
CREATE TABLE IF NOT EXISTS `return_order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `qty` int DEFAULT NULL,
  `total` double DEFAULT NULL,
  `unit_price` double DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `return_order_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1yv8wnq6592pgmwnms00kiicq` (`return_order_id`),
  CONSTRAINT `FK1yv8wnq6592pgmwnms00kiicq` FOREIGN KEY (`return_order_id`) REFERENCES `return_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在傾印表格  pikababy.return_order_detail 的資料：~0 rows (近似值)
DELETE FROM `return_order_detail`;

-- 傾印  資料表 pikababy.sales_order 結構
CREATE TABLE IF NOT EXISTS `sales_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `customer_id` int DEFAULT NULL,
  `order_date` date DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `sales_order_ibfk_1` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='銷貨單';

-- 正在傾印表格  pikababy.sales_order 的資料：~2 rows (近似值)
DELETE FROM `sales_order`;
INSERT INTO `sales_order` (`id`, `customer_id`, `order_date`) VALUES
	(1, 1, '2025-05-30'),
	(2, 1, '2025-05-30');

-- 傾印  資料表 pikababy.sales_order_detail 結構
CREATE TABLE IF NOT EXISTS `sales_order_detail` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` int DEFAULT NULL,
  `product_id` int DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `unit_price` double DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `sales_order_detail_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `sales_order` (`id`),
  CONSTRAINT `sales_order_detail_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='銷貨詳細資料表';

-- 正在傾印表格  pikababy.sales_order_detail 的資料：~2 rows (近似值)
DELETE FROM `sales_order_detail`;
INSERT INTO `sales_order_detail` (`id`, `order_id`, `product_id`, `quantity`, `unit_price`) VALUES
	(1, 1, 2, 1, 39900),
	(2, 2, 3, 2, 980);

-- 傾印  資料表 pikababy.supplier 結構
CREATE TABLE IF NOT EXISTS `supplier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `phone` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='供應商資料表';

-- 正在傾印表格  pikababy.supplier 的資料：~0 rows (近似值)
DELETE FROM `supplier`;

-- 傾印  資料表 pikababy.user_account 結構
CREATE TABLE IF NOT EXISTS `user_account` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `role` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='後臺使用者資料表';

-- 正在傾印表格  pikababy.user_account 的資料：~4 rows (近似值)
DELETE FROM `user_account`;
INSERT INTO `user_account` (`id`, `username`, `password`, `role`) VALUES
	(1, 'admin', '$2a$10$Kn1mSGBytbb8BkOUw5I4AeobBMFEZ0pA3C0Fb.QxPl5RkQG7qaK8G', 'ADMIN'),
	(2, 'sales', '$2a$10$Kn1mSGBytbb8BkOUw5I4AeobBMFEZ0pA3C0Fb.QxPl5RkQG7qaK8G', 'SALES'),
	(3, 'viewer', '$2a$10$Kn1mSGBytbb8BkOUw5I4AeobBMFEZ0pA3C0Fb.QxPl5RkQG7qaK8G', 'VIEWER'),
	(5, 'root', '1234', 'ROOT');

-- 傾印  資料表 pikababy.with_drawal 結構
CREATE TABLE IF NOT EXISTS `with_drawal` (
  `id` int NOT NULL AUTO_INCREMENT,
  `amount` int DEFAULT NULL,
  `apply_date` date DEFAULT NULL,
  `bank_account` varchar(255) DEFAULT NULL,
  `withdraw` int DEFAULT NULL,
  `withdraw_date` date DEFAULT NULL,
  `bank_id` int DEFAULT NULL,
  `cust_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK93xd22g3n4pavb2fqjb264fwk` (`bank_id`),
  CONSTRAINT `FK93xd22g3n4pavb2fqjb264fwk` FOREIGN KEY (`bank_id`) REFERENCES `bank_no` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在傾印表格  pikababy.with_drawal 的資料：~0 rows (近似值)
DELETE FROM `with_drawal`;
INSERT INTO `with_drawal` (`id`, `amount`, `apply_date`, `bank_account`, `withdraw`, `withdraw_date`, `bank_id`, `cust_id`) VALUES
	(1, 5000, '2025-06-01', '1245987495364862', 1, '2025-06-03', 19, 2);

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
