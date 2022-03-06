package cn.edu.xmu.oomall.activity.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

import cn.edu.xmu.oomall.activity.model.vo.ShopVo;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jiawei Zheng
 * @date 2021-11-26
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvanceSale implements Serializable {


    private Long id;
    private Long shopId;
    private String shopName;
    private String name;
    private ZonedDateTime payTime;
    private Long advancePayPrice;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private ZonedDateTime gmtCreate;
    private ZonedDateTime gmtModified;
    private Byte state;

    public enum state {
        /**
         * OFFLINE为草稿态
         * ONLINE为上线态
         * DELETE为下线态
         */
        OFFLINE((byte)0,"草稿"),
        ONLINE((byte)1,"上线"),
        DELETE((byte)2,"下线");
        private static final Map<Byte, state> TYPE_MAP;
        static {
            TYPE_MAP = new HashMap();
            for (state ss : values()) {
                TYPE_MAP.put(ss.code, ss);
            }
        }
        private byte code;
        private String description;
        state(byte code, String description) {
            this.code = code;
            this.description = description;
        }
        public static state getTypeByCode(Integer code) {
            return TYPE_MAP.get(code);
        }
        public Byte getCode() {
            return code;
        }
        public String getDescription() {
            return description;
        }
    }
}
