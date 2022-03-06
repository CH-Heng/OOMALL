package cn.edu.xmu.oomall.payment.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 15:00
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorPayment {
    private Long id;
    private String tradeSn;
    private Long patternId;
    private Long income;
    private Long expenditure;
    private Long documentId;
    private Byte state;
    private LocalDateTime time;
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

    public enum State{
        PENDING((byte)0 ,"待处理"),
        SOLVED((byte)1,"已处理");

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
}
