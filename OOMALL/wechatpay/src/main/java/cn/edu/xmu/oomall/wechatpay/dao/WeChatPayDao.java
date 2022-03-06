package cn.edu.xmu.oomall.wechatpay.dao;

import cn.edu.xmu.oomall.wechatpay.mapper.WeChatPayRefundPoMapper;
import cn.edu.xmu.oomall.wechatpay.mapper.WeChatPayTransactionPoMapper;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayRefund;
import cn.edu.xmu.oomall.wechatpay.model.bo.WeChatPayTransaction;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayRefundPoExample;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPo;
import cn.edu.xmu.oomall.wechatpay.model.po.WeChatPayTransactionPoExample;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnNo;
import cn.edu.xmu.oomall.wechatpay.util.WeChatPayReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author ziyi guo
 * @date 2021/12/1
 */
@Repository
public class WeChatPayDao {

    @Autowired
    private WeChatPayTransactionPoMapper weChatPayTransactionPoMapper;

    @Autowired
    private WeChatPayRefundPoMapper weChatPayRefundPoMapper;


    public WeChatPayReturnObject createTransaction(WeChatPayTransactionPo weChatPayTransactionPo){
        try{
            weChatPayTransactionPoMapper.insertSelective(weChatPayTransactionPo);
            WeChatPayTransactionPo newWeChatPayTransactionPo = weChatPayTransactionPoMapper.selectByPrimaryKey(weChatPayTransactionPo.getId());
            return new WeChatPayReturnObject(cloneVo(newWeChatPayTransactionPo, WeChatPayTransaction.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getTransactionByOutTradeNo(String outTradeNo){
        try{
            WeChatPayTransactionPoExample example = new WeChatPayTransactionPoExample();
            WeChatPayTransactionPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<WeChatPayTransactionPo> list = weChatPayTransactionPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            return new WeChatPayReturnObject(cloneVo(list.get(0), WeChatPayTransaction.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject updateTransactionByOutTradeNo(WeChatPayTransactionPo weChatPayTransactionPo){
        try{
            WeChatPayTransactionPoExample example = new WeChatPayTransactionPoExample();
            WeChatPayTransactionPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(weChatPayTransactionPo.getOutTradeNo());
            int ret = weChatPayTransactionPoMapper.updateByExampleSelective(weChatPayTransactionPo,example);
            if (ret == 0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            } else {
                return new WeChatPayReturnObject(WeChatPayReturnNo.OK);
            }
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject createRefund(WeChatPayRefundPo weChatPayRefundPo){
        try{
            weChatPayRefundPoMapper.insertSelective(weChatPayRefundPo);
            WeChatPayRefundPo newWeChatPayRefundPo = weChatPayRefundPoMapper.selectByPrimaryKey(weChatPayRefundPo.getId());
            return new WeChatPayReturnObject(cloneVo(newWeChatPayRefundPo, WeChatPayRefund.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getRefundByOutTradeNo(String outTradeNo){
        try{
            WeChatPayRefundPoExample example = new WeChatPayRefundPoExample();
            WeChatPayRefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutTradeNoEqualTo(outTradeNo);
            List<WeChatPayRefundPo> list = weChatPayRefundPoMapper.selectByExample(example);
            return new WeChatPayReturnObject(list);
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

    public WeChatPayReturnObject getRefundByOutRefundNo(String outRefundNo){
        try{
            WeChatPayRefundPoExample example = new WeChatPayRefundPoExample();
            WeChatPayRefundPoExample.Criteria criteria = example.createCriteria();
            criteria.andOutRefundNoEqualTo(outRefundNo);
            List<WeChatPayRefundPo> list = weChatPayRefundPoMapper.selectByExample(example);
            if(list.size()==0) {
                return new WeChatPayReturnObject(WeChatPayReturnNo.RESOURCE_NOT_EXISTS);
            }
            return new WeChatPayReturnObject(cloneVo(list.get(0), WeChatPayRefund.class));
        }catch (Exception e){
            return new WeChatPayReturnObject(WeChatPayReturnNo.SYSTEM_ERROR, e.getMessage());
        }
    }

}
