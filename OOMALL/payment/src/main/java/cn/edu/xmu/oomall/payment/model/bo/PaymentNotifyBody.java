package cn.edu.xmu.oomall.payment.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/20/9:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotifyBody {
    public enum State{
        PAYMENT((byte)0 ,"支付"),
        REFUND((byte)1,"退款");

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
    private String tradeSn;
    private Long patternId;
    private Byte type;
}
