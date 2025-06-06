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

-- 傾印  資料表 pikababy.bankno 結構
CREATE TABLE IF NOT EXISTS `bankno` (
  `id` int unsigned NOT NULL AUTO_INCREMENT,
  `bCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '銀行代碼',
  `bName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '銀行名稱',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='銀行代碼';

-- 正在傾印表格  pikababy.bankno 的資料：~37 rows (近似值)
DELETE FROM `bankno`;
INSERT INTO `bankno` (`id`, `bCode`, `bName`) VALUES
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
  `pic1` blob NOT NULL,
  `pic2` blob NOT NULL,
  `pic3` blob NOT NULL,
  `pCondition` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品狀況概述(100字內)',
  `quantity` int NOT NULL,
  `delivery` int NOT NULL COMMENT '0: 到府取貨；1: 物流宅配；2: 超商寄件；3: 已交貨',
  `deliveryDate` date DEFAULT NULL COMMENT '預約交貨日期(面交)',
  `review` int NOT NULL DEFAULT '0' COMMENT '0:未審核；1:通過；-1:未通過',
  `price` int DEFAULT NULL COMMENT '預估價格',
  `withdrawId` int DEFAULT NULL COMMENT '提款申請 id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_apply_withdraw` (`withdrawId`),
  CONSTRAINT `FK_apply_withdraw` FOREIGN KEY (`withdrawId`) REFERENCES `withdrawal` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='二手託售申請紀錄';

-- 正在傾印表格  pikababy.consignment 的資料：~0 rows (近似值)
DELETE FROM `consignment`;

-- 傾印  資料表 pikababy.withdrawal 結構
CREATE TABLE IF NOT EXISTS `withdrawal` (
  `id` int NOT NULL AUTO_INCREMENT,
  `custId` int NOT NULL COMMENT '客戶id',
  `amount` int NOT NULL COMMENT '可提領金額',
  `applyDate` date NOT NULL COMMENT '申請提款日期',
  `withdraw` int NOT NULL DEFAULT '0' COMMENT '0:未撥款；1:已撥款',
  `withdrawDate` date DEFAULT NULL COMMENT '撥款日期',
  `bankId` int unsigned DEFAULT NULL,
  `bankAccount` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `FK_withdraw_bank` (`bankId`),
  CONSTRAINT `FK_withdraw_bank` FOREIGN KEY (`bankId`) REFERENCES `bankno` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='二手提款申請紀錄';

-- 正在傾印表格  pikababy.withdrawal 的資料：~0 rows (近似值)
DELETE FROM `withdrawal`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
