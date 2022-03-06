package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.mapper.PaymentPatternPoMapper;
import cn.edu.xmu.oomall.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.oomall.payment.model.bo.Payment;
import cn.edu.xmu.oomall.payment.model.bo.PaymentPattern;
import cn.edu.xmu.oomall.payment.model.po.PaymentPatternPo;
import cn.edu.xmu.oomall.payment.model.po.PaymentPatternPoExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/03/9:28
 */
@Repository
public class PaymentPatternDao {

    private static final Logger logger = LoggerFactory.getLogger(PaymentPattern.class);

    @Autowired
    PaymentPoMapper paymentPoMapper;

    @Autowired
    PaymentPatternPoMapper paymentPatternPoMapper;

    /**
     * 获取所有的支付方式的状态
     * @return
     */
    public ReturnObject getPaymentPatternStates(){
        List<Map<String,Object>> stateList=new ArrayList<>();
        for(PaymentPattern.State state:PaymentPattern.State.values()){
            Map<String,Object> temp=new HashMap<>();
            temp.put("code",state.getCode());
            temp.put("name",state.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }

    /**
     * 获取当前所有有效的支付方式
     * @return
     */
    public ReturnObject getValidPaymentPatterns(){
        try {
            PaymentPatternPoExample example = new PaymentPatternPoExample();
            PaymentPatternPoExample.Criteria criteria = example.createCriteria();
            criteria.andStateEqualTo((byte)0);
            List<PaymentPatternPo> paymentPatternPos = paymentPatternPoMapper.selectByExample(example);
            return new ReturnObject(paymentPatternPos);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 获取当前所有有效的支付方式
     * @return
     */
    public ReturnObject getPaymentPatterns(){
        try {
            PaymentPatternPoExample example = new PaymentPatternPoExample();
            PaymentPatternPoExample.Criteria criteria = example.createCriteria();
            List<PaymentPatternPo> paymentPatternPos = paymentPatternPoMapper.selectByExample(example);
            return new ReturnObject(paymentPatternPos);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    public ReturnObject getPaymentPattern(Long id){
        try {
            PaymentPatternPo paymentPatternPo = paymentPatternPoMapper.selectByPrimaryKey(id);
            return new ReturnObject(paymentPatternPo);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

}
