package cn.edu.xmu.oomall.liquidation.model.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wwk
 * @date 2021/12/15
 */
@Data
public class Liquidation {

    private Long id;

    private Long shopId;

    private String shopName;

    private LocalDateTime liquidDate;

    private Long expressFee;

    private Long commission;

    private Long point;

    private Byte state;

    private Long shopRevenue;

    private Long creatorId;

    private String creatorName;

    private Long modifierId;

    private String modifierName;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public enum State {
        /**
         * NOT_REMITTED:未汇出
         * REMITTED:已汇出
         */
        NOT_REMITTED((byte)0,"未汇出"),
        REMITTED((byte)1,"已汇出");

        private static final Map<Byte, State> STATE_MAP;
        static {
            STATE_MAP = new HashMap();
            for (State enum1 : values()) {
                STATE_MAP.put(enum1.code, enum1);
            }
        }

        private byte code;
        private String description;

        State(byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Byte code) {
            return STATE_MAP.get(code);
        }

        public Byte getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
}
