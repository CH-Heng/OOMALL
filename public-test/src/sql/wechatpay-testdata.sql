LOCK TABLES `oomall_wechatpay_refund` WRITE;
INSERT INTO `oomall_wechatpay_refund` VALUES (1, '10', '10', 'SUCCESS', 100, 100, '2021-12-02 15:08:42');
UNLOCK TABLES;

LOCK TABLES `oomall_wechatpay_transaction` WRITE;
INSERT INTO `oomall_wechatpay_transaction` VALUES (1, '1', 'SUCCESS', 100, 100, '2021-12-02 13:02:47');
INSERT INTO `oomall_wechatpay_transaction` VALUES (2, '2', 'SUCCESS', 100, 100, '2021-12-02 14:50:33');
INSERT INTO `oomall_wechatpay_transaction` VALUES (3, '3', 'SUCCESS', 100, 100, '2021-12-02 14:50:45');
INSERT INTO `oomall_wechatpay_transaction` VALUES (4, '4', 'SUCCESS', 100, 100, '2021-12-02 14:50:58');
INSERT INTO `oomall_wechatpay_transaction` VALUES (5, '5', 'SUCCESS', 100, 100, '2021-12-02 14:51:09');
INSERT INTO `oomall_wechatpay_transaction` VALUES (6, '6', 'SUCCESS', 100, 100, '2021-12-02 14:51:20');
INSERT INTO `oomall_wechatpay_transaction` VALUES (7, '7', 'SUCCESS', 100, 100, '2021-12-02 14:51:33');
INSERT INTO `oomall_wechatpay_transaction` VALUES (8, '8', 'SUCCESS', 100, 100, '2021-12-02 14:51:45');
INSERT INTO `oomall_wechatpay_transaction` VALUES (9, '9', 'CLOSED', 100, NULL, '2021-12-02 15:03:35');
INSERT INTO `oomall_wechatpay_transaction` VALUES (10, '10', 'REFUND', 100, 100, '2021-12-02 15:07:19');
UNLOCK TABLES;