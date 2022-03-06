
DROP TABLE IF EXISTS `oomall_alipay_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_alipay_payment` (
`id` bigint NOT NULL AUTO_INCREMENT,
`out_trade_no` varchar(128) DEFAULT NULL,
`send_pay_date` datetime DEFAULT NULL,
`total_amount` bigint DEFAULT NULL,
`buyer_pay_amount` bigint DEFAULT NULL,
`trade_status` tinyint DEFAULT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付宝订单';

DROP TABLE IF EXISTS `oomall_alipay_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_alipay_refund` (
 `id` bigint NOT NULL AUTO_INCREMENT,
 `out_trade_no` varchar(128) DEFAULT NULL,
 `out_request_no` varchar(128) DEFAULT NULL,
 `total_amount` bigint DEFAULT NULL,
 `refund_amount` bigint DEFAULT NULL,
 `gmt_refund_pay` datetime DEFAULT NULL,
 `refund_status` tinyint  DEFAULT NULL,
 PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付宝退款单';