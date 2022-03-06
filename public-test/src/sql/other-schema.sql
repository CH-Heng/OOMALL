-- MySQL dump 10.13  Distrib 8.0.27, for Win64 (x86_64)
--
-- Host: 192.168.31.241    Database: customer
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
-- Table structure for table `oomall_address`
--

DROP TABLE IF EXISTS `oomall_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint DEFAULT NULL,
  `region_id` bigint DEFAULT NULL,
  `detail` varchar(500) DEFAULT NULL,
  `consignee` varchar(128) DEFAULT NULL,
  `mobile` varchar(128) DEFAULT NULL,
  `be_default` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11168 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_aftersale_service`
--

DROP TABLE IF EXISTS `oomall_aftersale_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_aftersale_service` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint DEFAULT NULL,
  `order_item_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `shop_id` bigint DEFAULT NULL,
  `service_sn` varchar(128) DEFAULT NULL,
  `type` tinyint DEFAULT NULL,
  `reason` varchar(500) DEFAULT NULL,
  `conclusion` varchar(500) DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `region_id` bigint DEFAULT NULL,
  `detail` varchar(255) DEFAULT NULL,
  `consignee` varchar(128) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `customer_log_sn` varchar(128) DEFAULT NULL,
  `shop_log_sn` varchar(128) DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `be_deleted` tinyint DEFAULT NULL,
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
-- Table structure for table `oomall_coupon`
--

DROP TABLE IF EXISTS `oomall_coupon`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_coupon` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `coupon_sn` varchar(128) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `activity_id` bigint DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_customer`
--

DROP TABLE IF EXISTS `oomall_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_name` varchar(128) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `be_deleted` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24655 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_expenditure_item`
--

DROP TABLE IF EXISTS `oomall_expenditure_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_expenditure_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `liquid_id` bigint DEFAULT NULL,
  `refund_id` bigint DEFAULT NULL,
  `shop_id` bigint DEFAULT NULL,
  `revenue_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `product_name` varchar(128) DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `orderitem_id` bigint DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `amount` bigint DEFAULT NULL,
  `express_fee` bigint DEFAULT NULL,
  `commission` bigint DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `sharer_id` bigint DEFAULT NULL,
  `shop_revenue` bigint DEFAULT NULL,
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
-- Table structure for table `oomall_liquidation`
--

DROP TABLE IF EXISTS `oomall_liquidation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_liquidation` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `shop_id` bigint DEFAULT NULL,
  `shop_name` varchar(128) DEFAULT NULL,
  `liquid_date` datetime DEFAULT NULL,
  `express_fee` bigint DEFAULT NULL,
  `commission` bigint DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `shop_revenue` bigint DEFAULT NULL,
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
-- Table structure for table `oomall_revenue_item`
--

DROP TABLE IF EXISTS `oomall_revenue_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_revenue_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `liquid_id` bigint DEFAULT NULL,
  `payment_id` bigint DEFAULT NULL,
  `shop_id` bigint DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `orderitem_id` bigint DEFAULT NULL,
  `quantity` int DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `product_name` varchar(128) DEFAULT NULL,
  `amount` bigint DEFAULT NULL,
  `express_fee` bigint DEFAULT NULL,
  `commission` bigint DEFAULT NULL,
  `point` bigint DEFAULT NULL,
  `sharer_id` bigint DEFAULT NULL,
  `shop_revenue` bigint DEFAULT NULL,
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
-- Table structure for table `oomall_share`
--

DROP TABLE IF EXISTS `oomall_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_share` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sharer_id` bigint DEFAULT NULL,
  `share_act_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `onsale_id` bigint DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=24614 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_shopping_cart`
--

DROP TABLE IF EXISTS `oomall_shopping_cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_shopping_cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `customer_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `quantity` bigint DEFAULT NULL,
  `price` bigint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4408 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_successful_share`
--

DROP TABLE IF EXISTS `oomall_successful_share`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_successful_share` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `share_id` bigint DEFAULT NULL,
  `sharer_id` bigint DEFAULT NULL,
  `product_id` bigint DEFAULT NULL,
  `onsale_id` bigint DEFAULT NULL,
  `customer_id` bigint DEFAULT NULL,
  `state` tinyint DEFAULT NULL,
  `creator_id` bigint DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2232 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-12-25 22:33:34
