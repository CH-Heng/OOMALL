DROP TABLE IF EXISTS `oomall_wechatpay_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_wechatpay_transaction`
(
    `id`               bigint NOT NULL AUTO_INCREMENT,
    `out_trade_no`     varchar(32)  DEFAULT NULL,
    `trade_state`      varchar(32)  DEFAULT NULL,
    `total`            int          DEFAULT NULL,
    `payer_total`      int          DEFAULT NULL,
    `success_time`     datetime     DEFAULT NULL,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信支付订单';



DROP TABLE IF EXISTS `oomall_wechatpay_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `oomall_wechatpay_refund`
(
    `id`                    bigint NOT NULL AUTO_INCREMENT,
    `out_refund_no`         varchar(64)  DEFAULT NULL,
    `out_trade_no`          varchar(32)  DEFAULT NULL,
    `status`                varchar(32)  DEFAULT NULL,
    `refund`                int          DEFAULT NULL,
    `payer_total`           int          DEFAULT NULL,
    `success_time`           datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='微信支付退款单';