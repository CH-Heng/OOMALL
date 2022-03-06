package cn.edu.xmu.oomall.payment.service;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.dao.PaymentPatternDao;
import cn.edu.xmu.oomall.payment.model.bo.PaymentPattern;
import cn.edu.xmu.oomall.payment.model.po.PaymentPatternPo;
import cn.edu.xmu.oomall.payment.model.vo.PaymentPatternRetVo;
import cn.edu.xmu.oomall.payment.model.vo.PaymentPatternSimpleRetVo;
import cn.edu.xmu.oomall.payment.model.vo.UserSimpleRetVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/03/9:31
 */
@Service
public class PaymentPatternService {
    @Autowired
    PaymentPatternDao paymentPatternDao;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentPatternStates() {
        return paymentPatternDao.getPaymentPatternStates();
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getValidPaymentPatterns() {
        ReturnObject ret = paymentPatternDao.getValidPaymentPatterns();
        if(ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        List<PaymentPatternPo> paymentPatternPos = (List<PaymentPatternPo>) ret.getData();
        List<Object> retVos= new ArrayList<>();
        for (PaymentPatternPo paymentPatternPo:paymentPatternPos){
            PaymentPatternRetVo paymentPatternRetVo = (PaymentPatternRetVo) cloneVo(paymentPatternPo,PaymentPatternRetVo.class);
            UserSimpleRetVo creator = new UserSimpleRetVo(paymentPatternPo.getCreatorId(),paymentPatternPo.getCreatorName());
            UserSimpleRetVo modifier = new UserSimpleRetVo(paymentPatternPo.getModifierId(),paymentPatternPo.getModifierName());
            paymentPatternRetVo.setCreator(creator);
            paymentPatternRetVo.setModifier(modifier);
            retVos.add(paymentPatternRetVo);
        }
        return new ReturnObject(retVos);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getPaymentPatterns() {
        ReturnObject ret = paymentPatternDao.getPaymentPatterns();
        if(ret.getCode()!= ReturnNo.OK){
            return ret;
        }
        List<PaymentPatternPo> paymentPatternPos = (List<PaymentPatternPo>) ret.getData();
        List<Object> retVos= new ArrayList<>();
        for (PaymentPatternPo paymentPatternPo:paymentPatternPos){
            PaymentPatternRetVo paymentPatternRetVo = (PaymentPatternRetVo) cloneVo(paymentPatternPo,PaymentPatternRetVo.class);
            UserSimpleRetVo creator = new UserSimpleRetVo(paymentPatternPo.getCreatorId(),paymentPatternPo.getCreatorName());
            UserSimpleRetVo modifier = new UserSimpleRetVo(paymentPatternPo.getModifierId(),paymentPatternPo.getModifierName());
            paymentPatternRetVo.setCreator(creator);
            paymentPatternRetVo.setModifier(modifier);
            retVos.add(paymentPatternRetVo);
        }
        return new ReturnObject(retVos);
    }
}
