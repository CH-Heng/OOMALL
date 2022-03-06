package cn.edu.xmu.oomall.goods.model.bo;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.goods.model.vo.NewOnSaleRetVo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zijun Min 22920192204257
 **/
@Data
public class OnSaleGetBo implements Serializable {
    private Long id;
    private Long shopId;
    private Long productId;
    private Long price;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer quantity;
    private Byte type;
    private Long activityId;
    private Long shareActId;
    private Byte state;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Integer maxQuantity;
    private Integer numKey;

    public Type getType() {
        return Type.getTypeByCode(Byte.valueOf(type));
    }

    public void setType(Type type) {
        this.type=(type.getCode().byteValue());
    }

    public State getState() {
        return State.getStatusByCode((byte) state);
    }

    public void setState(State state) {
        Byte code=state.getCode();
        Byte b=code.byteValue();
        this.state=b;
    }

    public enum Type {
        NOACTIVITY((byte) 0, "无活动"),
        SECKILL((byte) 1, "秒杀"),
        GROUPON((byte) 2, "团购"),
        PRESALE((byte) 3, "预售");

        private static final Map<Byte, Type> TYPE_MAP;

        static { 
            TYPE_MAP = new HashMap();
            for (Type enum1 : values()) {
                TYPE_MAP.put(enum1.code, enum1);
            }
        }

        private Byte code;
        private String description;

        Type(Byte code, String description) {
            this.code = code;
            this.description = description;
        }

        public static Type getTypeByCode(Byte code) {
            return TYPE_MAP.get(code);
        }

        public Byte getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

    }


    public enum State {
        DRAFT((byte) 0, "草稿"),
        ONLINE((byte) 1, "上线"),
        OFFLINE((byte) 2, "下线");

        private static final Map<Byte, State> STATE_MAP;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            STATE_MAP = new HashMap();
            for (State enum1 : values()) {
                STATE_MAP.put(enum1.code, enum1);
            }
        }

        private Byte code;
        private String description;

        State(Byte code, String description) {
            this.code=code;
            this.description=description;
        }

        public static State getStatusByCode(Byte code){
            return STATE_MAP.get(code);
        }

        public Byte getCode(){
            return code;
        }

        public String getDescription() {return description;}

    }


}