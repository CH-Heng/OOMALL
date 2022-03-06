package cn.edu.xmu.oomall.customer.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    public enum State {
        NORMAL(0,"正常"),
        BANNED(1,"被封禁");

        private static final Map<Integer, State> STATE_MAP;
        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            STATE_MAP = new HashMap();
            for (State enum1 : values()) {
                STATE_MAP.put(enum1.code, enum1);
            }
        }
        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static State getTypeByCode(Integer code) {
            return STATE_MAP.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;
    private String userName;
    private String password;
    private String realName;
    private Long point;
    private Byte state;
    private String email;
    private String mobile;
    private Byte beDeleted;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
