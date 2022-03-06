-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: 192.168.31.241    Database: order_payment
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `oomall_error_account`
--

DROP TABLE IF EXISTS `oomall_error_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_error_account` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `trade_sn` varchar(128) DEFAULT NULL,
  `pattern_id` bigint DEFAULT NULL,
  `income` bigint DEFAULT NULL,
  `expenditure` bigint DEFAULT NULL,
  `document_id` varchar(128) DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `adjust_id` bigint DEFAULT NULL,
  `adjust_name` varchar(128) DEFAULT NULL,
  `adjust_time` datetime DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_order`
--

DROP TABLE IF EXISTS `oomall_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint DEFAULT NULL,
  `shop_id` bigint DEFAULT NULL,
  `order_sn` varchar(128) DEFAULT NULL,
  `pid` bigint DEFAULT '0',
  `consignee` varchar(128) DEFAULT NULL,
  `region_id` bigint DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `message` varchar(500) DEFAULT NULL,
  `advancesale_id` bigint DEFAULT '0',
  `groupon_id` bigint DEFAULT '0',
  `express_fee` bigint DEFAULT '0',
  `discount_price` bigint DEFAULT '0',
  `origin_price` bigint DEFAULT NULL,
  `point` bigint DEFAULT '0',
  `confirm_time` datetime DEFAULT NULL,
  `shipment_sn` varchar(128) DEFAULT NULL,
  `state` int DEFAULT NULL,
  `be_deleted` tinyint DEFAULT '0',
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38050 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_order_item`
--

DROP TABLE IF EXISTS `oomall_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `shop_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `onsale_id` bigint DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `discount_price` bigint DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `coupon_activity_id` bigint DEFAULT NULL,
  `coupon_id` bigint DEFAULT NULL,
  `commented` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57148 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_payment`
--

DROP TABLE IF EXISTS `oomall_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `trade_sn` varchar(128) DEFAULT NULL,
  `pattern_id` bigint DEFAULT NULL,
  `amount` bigint DEFAULT NULL,
  `actual_amount` bigint DEFAULT NULL,
  `document_id` varchar(128) DEFAULT NULL,
  `document_type` tinyint DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `adjust_id` bigint DEFAULT NULL,
  `adjust_name` varchar(128) DEFAULT NULL,
  `adjust_time` datetime DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_payment_pattern`
--

DROP TABLE IF EXISTS `oomall_payment_pattern`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_payment_pattern` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `class_name` varchar(128) DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_refund`
--

DROP TABLE IF EXISTS `oomall_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_refund` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `trade_sn` varchar(128) DEFAULT NULL,
  `pattern_id` bigint DEFAULT NULL,
  `payment_id` bigint DEFAULT NULL,
  `amount` bigint DEFAULT NULL,
  `document_id` varchar(128) DEFAULT NULL,
  `document_type` tinyint DEFAULT NULL,
  `refund_time` datetime DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `adjust_id` bigint DEFAULT NULL,
  `adjust_name` varchar(128) DEFAULT NULL,
  `adjust_time` datetime DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-12-25 22:34:48
