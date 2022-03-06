package cn.edu.xmu.oomall.comment.model.bo;

import cn.edu.xmu.oomall.comment.model.vo.SimpleUserRetVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment{
    /**
     * 评论状态
     */
    public enum State{
        NOT_AUDIT((byte)0 ,"未审核"),
        PASS((byte)1,"评论成功"),
        FORBID((byte)2, "未通过");

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
    private Long productId;
    private Byte type;
    private String content;
    private Byte state;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private SimpleUserRetVo post;
    private SimpleUserRetVo audit;
    private LocalDateTime postTime;
    private LocalDateTime auditTime;
    private LocalDateTime gmtCreated;
    private LocalDateTime gmtModified;
}
