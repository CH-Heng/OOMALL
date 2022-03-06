package cn.edu.xmu.oomall.liquidation.util;

import cn.edu.xmu.oomall.liquidation.microservice.vo.PaymentRetVo;
import cn.edu.xmu.oomall.liquidation.util.base.Factory;

public class PaymentFactory implements Factory<PaymentRetVo> {

    @Override
    public PaymentRetVo create(Long id) {
        PaymentRetVo retVo = new PaymentRetVo();

        retVo.setId(id);
        retVo.setDocumentId("0000000000");
        retVo.setDocumentType((byte) 0);
        retVo.setState((byte) 2);

        return retVo;
    }
}
