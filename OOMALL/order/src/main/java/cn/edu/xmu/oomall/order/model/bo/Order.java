package cn.edu.xmu.oomall.order.model.bo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    public enum State {
        /**
         * 代付款
         */
        TO_BE_PAID(100, "待付款"),
        /**
         * 待收货
         */
        TO_BE_RECEIVED(200, "待收货"),
        /**
         * 已发货
         */
        DELIVERED(300, "已发货"),
        /**
         * 已完成
         */
        COMPLETED(400, "已完成"),
        /**
         * 已取消
         */
        CANCELED(500, "已取消"),
        /**
        待退款
         */
        TO_REFUND(501,"待退款"),
        /**
        已退款
         */
        REFUNDED(502,"已退款"),
        /**
         * 新订单
         */
        NEW(101, "新订单"),
        /**
         *
         */
        THE_BALANCE_TO_BE_PAID(102,"待支付尾款"),
        /**
         * 付款完成
         */
        PAID(201, "付款完成"),
        /**
         * 待成团
         */
        GROUPON_THRESHOLD_TO_BE_REACH(202, "待成团");

        private static final Map<Integer, State> stateMap;
        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }
        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;
    private Long customerId;
    private Long shopId;
    private String orderSn;
    private Long pid;
    private String consignee;
    private Long regionId;
    private String address;
    private String mobile;
    private String message;
    private Long advancesaleId;
    private Long grouponId;
    private Long expressFee;
    private Long discountPrice;
    private Long originPrice;
    private Long point;
    private LocalDateTime confirmTime;
    private String shipmentSn;
    private Integer state;
    private Byte beDeleted;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
