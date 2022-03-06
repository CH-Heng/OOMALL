package cn.edu.xmu.oomall.aftersale.model.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class Aftersale {
    /**
     * 售后的状态
     */
    public enum State{
        NEW((byte)0 ,"新建态"),
        TO_BE_DELIVERED_BY_CUSTOMER((byte)1,"待买家发货"),
        DELIVERED_BY_CUSTOMER((byte)2, "买家已发货"),
        TO_BE_REFUNDED((byte)3, "待退款"),
        TO_BE_DELIVERED_BY_SHOPKEEPER((byte)4,"待店家发货"),
        BE_DELIVERED_BY_SHOPKEEPER((byte)5,"店家已发货"),
        END((byte)6,"已结束"),
        CANCELLED((byte)7,"已取消"),
        TO_BE_PAYMENT((byte)8,"待支付");

        private static final Map<Byte, State> stateMap;
        static {
            stateMap = new HashMap();
            for (State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private Byte code;
        private String description;

        State(Byte code, String description) {
            this.code=code;
            this.description=description;
        }


        public Byte getCode() {
            return this.code;
        }

        public String getDescription() {
            return this.description;
        }

        public State getDescriptionByCode(Byte code){
            return stateMap.get(code);
        }
    }

    /**
     * 售后的类型
     */
    public enum AftersaleType{
        EXCHANGE((byte)0,"换货"),
        RETURN((byte)1,"退货"),
        REPAIR((byte)2, "维修");


        private static final Map<Byte, AftersaleType> AftersaleTypeMap;
        static {
            AftersaleTypeMap = new HashMap();
            for (AftersaleType enum1 : values()) {
                AftersaleTypeMap.put(enum1.code, enum1);
            }
        }

        private Byte code;
        private String description;

        AftersaleType(Byte code, String description) {
            this.code=code;
            this.description=description;
        }


        public Byte getCode() {
            return this.code;
        }

        public String getDescription() {
            return this.description;
        }

        public AftersaleType getDescriptionByCode(Byte code){
            return AftersaleTypeMap.get(code);
        }
    }
    private Long id;
    private Long orderId;
    private Long orderItemId;
    private Long customerId;
    private Long shopId;
    private String serviceSn;
    private Byte type;
    private String reason;
    private String conclusion;
    private Long price;
    private Long quantity;
    private Long regionId;
    private String detail;
    private String consignee;
    private String mobile;
    private String customerLogSn;
    private String shopLogSn;
    private Byte state;
    private Byte beDeleted;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
