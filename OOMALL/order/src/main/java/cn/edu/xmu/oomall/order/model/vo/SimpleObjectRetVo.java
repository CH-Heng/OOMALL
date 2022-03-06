package cn.edu.xmu.oomall.order.model.vo;

import cn.edu.xmu.oomall.core.model.VoObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/12/4
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleObjectRetVo implements VoObject {
    private Long id;
    private String name;

    @Override
    public Object createVo() {
        return this;
    }

    @Override
    public Object createSimpleVo() {
        return this;
    }
}
