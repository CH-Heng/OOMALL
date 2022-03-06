package cn.edu.xmu.oomall.coupon.microservice.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnsaleVo {

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

    @Data
    class ShareAct {
        private Long id;
        private String name;
    }

    @Data
    class CreatedBy {
        private Long id;
        private String username;
    }

    @Data
    class ModifiedBy {
        private Long id;
        private String username;
    }

    private Long id;

    private ShopVo shop;

    private ProductVo product;

    private Integer price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private LocalDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private LocalDateTime endTime;

    private Integer quantity;

    private Integer type;

    private Byte state;

    private ShareAct shareAct;

    private CreatedBy createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private LocalDateTime gmtCreate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private LocalDateTime gmtModified;

    private ModifiedBy modifiedBy;
}