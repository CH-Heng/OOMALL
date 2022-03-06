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
public class PaymentPattern {
    public enum State{
        ACTIVATED((byte)0 ,"可用"),
        BANED((byte)1,"不可用");

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
    private String name;
    private Byte state;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String className;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

}
