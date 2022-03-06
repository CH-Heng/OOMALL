package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.oomall.payment.mapper.PaymentPoMapper;
import cn.edu.xmu.oomall.payment.mapper.RefundPoMapper;
import cn.edu.xmu.oomall.payment.model.bo.Payment;
import cn.edu.xmu.oomall.payment.model.bo.Refund;
import cn.edu.xmu.oomall.payment.model.po.PaymentPo;
import cn.edu.xmu.oomall.payment.model.po.PaymentPoExample;
import cn.edu.xmu.oomall.payment.model.po.RefundPo;
import cn.edu.xmu.oomall.payment.model.po.RefundPoExample;
import cn.edu.xmu.oomall.payment.model.vo.PaymentRetVo;
import cn.edu.xmu.oomall.payment.model.vo.PaymentSimpleRetVo;
import cn.edu.xmu.oomall.payment.model.vo.RefundSimpleRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * Created with IntelliJ IDEA.
 *
 * @author BingShuai Liu
 * @studentId 22920192204245
 * @date 2021/12/02/22:29
 */
@Repository
public class PaymentDao {
    @Autowired
    PaymentPoMapper paymentPoMapper;

    @Autowired
    RefundPoMapper refundPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);

    /**
     * 获取支付状态
     * @return
     */
    public ReturnObject getPaymentStates(){
        List<Map<String,Object>> stateList=new ArrayList<>();
        for(Payment.State state:Payment.State.values()){
            Map<String,Object> temp=new HashMap<>();
            temp.put("code",state.getCode());
            temp.put("name",state.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }

    /**
     * 获取退款状态
     * @return
     */
    public ReturnObject getRefundStates(){
        List<Map<String,Object>> stateList=new ArrayList<>();
        for(Refund.State state:Refund.State.values()){
            Map<String,Object> temp=new HashMap<>();
            temp.put("code",state.getCode());
            temp.put("name",state.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }

    /**
     * 顾客获得支付信息
     * @param documentId
     * @return
     */
    public ReturnObject getPayment(String documentId){
        try{
            PaymentPoExample example = new PaymentPoExample();
            PaymentPoExample.Criteria criteria = example.createCriteria();
            criteria.andDocumentIdEqualTo(documentId);
            List<PaymentPo> paymentPos = paymentPoMapper.selectByExample(example);
            ReturnObject<List> returnObject = new ReturnObject(paymentPos);
            return Common.getListRetVo(returnObject, PaymentSimpleRetVo.class);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 顾客获得退款信息
     * @param documentId
     * @return
     */
    public ReturnObject getRefund(String documentId){
        try{
            RefundPoExample example = new RefundPoExample();
            RefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andDocumentIdEqualTo(documentId);
            List<RefundPo> refundPos = refundPoMapper.selectByExample(example);
            List<RefundSimpleRetVo>list = new ArrayList<>();
            for(RefundPo refundPo:refundPos){
                RefundSimpleRetVo refundSimpleRetVo = cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo(refundPo,RefundSimpleRetVo.class);
                refundSimpleRetVo.setId(refundPo.getId());
                list.add(refundSimpleRetVo);
            }
            ReturnObject<List> returnObject = new ReturnObject(refundPos);
            return Common.getListRetVo(returnObject, RefundSimpleRetVo.class);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }
    /**
     * 获取支付列表
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getPaymentInfo(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){
        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria = example.createCriteria();
        PageHelper.startPage(page,pageSize);
        List<PaymentPo> paymentPos = new ArrayList<>();
        try{
            if (null!=documentId){
                criteria.andDocumentIdEqualTo(documentId);
            }
            if (null!=state){
                criteria.andStateEqualTo(state);
            }
            if (null!=beginTime){
                criteria.andBeginTimeEqualTo(beginTime);
            }
            if (null!=endTime){
                criteria.andEndTimeEqualTo(endTime);
            }
            paymentPos = paymentPoMapper.selectByExample(example);
            PageInfo<VoObject> pageInfo = new PageInfo(paymentPos);
            ReturnObject ret = new ReturnObject<>(pageInfo);
            return Common.getPageRetVo(ret, PaymentSimpleRetVo.class);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 根据paymentPo返回一个paymentPo对象
     * @param paymentPo
     * @return
     */
    public ReturnObject getPaymentsByPo(PaymentPo paymentPo){
        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria = example.createCriteria();
        List<PaymentPo> paymentPos = new ArrayList<>();
        try{
            if (null!=paymentPo.getDocumentId()){
                criteria.andDocumentIdEqualTo(paymentPo.getDocumentId());
            }
            if (null!=paymentPo.getDocumentType()){
                criteria.andDocumentTypeEqualTo(paymentPo.getDocumentType());
            }
            paymentPos = paymentPoMapper.selectByExample(example);
            if (paymentPos.size()==0){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(paymentPos.get(0));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 获取支付明细
     * @param id
     * @return
     */
    public ReturnObject getPaymentDetailInfo(Long id){
        try{
            PaymentPo paymentPo = paymentPoMapper.selectByPrimaryKey(id);
            if (paymentPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(paymentPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 修改支付
     * @param paymentPo
     * @return
     */
    public ReturnObject modifyPayment(PaymentPo paymentPo){
        int ret;
        try{
            ret = paymentPoMapper.updateByPrimaryKeySelective(paymentPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if (ret==0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }else{
            return new ReturnObject();
        }
    }

    /**
     * 获取退款列表
     * @param documentId
     * @param state
     * @param beginTime
     * @param endTime
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getRefundInfo(String documentId, Byte state, LocalDateTime beginTime, LocalDateTime endTime, Integer page, Integer pageSize){
        RefundPoExample example = new RefundPoExample();
        RefundPoExample.Criteria criteria = example.createCriteria();
        PageHelper.startPage(page,pageSize);
        List<RefundPo> refundPos = new ArrayList<>();
        try{
            if (null!=documentId){
                criteria.andDocumentIdEqualTo(documentId);
            }
            if (null!=state){
                criteria.andStateEqualTo(state);
            }
            if (null!=beginTime){
                criteria.andRefundTimeGreaterThan(beginTime);
            }
            if (null!=endTime){
                criteria.andRefundTimeLessThan(endTime);
            }
            refundPos = refundPoMapper.selectByExample(example);
            if (refundPos.isEmpty()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(refundPos.get(0));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 获取退款明细
     * @param id
     * @return
     */
    public ReturnObject getRefundDetailInfo(Long id){
        try{
            RefundPo refundPo = refundPoMapper.selectByPrimaryKey(id);
            if (refundPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(refundPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 修改退款
     * @param refundPo
     * @return
     */
    public ReturnObject modifyRefund(RefundPo refundPo){
        int ret;
        try{
            ret = refundPoMapper.updateByPrimaryKeySelective(refundPo);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if (ret == 0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }else {
            return new ReturnObject();
        }
    }

    /**
     * 新增退款单
     * @param refundPo
     * @return
     */
    public ReturnObject createRefund(RefundPo refundPo){
        try {
            int ret = refundPoMapper.insert(refundPo);
            if (ret==0){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new ReturnObject(refundPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
    /**
     * 新建支付单
     * @param paymentPo
     * @return
     */
    public ReturnObject newPayment(PaymentPo paymentPo){
        try{
            int ret=paymentPoMapper.insert(paymentPo);
            if(ret==0){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new ReturnObject(paymentPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 通过outTradeNo来查找支付
     * @param outTradeNo
     * @return
     */
    public ReturnObject getPaymentByOutTradeNo(String outTradeNo){
        PaymentPoExample example = new PaymentPoExample();
        PaymentPoExample.Criteria criteria = example.createCriteria();
        try{
            if (outTradeNo!=null){
                criteria.andTradeSnEqualTo(outTradeNo);
            }
            List<PaymentPo> paymentPos = paymentPoMapper.selectByExample(example);
            if (paymentPos.isEmpty()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(paymentPos.get(0));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 通过tradeSn找到对应的refundPo
     */
    public ReturnObject getRefundByOutTradeNo(String tradeSn){
        try{
            RefundPoExample refundPoExample=new RefundPoExample();
            RefundPoExample.Criteria criteria=refundPoExample.createCriteria();
            criteria.andTradeSnEqualTo(tradeSn);
            List<RefundPo>refundPos=refundPoMapper.selectByExample(refundPoExample);
            if(refundPos.isEmpty()){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(refundPos.get(0));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }


}
