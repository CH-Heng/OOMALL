package cn.edu.xmu.oomall.customer.model.bo;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coupon {
    public enum State {
        RECEIVED((byte) 0, "已领取"),
        USED((byte) 1, "已使用"),
        INVALID((byte) 2, "已失效");

        private byte code;

        private String state;

        State(byte code, String state) {
            this.code = code;
            this.state = state;
        }

        public byte getCode() {
            return code;
        }

        public String getState() {
            return state;
        }
    }

    private Long id;
    private String couponSn;
    private String name;
    private Long customerId;
    private Long activityId;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Byte state;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
