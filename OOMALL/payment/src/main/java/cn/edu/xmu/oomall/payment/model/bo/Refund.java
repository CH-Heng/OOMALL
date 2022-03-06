package cn.edu.xmu.oomall.payment.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Refund {
    public enum State{
        UNREFUNDED((byte)0 ,"待支付"),
        REFUNDED((byte)1,"已支付"),
        RECONCILED((byte)2, "已对账"),
        LIQUDATED((byte)3, "已清算"),
        CANCLED((byte)4, "已取消"),
        FAILED((byte)5, "失败");

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
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long paymentId;
    private Long amount;
    private String documentId;
    private Byte documentType;
    private LocalDateTime refundTime;
    private Byte state;
    private String descr;
    private Long adjustId;
    private String adjustName;
    private LocalDateTime adjustTime;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
