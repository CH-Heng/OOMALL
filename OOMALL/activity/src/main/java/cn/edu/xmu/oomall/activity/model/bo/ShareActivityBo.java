package cn.edu.xmu.oomall.activity.model.bo;

import cn.edu.xmu.oomall.activity.model.vo.StrategyVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.tomcat.jni.Local;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/15 19:11
 */
@Data
@AllArgsConstructor
public class ShareActivityBo implements Serializable {
    private Long id;
    private Long shopId;
    private String shopName;
    private String name;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Byte state;
    private Long creatorId;
    private String creatorName;
    private Long modifierId;
    private String modifierName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private List<StrategyVo> strategy;
    public ShareActivityBo(){
        strategy = new ArrayList<>();
    }
}
