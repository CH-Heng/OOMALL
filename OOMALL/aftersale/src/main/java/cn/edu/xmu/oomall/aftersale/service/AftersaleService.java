package cn.edu.xmu.oomall.aftersale.service;

import cn.edu.xmu.oomall.aftersale.dao.AftersaleDao;
import cn.edu.xmu.oomall.aftersale.microservice.CustomerService;
import cn.edu.xmu.oomall.aftersale.microservice.OrderService;
import cn.edu.xmu.oomall.aftersale.microservice.PaymentService;
import cn.edu.xmu.oomall.aftersale.microservice.FreightService;
import cn.edu.xmu.oomall.aftersale.microservice.vo.*;
import cn.edu.xmu.oomall.aftersale.model.bo.Aftersale;
import cn.edu.xmu.oomall.aftersale.model.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.model.vo.*;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.privilegegateway.annotation.util.RandomCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

@Service
public class AftersaleService {
    @Autowired
    AftersaleDao aftersaleDao;

    @Autowired
    OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FreightService freightService;

    @Autowired
    private PaymentService paymentService;


    private Integer ORDER_BE_DELIVERED= 300;
    private Integer ORDER_BE_CANCELED=500;
    private Long REPAIR_FEE=2000L;
    private Byte AFTERSALE_DOCUMENT_TYPE=(byte)4;

    /**
     * 获取售后单状态
     * @author wxt
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getStates() {
        return aftersaleDao.getAftersaleState();
    }

    /**
     * 买家查看所有售后单
     * @author wxt
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getAllAftersalesByUser(Long userId,Integer state, ZonedDateTime beginTime,ZonedDateTime endTime,Integer page, Integer pageSize) {
         ReturnObject ret=aftersaleDao.getAllAftersales(userId,null,state,null,beginTime,endTime,page,pageSize);
         return ret;
    }

    /** 管理员查看所有售后单
     * @author wxt
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getAllAftersalesByAdmin(Long shopId,Integer state, Integer type,ZonedDateTime beginTime,ZonedDateTime endTime,Integer page, Integer pageSize) {
         ReturnObject ret=aftersaleDao.getAllAftersales(null,shopId,state,type,beginTime,endTime,page,pageSize);
        return ret;
    }

    /**
     * 买家根据售后单id查售后单
     * @author wxt
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getAftersalesByUserId(Long userId,Long aftersaleId){
        ReturnObject ret =aftersaleDao.getAftersalesById(aftersaleId);
        AfterSalePo afterSalePo=(AfterSalePo) ret.getData();
        if(afterSalePo==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"售后单id不存在");
        }
        if(!afterSalePo.getCustomerId().equals(userId))
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该顾客不存在此售后单");
        }
        AftersaleRetVo retVo=(AftersaleRetVo) cloneVo(afterSalePo,AftersaleRetVo.class);
        InternalReturnObject customerReturnObject =customerService.getSimpleCustomer(userId);
        if (customerReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(customerReturnObject.getErrno()), customerReturnObject.getErrmsg());
        }
        CustomerSimpleVo customerVo=(CustomerSimpleVo) customerReturnObject.getData();
        retVo.setCustomer(customerVo);
        // 获得region
        InternalReturnObject regionRetObj = freightService.getRegionById(afterSalePo.getRegionId());
        if (regionRetObj.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(regionRetObj.getErrno()), regionRetObj.getErrmsg());
        }
        SimpleRegionVo regionSimpleRetVo = (SimpleRegionVo) regionRetObj.getData();;
        retVo.setRegion(regionSimpleRetVo);
        return new ReturnObject(retVo);
    }

    /**
     * 管理员通过售后单id查售后单
     * @author wxt
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject getAftersalesByAdminId(Long shopId,Long aftersaleId){
        ReturnObject ret =aftersaleDao.getAftersalesById(aftersaleId);
        AfterSalePo afterSalePo=(AfterSalePo) ret.getData();
        if(afterSalePo==null)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"不存在该售后单");
        }
        if(shopId!=0) {
            if (!afterSalePo.getShopId().equals(shopId)) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, "该店铺不存在此售后单");
            }
        }
        FullAftersaleRetVo retVo=(FullAftersaleRetVo) cloneVo(ret.getData(),FullAftersaleRetVo.class);
        InternalReturnObject customerReturnObject =customerService.getSimpleCustomer(afterSalePo.getCustomerId());
        if (customerReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(customerReturnObject.getErrno()), customerReturnObject.getErrmsg());
        }
        CustomerSimpleVo customerVo=(CustomerSimpleVo) customerReturnObject.getData();
        retVo.setCustomer(customerVo);
        // 获得region
        InternalReturnObject regionRetObj = freightService.getRegionById(afterSalePo.getRegionId());
        if (regionRetObj.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(regionRetObj.getErrno()), regionRetObj.getErrmsg());
        }
        SimpleRegionVo regionSimpleRetVo = (SimpleRegionVo)regionRetObj.getData();
        retVo.setRegion(regionSimpleRetVo);
        return new ReturnObject(retVo);
    }

    /**
     * 获得售后单支付信息
     * @author wxt
     */
    public ReturnObject getPaymentById(Long id) {
        ReturnObject retPo=aftersaleDao.getAftersalesById(id);
        AfterSalePo po=(AfterSalePo) retPo.getData();
        String aftersaleSn=po.getServiceSn();
        InternalReturnObject ret=paymentService.getPaymentBySn(aftersaleSn);
        if (ret.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(ret.getErrno()), ret.getErrmsg());
        }
        SimplePaymentVo simplePaymentVo=(SimplePaymentVo) ret.getData();
        if(simplePaymentVo.getDocumentType()!=(byte)4)
        {
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"该支付单的交易类型不是售后");
        }
        return new ReturnObject(simplePaymentVo);
    }


    /** 买家提交售后单
     * @Param id 订单明细id
     * @author 张晖婧
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject submitAftersale(Long id, NewAftersaleVo newAftersaleVo, Long loginUser, String loginUserName) {
        InternalReturnObject internalReturnObject=orderService.getOrderItemForAftersaleByOrderItemId(id);
        //订单明细id不存在
        if(internalReturnObject.getErrno()!=ReturnNo.OK.getCode()){
            return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()),internalReturnObject.getErrmsg());
        }
        //如果订单中顾客id和申请建立售后单的顾客id不一致
        OrderItemForAftersaleVo orderItemForAftersaleVo=(OrderItemForAftersaleVo) internalReturnObject.getData();
        if(orderItemForAftersaleVo.getCustomerId()!=loginUser){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE,"无法建立不属于自己的订单的售后单");
        }
        //如果订单状态为已取消或未发货
        if(orderItemForAftersaleVo.getState()<ORDER_BE_DELIVERED||orderItemForAftersaleVo.getState()==ORDER_BE_CANCELED){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }

        Aftersale aftersale=cloneVo(newAftersaleVo,Aftersale.class);
        //填写售后单中有关订单的字段
        aftersale.setCustomerId(loginUser);
        aftersale.setOrderItemId(id);
        aftersale.setOrderId(orderItemForAftersaleVo.getOrderId());
        aftersale.setShopId(orderItemForAftersaleVo.getShopId());
        //填写售后状态
        aftersale.setState(Aftersale.State.NEW.getCode());
        //为售后单生成随机售后单序号
        aftersale.setServiceSn(randSn(loginUser,id));

        setPoCreatedFields(aftersale,loginUser,loginUserName);

        return aftersaleDao.insertAftersale(aftersale);
    }

    /** 买家修改售后单信息（店家确认售后单前）
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject updateAftersale(Long id, UpdateAftersaleVo updateAftersaleVo, Long loginUser, String loginUserName) {
        Aftersale aftersale=cloneVo(updateAftersaleVo,Aftersale.class);
        aftersale.setId(id);
        setPoModifiedFields(aftersale,loginUser,loginUserName);

        return aftersaleDao.updateAftersale(aftersale);
    }

    /** 买家取消售后单和逻辑删除售后单
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject deleteAftersale(Long id, Long loginUser, String loginUserName) {
        return aftersaleDao.deleteAftersale(id,loginUser,loginUserName);
    }

    /** 买家填写售后单的运单信息
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject fillCustomerLogSn(Long id, AftersaleLogSnVo aftersaleLogSnVo, Long loginUser, String loginUserName) {
        Aftersale aftersale=new Aftersale();
        aftersale.setId(id);
        aftersale.setCustomerLogSn(aftersaleLogSnVo.getLogSn());
        setPoModifiedFields(aftersale,loginUser,loginUserName);
        return aftersaleDao.fillCustomerLogSn(aftersale);
    }

    /** 买家确认售后单结束
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject confirmAftersaleByCustomer(Long id, Long loginUser, String loginUserName) {
        return aftersaleDao.confirmAftersaleByCustomer(id,loginUser,loginUserName);
    }

    /** 管理员同意/不同意（退款，换货，维修）
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject confirmAftersaleByAdmin(Long shopId, Long id, AdminConclusionVo adminConclusionVo, Long loginUser, String loginUserName) {
        Aftersale aftersale=cloneVo(adminConclusionVo,Aftersale.class);
        if(adminConclusionVo.getConfirm()){
            aftersale.setState(Aftersale.State.TO_BE_DELIVERED_BY_CUSTOMER.getCode());
        }
        else{
            aftersale.setState(Aftersale.State.CANCELLED.getCode());
        }
        aftersale.setShopId(shopId);
        aftersale.setId(id);
        setPoModifiedFields(aftersale,loginUser,loginUserName);
        return aftersaleDao.confirmAftersaleByAdmin(aftersale);
    }

    /** 店家验收收到买家的退（换）货
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject confirmRecieveByShop(Long shopId, Long id, ShopConclusionVo shopConclusionVo, Long loginUser, String loginUserName) {
        ReturnObject returnObject=aftersaleDao.findAftersaleInShop(shopId,id);
        if(returnObject==null){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"店铺不存在该售后单");
        }
        AfterSalePo aftersalePo=(AfterSalePo) returnObject.getData();
        //判断售后单的状态是否可以验收
        if(((int)aftersalePo.getState() != Aftersale.State.DELIVERED_BY_CUSTOMER.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }

        Aftersale aftersale=cloneVo(shopConclusionVo,Aftersale.class);
        aftersale.setId(id);
        setPoModifiedFields(aftersale,loginUser,loginUserName);
        //验收不通过
        if(!shopConclusionVo.getConfirm()){
            aftersale.setState(Aftersale.State.TO_BE_DELIVERED_BY_CUSTOMER.getCode());
        }
        else{
            //售后单类型为维修
            if(aftersalePo.getType()==Aftersale.AftersaleType.REPAIR.getCode()){
                //查询订单的支付信息
                ReturnObject ret=paymentService.getPayment(aftersalePo.getOrderId());
                if(ret.getCode()!=ReturnNo.OK){
                    return ret;
                }
                SimplePaymentVo orderPayment=(SimplePaymentVo) ret.getData();

                //新建售后支付对象
                PaymentVo paymentVo=new PaymentVo(orderPayment.getPatternId(),aftersalePo.getServiceSn(),AFTERSALE_DOCUMENT_TYPE,"维修费用",REPAIR_FEE, ZonedDateTime.now(),ZonedDateTime.now().plusHours(24));
                InternalReturnObject internalReturnObject=paymentService.setCustomerPayment(shopId,aftersalePo.getCustomerId(),paymentVo);
                if(internalReturnObject.getErrno()!=ReturnNo.OK.getCode()){
                    return new ReturnObject(ReturnNo.getByCode(internalReturnObject.getErrno()),internalReturnObject.getErrmsg());
                }
                aftersale.setState(Aftersale.State.TO_BE_PAYMENT.getCode());
                //暂时不写入价格
                //aftersale.setPrice(REPAIR_FEE);

            }//售后单类型为退货或换货
            else {
                //查询订单明细
                InternalReturnObject internalReturnObject = orderService.getOrderItemForAftersaleByOrderItemId(id);
                OrderItemForAftersaleVo orderItemForAftersaleVo = (OrderItemForAftersaleVo) internalReturnObject.getData();
                //退货
                if (aftersalePo.getType() == Aftersale.AftersaleType.RETURN.getCode()) {
                    //计算退款金额和积点数量
                    Long actualPaymentPrice = orderItemForAftersaleVo.getPrice() - orderItemForAftersaleVo.getDiscountPrice();
                    Integer sumQuantity = orderItemForAftersaleVo.getQuantity();
                    Long sumPoint = orderItemForAftersaleVo.getPoint();
                    Long price = aftersalePo.getQuantity() / sumQuantity * actualPaymentPrice;
                    Long point = aftersalePo.getQuantity() / sumQuantity * sumPoint;

                    AftersaleRefundVo aftersaleRefundVo = new AftersaleRefundVo(aftersalePo.getServiceSn(), aftersalePo.getOrderItemId(), price, point);
                    //调用退款接口
                    InternalReturnObject refundResult = paymentService.refundForAftersale(shopId, aftersalePo.getCustomerId(), aftersaleRefundVo);
                    if (refundResult.getErrno() != ReturnNo.OK.getCode()) {//退款失败
                        aftersale.setState(Aftersale.State.TO_BE_REFUNDED.getCode());
                    } else {
                        aftersale.setPrice(-price);     //金额为负表示退款
                        aftersale.setState(Aftersale.State.END.getCode());
                    }
                }//换货
                else if (aftersalePo.getType() == Aftersale.AftersaleType.EXCHANGE.getCode()) {
                    OrderItemForExchangeVo orderItemForExchangeVo=new OrderItemForExchangeVo(orderItemForAftersaleVo.getProductId(),orderItemForAftersaleVo.getName(),aftersalePo.getQuantity());
                    OrderInfoForExchangeVo orderInfoForExchangeVo=cloneVo(aftersalePo,OrderInfoForExchangeVo.class);
                    orderInfoForExchangeVo.setAddress(aftersalePo.getDetail());
                    orderInfoForExchangeVo.setOrderItems(orderItemForExchangeVo);
                    orderInfoForExchangeVo.setMessage("换货订单");

                    InternalReturnObject ret=orderService.submitExchangeOrderForAftersale(shopId,orderInfoForExchangeVo);
                    if(ret.getErrno()!=ReturnNo.OK.getCode()){
                        return new ReturnObject(ReturnNo.getByCode(ret.getErrno()),ret.getErrmsg());
                    }
                    aftersale.setState(Aftersale.State.TO_BE_DELIVERED_BY_SHOPKEEPER.getCode());
                    aftersale.setPrice(0L);
                }
            }
        }
        return aftersaleDao.confirmRecieveByShop(aftersale);
    }

    /** 店家寄出货物
     * @Param shopId 店铺id
     * @Param id 售后单id
     * @author 张晖婧
     */
    public ReturnObject deliverAgain(Long shopId, Long id, AftersaleShopLogSnVo aftersaleShopLogSnVo, Long loginUser, String loginUserName) {
        Aftersale aftersale=cloneVo(aftersaleShopLogSnVo,Aftersale.class);
        aftersale.setShopId(shopId);
        aftersale.setId(id);
        setPoModifiedFields(aftersale,loginUser,loginUserName);
        return aftersaleDao.deliverAgain(aftersale);
    }

    /**
     * 生成售后单随机编号
     * @param customerId
     * @param orderItemId
     * @return
     */
    private String randSn(Long customerId, Long orderItemId) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%010d%010d", customerId, orderItemId));
        builder.append(RandomCaptcha.getRandomString(10));
        return builder.toString();
    }

    public ReturnObject getAftersalesBySn(String sn) {
      ReturnObject returnObject=aftersaleDao.findAftersaleByServiceSn(sn);
      AfterSalePo afterSalePo=(AfterSalePo) returnObject.getData();
      AftersaleRetVo aftersaleRetVo=cloneVo(afterSalePo,AftersaleRetVo.class);
        InternalReturnObject customerReturnObject =customerService.getSimpleCustomer(afterSalePo.getCustomerId());
        if (customerReturnObject.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(customerReturnObject.getErrno()), customerReturnObject.getErrmsg());
        }
        CustomerSimpleVo customerVo=(CustomerSimpleVo) customerReturnObject.getData();
        aftersaleRetVo.setCustomer(customerVo);
        // 获得region
        InternalReturnObject regionRetObj = freightService.getRegionById(afterSalePo.getRegionId());
        if (regionRetObj.getData() == null) {
            return new ReturnObject(ReturnNo.getByCode(regionRetObj.getErrno()), regionRetObj.getErrmsg());
        }
        SimpleRegionVo regionSimpleRetVo = (SimpleRegionVo)regionRetObj.getData();
        aftersaleRetVo.setRegion(regionSimpleRetVo);
      return new ReturnObject<>(aftersaleRetVo);
    }
}
