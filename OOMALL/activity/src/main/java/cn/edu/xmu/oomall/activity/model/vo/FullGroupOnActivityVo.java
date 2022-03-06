package cn.edu.xmu.oomall.activity.model.vo;

import cn.edu.xmu.oomall.activity.constant.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * @author Gao Yanfeng
 * @date 2021/11/12
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "完整团购活动视图")
public class FullGroupOnActivityVo {
    private Long id;

    private String name;

    private Long shopId;

    private List<GroupOnStrategyVo> strategy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime beginTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZZZZ")
    private ZonedDateTime endTime;

    private SimpleAdminUserVo creator;

    private SimpleAdminUserVo modifier;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime gmtCreate;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMAT, timezone = "GMT+8")
    private LocalDateTime gmtModified;

    private Byte state;
}
