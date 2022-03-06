package cn.edu.xmu.oomall.order.service;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.dao.OrderDao;
import cn.edu.xmu.oomall.order.dao.OrderItemDao;
import cn.edu.xmu.oomall.order.microservice.*;
import cn.edu.xmu.oomall.order.microservice.vo.*;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderInfo;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.po.OrderPoExample;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


import java.util.stream.Collectors;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@Service
public class OrderService {
    @Autowired
    OrderDao orderDao;

    @Autowired
    OrderItemDao orderItemDao;

    @Resource
    ActivityService activityService;

    @Resource
    CouponService couponService;

    @Resource
    GoodsService goodsService;

    @Resource
    FreightService freightService;

    @Resource
    CustomerService customerService;

    @Autowired
    PaymentService paymentService;

    @Resource
    ShopService shopService;

    @Autowired
    RocketMqService rocketMqService;

    private final static String ONSALE_ID="o_%d";

    /**
     * 查看订单模块的所有活动
     * @return List<StateRetVo>list
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ReturnObject<List<Map<String, Object>>> showAllState(){
        return orderDao.showAllState();
    }

    /**
     * 买家查询名下订单
     * @param orderSn 订单编号
     * @param state 状态
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页
     * @param pageSize 页大小
     * @param userId 用户Id
     * @param userName 用户姓名
     * @return 订单详情
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject<PageInfo<VoObject>> showCustomerOwnOrderInfo(String orderSn, Integer state, ZonedDateTime beginTime, ZonedDateTime endTime, Integer page, Integer pageSize, Long userId, String userName){
        OrderPoExample example = new OrderPoExample();
        OrderPoExample.Criteria criteria = example.createCriteria();
        if(!"".equals(orderSn)&&orderSn!=null){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(state!=null){
            criteria.andStateBetween(state-4,state+90);
        }
        if(userId!=null){
            criteria.andCreatorIdEqualTo(userId);
        }
        if(beginTime!=null&&endTime!=null){
            criteria.andGmtCreateBetween(beginTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),endTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        }
        //被删除订单不返回
        criteria.andBeDeletedEqualTo((byte) 0);
        example.setOrderByClause("gmt_create DESC");
        return orderDao.showCustomerOwnOrderByField(example,page,pageSize);

    }

    /**
     * 用于判断各种id是否存在
     * @param order 订单
     */
    public Order isExistAboutOrder(Order order){
        if(order.getAdvancesaleId()!=null){
            InternalReturnObject internalReturnObject1 = activityService.isAdvanceSaleExist(order.getAdvancesaleId());
            if(!(Boolean) internalReturnObject1.getData()){
                order.setAdvancesaleId(0L);
            }
        }else{
            order.setAdvancesaleId(0L);
        }
        if(order.getGrouponId()!=null){
            InternalReturnObject internalReturnObject2 = activityService.isGrouponExist(order.getGrouponId());
            if(!(Boolean) internalReturnObject2.getData()){
                order.setGrouponId(0L);
            }
        }else{
            order.setGrouponId(0L);
        }
        if(order.getRegionId()!=null){
            InternalReturnObject internalReturnObject = freightService.isRegionExist(order.getRegionId());
            if(!(Boolean) internalReturnObject.getData()){
                order.setRegionId(0L);
            }
        }
        return order;
    }

    /**
     * 判断orderItemVo内的各种id是否存在
     * @param orderItemVo
     */
    public OrderItemVo isExistAboutOrderItem(OrderItemVo orderItemVo){
        if(orderItemVo.getProductId()!=null){
            InternalReturnObject internalReturnObject5 = goodsService.isProductExist(orderItemVo.getProductId());
            if(!(Boolean) internalReturnObject5.getData()){
                orderItemVo.setProductId(0L);
            }
        }
        if(orderItemVo.getOnsaleId()!=null){
            InternalReturnObject internalReturnObject6 = goodsService.isOnsaleExist(orderItemVo.getOnsaleId());
            if(!(Boolean) internalReturnObject6.getData()){
                orderItemVo.setOnsaleId(0L);
            }
        }
        if(orderItemVo.getCouponActivityId()==null){
            orderItemVo.setCouponActivityId(0L);
        }
        if(orderItemVo.getCouponId()==null){
            orderItemVo.setCouponId(0L);
        }else{
            customerService.useCoupon(orderItemVo.getCouponId());
        }
        return orderItemVo;
    }

    /**
     * 完善orderInfo
     * 新建订单时没有子订单不用修改子订单状态
     * @param orderItemRetVos
     * @param userId
     * @param userName
     * @return
     */
    public ReturnObject orderInfoCompletion(Order order, List<OrderItemVo>orderItemRetVos, Long userId, String userName,ZonedDateTime beginTime) {
        //设置订单字段的初始值
        order.setCustomerId(userId);
        order.setPid(0L);
        order.setState(Order.State.NEW.getCode());
        order.setBeDeleted((byte)0);
        Common.setPoCreatedFields(order,userId,userName);
        List<OrderItem> orderItems = new ArrayList<>();
        //判断是否是同一个店铺
        //填充Onsale表中的内容
        for (OrderItemVo orderItemVo : orderItemRetVos) {
            OrderItem orderItem = Common.cloneVo(orderItemVo, OrderItem.class);
            //couponActivityId没有clone到
            orderItem.setCouponActivityId(orderItemVo.getCouponActivityId());
            orderItemVo = isExistAboutOrderItem(orderItemVo);
            if (orderItemVo.getOnsaleId() != null) {
                OnSaleRetVo onSaleRetVo;
                InternalReturnObject<OnSaleRetVo> internalReturnObject = goodsService.selectFullOnsale(orderItemVo.getOnsaleId());
                onSaleRetVo = internalReturnObject.getData();
                if (onSaleRetVo.getQuantity() != null && onSaleRetVo.getQuantity() < orderItemVo.getQuantity()) {
                    return new ReturnObject(ReturnNo.GOODS_STOCK_SHORTAGE);
                }
                Long shopId = onSaleRetVo.getShop() == null ? null : onSaleRetVo.getShop().getId();
                orderItem.setShopId(shopId);
                orderItem.setPrice(onSaleRetVo.getPrice());
                orderItem.setName(onSaleRetVo.getProduct() == null ? null : onSaleRetVo.getProduct().getName());
                orderItem.setCommented((byte) 0);
                Common.setPoCreatedFields(orderItem,userId,userName);
                orderItems.add(orderItem);
                //去库存
                goodsService.decreaseOnSale(orderItem.getShopId(),orderItem.getOnsaleId(),new QuantityVo(orderItem.getQuantity().intValue()));
            }
        }
        Boolean isNeedSeperate = false;
        Long shopIdOrigin = orderItems.get(0).getShopId();
        for(OrderItem orderItem:orderItems){
            if(shopIdOrigin!=orderItem.getShopId()){
                isNeedSeperate = true;
                break;
            }
        }
        if(!isNeedSeperate){
            order.setShopId(shopIdOrigin);
        }
        //设置订单的运费
        ReturnObject returnObject = orderDao.findFeightPrice(order.getRegionId(),orderItems);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        order.setExpressFee((Long)returnObject.getData());
        //预售订单
        if(order.getAdvancesaleId()!=0){
            AdvanceSaleRetVo advanceSaleRetVo = activityService.queryOnlineAdvanceSaleInfo(order.getAdvancesaleId()).getData();
            //预售活动父订单：子订单：订单明细 = 1：1:1，不考虑优惠活动等等
            OrderItem orderItem = orderItems.get(0);
            //预售款
            Long deposit = advanceSaleRetVo.getAdvancePayPrice()*orderItem.getQuantity();
            //尾款
            Long balance = (advanceSaleRetVo.getPrice()-advanceSaleRetVo.getAdvancePayPrice())*orderItem.getQuantity();
            //运费加载预售款中
            //积点放订金，运费放尾款
            OrderPaymentRetVo orderPaymentRetVo1 = new OrderPaymentRetVo(order.getOrderSn(),(byte)2,null,deposit- order.getPoint(),beginTime,beginTime.plusMinutes(30));
            OrderPaymentRetVo orderPaymentRetVo2 = new OrderPaymentRetVo(order.getOrderSn(),(byte)3,null,balance+order.getExpressFee(),beginTime,beginTime.plusMinutes(30));
            List<OrderPaymentRetVo>orderPaymentRetVos = Arrays.asList(orderPaymentRetVo1,orderPaymentRetVo2);
            orderItem.setPoint(order.getPoint());
            //支付尾款的时候还需要更新返点
            List<OrderItem>orderItemList = Arrays.asList(orderItem);
            OrderInfo orderInfo = new OrderInfo(order,orderItemList,orderPaymentRetVos);
            return new ReturnObject(orderInfo);
        }
        //团购订单
        if(order.getGrouponId()!=0){
            //团购： 父订单：子订单：订单明细 = 1：1:1，不考虑优惠活动等等
            //团购不支持使用返点
            OrderItem orderItem = orderItems.get(0);
            Long payPrice = orderItem.getPrice() * orderItem.getQuantity();
            OrderPaymentRetVo orderPaymentRetVo = new OrderPaymentRetVo(order.getOrderSn(),(byte)0,null,payPrice+order.getExpressFee(),beginTime,beginTime.plusMinutes(30));
            List<OrderPaymentRetVo>orderPaymentRetVos = Arrays.asList(orderPaymentRetVo);
            OrderInfo orderInfo = new OrderInfo(order,orderItems,orderPaymentRetVos);
            return new ReturnObject(orderInfo);
        }

        //普通订单
        //计算优惠价格
        List<DiscountItemVo>discountItemVos = new ArrayList<>();
        for(OrderItem orderItem:orderItems){
            DiscountItemVo discountItemVo = new DiscountItemVo(orderItem.getProductId(),orderItem.getOnsaleId(),orderItem.getQuantity(),orderItem.getPrice(),orderItem.getCouponActivityId());
            discountItemVos.add(discountItemVo);
        }
        InternalReturnObject<List<DiscountRetVo>> internalReturnObject = couponService.calculateDiscount(discountItemVos);
        List<DiscountRetVo>discountRetVos;
        if(internalReturnObject==null){
            discountRetVos = new ArrayList<>();
        }else{
            discountRetVos = internalReturnObject.getData();
        }
        //根据订单明细和productId一对一的关系，重组orderItem(orderItem中有其他信息)
        for(int i=0;i<discountRetVos.size();i++){
            for(int j=0;j<orderItems.size();j++){
                if(discountRetVos.get(i).getProductId().equals(orderItems.get(j).getProductId())){
                    OrderItem orderItem = orderItems.get(j);
                    orderItem.setDiscountPrice(discountRetVos.get(i).getDiscountPrice());
                    orderItems.set(j,orderItem);
                }
            }
        }
        for(OrderItem orderItem:orderItems){
            Long newDiscountPrice = orderItem.getPrice()*10-orderItem.getDiscountPrice();
            orderItem.setDiscountPrice(newDiscountPrice);
        }
        // 设置订单的初始价格和折扣价格
        Long originPrice = 0L;
        Long discontPrice = 0L;
        for(OrderItem orderItem:orderItems){
            originPrice += orderItem.getPrice()*orderItem.getQuantity();
            discontPrice += orderItem.getDiscountPrice()*orderItem.getQuantity();
        }
        //订单的折扣价格单位是分
        //订单明细的折扣价格单位是十分之一分
        order.setOriginPrice(originPrice);
        order.setDiscountPrice(discontPrice/10);
        if(order.getPoint()<=order.getExpressFee()){
            //分摊积点
            for(OrderItem orderItem:orderItems){
                orderItem.setPoint(0L);
            }
        }else{
            //分摊积点
            for(OrderItem orderItem:orderItems){
                orderItem.setPoint((order.getPoint()-order.getExpressFee())*(Math.round(orderItem.getPrice()-orderItem.getDiscountPrice()*1.0/10))/(order.getOriginPrice()-order.getDiscountPrice()));
            }
        }

        //订单支付费用
        Long payPrice = order.getOriginPrice()-order.getDiscountPrice() + order.getExpressFee() - order.getPoint();
        OrderPaymentRetVo orderPaymentRetVo = new OrderPaymentRetVo(order.getOrderSn(),(byte)0,null,payPrice,beginTime,beginTime.plusMinutes(30));
        List<OrderPaymentRetVo>orderPaymentRetVos = Arrays.asList(orderPaymentRetVo);
        OrderInfo orderInfo = new OrderInfo(order,orderItems,orderPaymentRetVos);
        return new ReturnObject(orderInfo);
    }

    /**
     * 买家申请建立订单（普通，团购，预售）
     * 新建订单时没有子订单不用修改子订单状态
     * @param userId 用户Id
     * @param userName 用户姓名
     * @param orderInfoVo 指定新订单的资料
     * @return 新建结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject customerPostNewNormalOrder(Long userId, String userName, OrderInfoVo orderInfoVo,ZonedDateTime beginTime){
        CustomerPointRetVo customerPointRetVo = customerService.getPointByUserId(userId).getData();
        if(orderInfoVo.getPoint()>customerPointRetVo.getPoint()){
            orderInfoVo.setPoint(orderInfoVo.getPoint());
        }
        customerService.modifyCustomerPoint(userId,new CustomerPointVo(-orderInfoVo.getPoint()));
        Order order = Common.cloneVo(orderInfoVo,Order.class);
        Common.setPoCreatedFields(order,userId,userName);
        order = isExistAboutOrder(order);
        List<OrderItemVo>orderItemVos = orderInfoVo.getOrderItems();
        ReturnObject returnObject = orderInfoCompletion(order,orderItemVos,userId,userName,beginTime);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        OrderInfo orderInfo = (OrderInfo) returnObject.getData();
        rocketMqService.sendPostOrderMessage(orderInfo);
        ReturnObject<List> returnObject1 = new ReturnObject<>(orderInfo.getOrderPaymentRetVos());
        return cn.edu.xmu.oomall.core.util.Common.getListRetVo(returnObject1,OrderPaymentRetVo.class);
    }

    /**
     * 买家查询订单完整信息（普通，团购，预售）
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return 查询结果
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject showOwnOrderInfoById(Long id,Long userId,String userName){
        ReturnObject<Order>returnObject1 = orderDao.showOrderById(id,userId);
        if(returnObject1.getCode()!= ReturnNo.OK){
            return returnObject1;
        }
        Order order = returnObject1.getData();
        //搜索父订单的
        ReturnObject<List<OrderItem>>returnObject2;
        if(order.getPid()==0){
            returnObject2 = orderItemDao.showOwnOrderItemByOrderId(id);
        }else{
            returnObject2 = orderItemDao.showOwnOrderItemByOrderId(order.getPid());
        }
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        List<OrderItem>list = returnObject2.getData();
        OrderInfoRetVo orderInfoRetVo = Common.cloneVo(order,OrderInfoRetVo.class);
        InternalReturnObject<CustomerSimpleRetVo> returnObject3 = customerService.getSimpleCustomer(order.getCustomerId());
        orderInfoRetVo.setCustomer(returnObject3.getData());
        if(order.getShopId()!=null){
            InternalReturnObject<SimpleObjectRetVo> returnObject4 = shopService.getShopById(order.getShopId());
            orderInfoRetVo.setShop(returnObject4.getData());
        }
        List<OrderItemRetVo>list1 = new ArrayList<>();
        for(OrderItem orderItem:list){
            if(order.getShopId()!=null) {
                if(Objects.equals(orderItem.getShopId(), order.getShopId())){
                    list1.add(Common.cloneVo(orderItem, OrderItemRetVo.class));
                }
            }else{
                list1.add(Common.cloneVo(orderItem, OrderItemRetVo.class));
            }
        }
        orderInfoRetVo.setOrderItem(list1);
        return new ReturnObject<>(orderInfoRetVo);
    }

    /**
     * 买家修改本人名下订单
     * 此API下修改订单，其他相关订单也要修改
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param orderUpdateFieldVo 订单修改信息
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> updateOwnOrderUsingField(Long id, Long userId, String userName, OrderUpdateFieldVo orderUpdateFieldVo){
        Order order = Common.cloneVo(orderUpdateFieldVo,Order.class);
        Common.setPoModifiedFields(order,userId,userName);
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order1 = (Order) returnObject.getData();
        if(order1.getState()>=Order.State.DELIVERED.getCode()){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        ReturnObject returnObject1 = orderItemDao.showOwnOrderItemByOrderId(order1.getId());
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject1;
        }
        List<OrderItem>orderItems = (List<OrderItem>) returnObject1.getData();
        ReturnObject returnObject2 = orderDao.findFeightPrice(order1.getRegionId(),orderItems);
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        Long freightPrice = (Long) returnObject2.getData();
        if(!freightPrice.equals(order1.getExpressFee())){
            return new ReturnObject<>(ReturnNo.ORDER_CHANGENOTALLOW);
        }
        if(order1.getPid()==0){  //如果是父订单
            return orderDao.updateOwnOrderUsingField(id,order);
        }else{  //如果是子订单
            return orderDao.updateOwnOrderUsingField(order1.getPid(),order);
        }
    }

    /**
     * 买家逻辑删除本人名下订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> deleteOwnOrderById(Long id, Long userId, String userName){
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order1 = (Order) returnObject.getData();
        //完成态才能删除
        if(order1.getState()<Order.State.COMPLETED.getCode().intValue()){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        if(order1.getPid()==0){  //如果是父订单
            return orderDao.deleteOwnOrderById(id,userId,userName);
        }else{  //如果是子订单
            return orderDao.deleteOwnOrderById(order1.getPid(),userId,userName);
        }
    }

    /**
     * 买家取消本人名下订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 取消结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> cancelOrderById(Long id, Long userId, String userName){
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order = (Order) returnObject.getData();
        if(order.getPid()!=0){  //如果不是父订单需要转换为父订单进行操作
            returnObject = orderDao.showOrderById(order.getPid(),userId);
            if(returnObject.getCode()!=ReturnNo.OK){
                return returnObject;
            }
            order = (Order)returnObject.getData();
        }
        if(order.getState()>Order.State.DELIVERED.getCode()){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        //退返点
        customerService.modifyCustomerPoint(userId,new CustomerPointVo(order.getPoint()));
        //进行退款
        RefundVo refundVo = new RefundVo(order.getOrderSn(),(byte)0,null);
        paymentService.refund(refundVo);
        ReturnObject returnObject1 = orderItemDao.showOwnOrderItemByOrderId(order.getId());
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject1;
        }
        List<OrderItem>orderItems = (List<OrderItem>) returnObject1.getData();
        for(OrderItem orderItem:orderItems){
            //增加库存
            goodsService.increaseOnSale(orderItem.getShopId(),orderItem.getOnsaleId(),new QuantityVo(orderItem.getQuantity().intValue()));
            if(orderItem.getCouponId()!=null){
                //恢复优惠券
                customerService.renewCoupon(orderItem.getCouponId());
            }
        }
        return orderDao.cancelOwnOrderById(order.getId(),userId,userName);


    }

    /**
     * 买家标记确认收获
     * 需同步父子订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> confirmOrderById(Long id, Long userId, String userName){
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order = (Order) returnObject.getData();
        //判断是否处于已发货状态
        if(!order.getState().equals(Order.State.DELIVERED.getCode())){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        if(order.getPid()==0){  //父订单
            return orderDao.confirmOwnOrderById(id,userId,userName);
        }else{  //子订单
            return orderDao.confirmOwnOrderById(order.getPid(),userId,userName);
        }
    }


    /**
     * 店家查询商户所有订单 (概要)
     * @param shopId 店铺id
     * @param customerId 顾客id
     * @param orderSn 订单编号
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param page 页
     * @param pageSize 页大小
     * @param userId 用户Id
     * @param userName 用户姓名
     * @return 订单详情
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject<PageInfo<VoObject>> showShopOwnOrderInfo(Long shopId,Long customerId,String orderSn, ZonedDateTime beginTime, ZonedDateTime endTime, Integer page, Integer pageSize, Long userId, String userName){
        OrderPoExample example = new OrderPoExample();
        OrderPoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null){
            criteria.andShopIdEqualTo(shopId);
        }
        if(customerId!=null){
            criteria.andCustomerIdEqualTo(customerId);
        }
        if(!"".equals(orderSn)&&orderSn!=null){
            criteria.andOrderSnEqualTo(orderSn);
        }
        if(userId!=null){
            criteria.andCreatorIdEqualTo(userId);
        }
        if(beginTime!=null&&endTime!=null){
            criteria.andGmtCreateBetween(beginTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(),endTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
        }

        example.setOrderByClause("gmt_create DESC");
        return orderDao.showCustomerOwnOrderByField(example,page,pageSize);

    }

    /**
     * 店家修改订单 (留言)
     * 不用同步父子订单
     * @param id 订单id
     * @param shopId 店铺id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param message 消息
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> updateOrderMessageById(Long id,Long shopId, Long userId, String userName,String message){
        return orderDao.updateOwnOrderMessageById(id,shopId,userId,userName,message);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject showShopOwnOrderInfoById(Long id,Long shopId,Long userId,String userName){
        ReturnObject<Order>returnObject1 = orderDao.showOrderById(id,userId);
        ReturnObject<List<OrderItem>>returnObject2 = orderItemDao.showOwnOrderItemByOrderId(id);
        if(returnObject1.getCode()!= ReturnNo.OK){
            return returnObject1;
        }
        Order order = returnObject1.getData();
        if(order.getShopId()!=null&&!shopId.equals(order.getShopId())){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        List<OrderItem>list = returnObject2.getData();
        OrderInfoRetVo orderInfoRetVo = Common.cloneVo(order,OrderInfoRetVo.class);
        InternalReturnObject<CustomerSimpleRetVo> returnObject3 = customerService.getSimpleCustomer(order.getCustomerId());
        orderInfoRetVo.setCustomer(returnObject3.getData());
        if(order.getShopId()!=null){
            InternalReturnObject<SimpleObjectRetVo> returnObject4 = shopService.getShopById(order.getShopId());
            orderInfoRetVo.setShop(returnObject4.getData());
        }
        List<OrderItemRetVo>list1 = new ArrayList<>();
        for(OrderItem orderItem:list){
            list1.add(Common.cloneVo(orderItem,OrderItemRetVo.class));
        }
        orderInfoRetVo.setOrderItem(list1);
        return new ReturnObject<>(orderInfoRetVo);
    }

    /**
     * 管理员取消本店铺订单。
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param shopId 店铺id
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> deleteShopOrderById(Long id, Long shopId, Long userId, String userName){
        return orderDao.deleteShopOrderById(id,shopId,userId,userName);
    }

    /**
     *
     * 店家对订单标记发货
     * @param id 订单id
     * @param shopId 店铺id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param freightSn 指定发货咨询
     * @return 修改结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject<Order> postFreights(Long id,Long shopId, Long userId, String userName,String freightSn){
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order = (Order) returnObject.getData();
        if(!order.getState().equals(Order.State.TO_BE_RECEIVED.getCode())
                && !order.getState().equals(Order.State.PAID.getCode())
                && !order.getState().equals(Order.State.GROUPON_THRESHOLD_TO_BE_REACH.getCode())){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        if(order.getPid()==0){
            return orderDao.postFreights(id,userId,userName,freightSn);
        }else{  //如果分单，操作的是父订单,所以不是父订单返回没有资源id存在
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }

    }

    /**
     * 查询自己订单的支付信息
     * @param id 订单id
     * @param userId 用户id
     * @param userName 用户名
     * @return 支付信息
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject showOwnPayment(Long id,Long userId,String userName){
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order = (Order)returnObject.getData();
        InternalReturnObject internalReturnObject = paymentService.getPaymentsByOrderSn(order.getOrderSn());
        List<PaymentSimpleRetVo>list = (List<PaymentSimpleRetVo>) internalReturnObject.getData();
        ReturnObject<List>returnObject1 = new ReturnObject<>(list);
        return cn.edu.xmu.oomall.core.util.Common.getListRetVo(returnObject1,PaymentSimpleRetVo.class);
    }

    /**
     * 查询自己订单的退款信息
     * @param id 订单id
     * @param userId 用户id
     * @param userName 用户名
     * @return 支付信息
     */
    @Transactional(readOnly = true,rollbackFor = Exception.class)
    public ReturnObject showOwnRefund(Long id,Long userId,String userName){
        ReturnObject returnObject = orderDao.showOrderById(id,userId);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        Order order = (Order)returnObject.getData();
        InternalReturnObject internalReturnObject = paymentService.getRefundsByOrderSn(order.getOrderSn());
        List<RefundSimpleRetVo>list = (List<RefundSimpleRetVo>) internalReturnObject.getData();
        ReturnObject<List>returnObject1 = new ReturnObject<>(list);
        return cn.edu.xmu.oomall.core.util.Common.getListRetVo(returnObject1,RefundSimpleRetVo.class);
    }

    /**
     * 确认团购订单
     * @param id 团购订单Id
     * @param shopId 店铺id
     * @param userId 用户id
     * @param userName 用户姓名
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject confirmGrouponOrder(Long id, Long shopId,Long userId, String userName){
        //先获得订单
        ReturnObject<Order>returnObject1 = orderDao.showOrderById(id,userId);
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject1;
        }
        Order order = returnObject1.getData();
        if(order.getPid()!=0){  //如果不是父订单需要转换为父订单进行操作
            returnObject1 = orderDao.showOrderById(order.getPid(),userId);
            if(returnObject1.getCode()!=ReturnNo.OK){
                return returnObject1;
            }
            order = returnObject1.getData();
        }
        if(!shopId.equals(order.getShopId())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if(!order.getState().equals(Order.State.GROUPON_THRESHOLD_TO_BE_REACH.getCode())){
            return new ReturnObject(ReturnNo.STATENOTALLOW);
        }
        if(order.getGrouponId()==null){
            return new ReturnObject(ReturnNo.FIELD_NOTVALID);
        }
        //计算参与团购的所有数量
        ReturnObject returnObject = orderDao.showOrdersByGrouponId(order.getGrouponId());
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        List<Order>orders = (List<Order>)returnObject.getData();
        List<OrderItem>orderItems = new ArrayList<>();
        for(Order order1:orders){
            ReturnObject returnObject2 = orderItemDao.showOwnOrderItemByOrderId(order1.getId());
            if(returnObject2.getCode()!=ReturnNo.OK){
                return returnObject2;
            }
            orderItems.addAll((List<OrderItem>)returnObject2.getData());
        }
        Long quantity = 0L;
        Integer percentage = 0;
        for(OrderItem orderItem:orderItems){
            quantity += orderItem.getQuantity();
        }
        GroupOnActivityVo groupOnActivityVo = activityService.getOnlineGroupOnActivity(order.getGrouponId()).getData();
        //判断达到什么等级的quantity
        List<GroupOnStrategyVo>groupOnStrategyVos = groupOnActivityVo.getStrategy();
        //自然序
        List<GroupOnStrategyVo> groupOnStrategyVos1 = groupOnStrategyVos.stream().sorted(Comparator.comparing(GroupOnStrategyVo::getQuantity)).collect(Collectors.toList());
        for(int i=0;i<groupOnStrategyVos1.size()-1;i++){
            if(quantity>groupOnStrategyVos1.get(i).getQuantity()&&quantity<groupOnStrategyVos1.get(i+1).getQuantity()){
                percentage = groupOnStrategyVos1.get(i).getPercentage();
            }
        }
        // 获得当前订单的价格
        ReturnObject returnObject4 = orderItemDao.showOwnOrderItemByOrderId(id);
        if(returnObject4.getCode()!=ReturnNo.OK){
            return returnObject4;
        }
        List<OrderItem>orderItemList = (List<OrderItem>)returnObject4.getData();
        OrderItem orderItem = orderItemList.get(0);
        Long payPrice = orderItem.getPrice() * orderItem.getQuantity();
        Long refundPrice = payPrice * (1-percentage/1000);
        //进行退款
        RefundVo refundVo = new RefundVo(order.getOrderSn(),(byte)0,null);
        paymentService.refund(refundVo);
        //修改订单状态
        ReturnObject returnObject2 = orderItemDao.showOwnOrderItemByOrderId(order.getId());
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        order.setState(Order.State.PAID.getCode());
        Common.setPoModifiedFields(order,userId,userName);
        ReturnObject returnObject3 = orderDao.updateOrder(order);
        if(returnObject3.getCode()!= ReturnNo.OK){
            return returnObject3;
        }
        ReturnObject returnObject5 = orderDao.updateSubOrderIdStateByOrder(id,Order.State.PAID.getCode());
        if(returnObject5.getCode()!=ReturnNo.OK){
            return returnObject5;
        }
        return new ReturnObject(ReturnNo.OK);
    }

    /**
     * 内部API-取消订单
     * @param id 团购订单Id
     * @param shopId 店铺id
     * @param userId 用户id
     * @param userName 用户姓名
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ReturnObject cancelOrder(Long id, Long shopId,Long userId, String userName){
        //先获得订单
        ReturnObject<Order>returnObject1 = orderDao.showOrderById(id,userId);
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject1;
        }
        Order order = returnObject1.getData();
        if(order.getPid()!=0){  //如果不是父订单需要转换为父订单进行操作
            returnObject1 = orderDao.showOrderById(order.getPid(),userId);
            if(returnObject1.getCode()!=ReturnNo.OK){
                return returnObject1;
            }
            order = returnObject1.getData();
        }
        if(!shopId.equals(order.getShopId())){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        if(order.getState()>Order.State.DELIVERED.getCode()){
            return new ReturnObject<>(ReturnNo.STATENOTALLOW);
        }
        //进行退款
        RefundVo refundVo = new RefundVo(order.getOrderSn(),(byte)0,null);
        paymentService.refund(refundVo);
        ReturnObject returnObject2 = orderItemDao.showOwnOrderItemByOrderId(order.getId());
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        //增加库存
        List<OrderItem>orderItems = (List<OrderItem>) returnObject2.getData();
        for(OrderItem orderItem:orderItems){
            goodsService.increaseOnSale(orderItem.getShopId(),orderItem.getOnsaleId(),new QuantityVo(orderItem.getQuantity().intValue()));
        }
        return orderDao.cancelOwnOrderById(id,userId,userName);
    }

    /**
     * 内部API-管理员建立售后订单
     * @param shopId 店铺Id
     * @param userId 用户id
     * @param userName 用户名
     * @param orderInfo 订单信息
     * @return 结果
     */
    public ReturnObject adminPostAfterSaleOrder(Long shopId,Long userId,String userName,AfterSaleOrderInfo orderInfo){
        Order order = Common.cloneVo(orderInfo,Order.class);
        // TODO: 2021/12/18 用哪个getSeqNum
        order.setOrderSn(Common.genSeqNum(1));
        //设置订单字段的初始值
        order.setCustomerId(userId);
        order.setShopId(shopId);
        order.setPid(0L);
        //此类订单是用于售后换货, 新建状态是待发货
        order.setState(Order.State.TO_BE_RECEIVED.getCode());
        order.setBeDeleted((byte)0);
        List<OrderItem> orderItems = new ArrayList<>();
        //填充Onsale表中的内容
        for (OrderItemSimpleVo orderItemVo : orderInfo.getOrderItems()) {
            OrderItem orderItem = Common.cloneVo(orderItemVo, OrderItem.class);
            if (orderItemVo.getOnsaleId() != null) {
                OnSaleRetVo onSaleRetVo;
                InternalReturnObject<OnSaleRetVo> internalReturnObject = goodsService.selectFullOnsale(orderItemVo.getOnsaleId());
                onSaleRetVo = internalReturnObject.getData();
                if (onSaleRetVo.getQuantity() != null && onSaleRetVo.getQuantity() < orderItemVo.getQuantity()) {
                    return new ReturnObject(ReturnNo.GOODS_STOCK_SHORTAGE);
                }
                orderItem.setShopId(onSaleRetVo.getShop() == null ? null : onSaleRetVo.getId());
                //订单金额为0
                orderItem.setPrice(0L);
                orderItem.setName(onSaleRetVo.getProduct() == null ? null : onSaleRetVo.getProduct().getName());
                orderItem.setCommented((byte) 0);
                orderItems.add(orderItem);
                //去库存
                goodsService.decreaseOnSale(orderItem.getShopId(),orderItem.getOnsaleId(),new QuantityVo(orderItem.getQuantity().intValue()));
            }
        }
        //设置订单的运费
        ReturnObject returnObject = orderDao.findFeightPrice(order.getRegionId(),orderItems);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        order.setExpressFee((Long)returnObject.getData());
        //订单金额为0
        order.setOriginPrice(0L);
        order.setDiscountPrice(0L);
        Common.setPoCreatedFields(order,userId,userName);
        ReturnObject returnObject1 = orderDao.addOrder(order);
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject1;
        }
        Order order1 = (Order)returnObject1.getData();
        Order order2 = (Order)returnObject1.getData();
        order2.setOrderSn(Common.genSeqNum(1));
        order2.setId(null);
        order2.setPid(order1.getId());
        ReturnObject returnObject5 = orderDao.addOrder(order);
        if(returnObject5.getCode()!=ReturnNo.OK){
            return returnObject5;
        }
        for(OrderItem orderItem:orderItems){
            orderItem.setOrderId(order1.getId());
            Common.setPoCreatedFields(orderItem,userId,userName);
            ReturnObject returnObject2 = orderItemDao.addOrderItem(orderItem);
            if(returnObject2.getCode()!=ReturnNo.OK){
                return returnObject2;
            }
        }
        OrderInfoRetVo orderInfoRetVo = Common.cloneVo(order1,OrderInfoRetVo.class);
        InternalReturnObject<CustomerSimpleRetVo> returnObject3 = customerService.getSimpleCustomer(order.getCustomerId());
        orderInfoRetVo.setCustomer(returnObject3.getData());
        if(order.getShopId()!=null){
            InternalReturnObject<SimpleObjectRetVo> returnObject4 = shopService.getShopById(order1.getShopId());
            orderInfoRetVo.setShop(returnObject4.getData());
        }
        List<OrderItemRetVo>list1 = new ArrayList<>();
        for(OrderItem orderItem:orderItems){
            list1.add(Common.cloneVo(orderItem,OrderItemRetVo.class));
        }
        orderInfoRetVo.setOrderItem(list1);
        return new ReturnObject<>(orderInfoRetVo);
    }

    /**
     * 内部API-根据orderItem获得orderItemInfo
     * @param id orderItemId
     * @return orderInfo
     */
    public ReturnObject<OrderItemInfo> showOrderItemsByOrderId(Long id,Long userId,String userName){
        try{
            ReturnObject returnObject = orderItemDao.showOrderItem(id,userId);
            if(returnObject.getCode()!=ReturnNo.OK){
                return returnObject;
            }
            OrderItem orderItem = (OrderItem) returnObject.getData();
            ReturnObject returnObject1 = orderDao.showOrderById(orderItem.getOrderId(),userId);
            if(returnObject1.getCode()!=ReturnNo.OK){
                return returnObject1;
            }
            Order order= (Order) returnObject1.getData();
            OrderItemInfo orderItemInfo = Common.cloneVo(orderItem,OrderItemInfo.class);
            orderItemInfo.setState(order.getState());
            return new ReturnObject<>(orderItemInfo);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 内部API
     * 根据 orderSn 获得 Order
     * @return OrderRetVo
     */
    public ReturnObject showOrderByOrderSn(String orderSn){
        ReturnObject returnObject = orderDao.showOrdersByOrderSn(orderSn);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        List<Order>orders = (List<Order>) returnObject.getData();
        if(orders.size()<=0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        OrderRetVo orderRetVo = Common.cloneVo(orders.get(0),OrderRetVo.class);
        return new ReturnObject(orderRetVo);
    }

    /**
     * 内部API
     * 根据 OrderId 获得 OrderItem
     * @return OrderItemRetVo
     */
    public ReturnObject showOrderItemByOrderId(Long orderId,Integer page,Integer pageSize){
        return orderItemDao.showOwnOrderItemByOrderId(orderId,page,pageSize);
    }

    /**
     * 内部API
     * 根据orderSn进行分单
     * @return OrderRetVo
     */
    public ReturnObject seperateOrdersByOrderSn(String orderSn,Byte documentType){
        ReturnObject returnObject = orderDao.showOrdersByOrderSn(orderSn);
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }

        List<Order>orders = (List<Order>) returnObject.getData();
        if(orders.size()<=0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Order order = orders.get(0);

        //确认订单是什么类型,并设置相应的状态，团购是待成团,预售待支付尾款,普通是支付成功
        Integer state =Order.State.PAID.getCode();

        if(order.getGrouponId()!=0){
            state = Order.State.GROUPON_THRESHOLD_TO_BE_REACH.getCode();
        } else if (order.getAdvancesaleId() != 0) {
            if(documentType==(byte)2){  //支付定金
                state = Order.State.THE_BALANCE_TO_BE_PAID.getCode();
            }else if(documentType==(byte)3){    //支付尾款
                state = Order.State.PAID.getCode();
            }
        }else{
            state = Order.State.PAID.getCode();
        }
        order.setState(state);
        //order存在shopId说明不用分单
        if(order.getShopId()!=null){

            //更新父订单状态
            ReturnObject returnObject2 = orderDao.updateOrder(order);
            if(returnObject2.getCode()!=ReturnNo.OK){
                return returnObject2;
            }
            return new ReturnObject(ReturnNo.OK);
        }
        ReturnObject returnObject1 = orderItemDao.showOwnOrderItemByOrderId(order.getId());
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        List<OrderItem>orderItems =(List<OrderItem>) returnObject1.getData();
        HashMap<Long,Long>shopDiscountPrice = new HashMap<>();
        HashMap<Long,Long>shopOriginPrice = new HashMap<>();
        HashMap<Long,Long>shopPoint = new HashMap<>();
        for(OrderItem orderItem:orderItems){
            if(orderItem.getShopId()!=null){
                if(shopDiscountPrice.containsKey(orderItem.getShopId())){
                    shopDiscountPrice.put(orderItem.getShopId(),shopDiscountPrice.get(orderItem.getShopId())+orderItem.getDiscountPrice()*orderItem.getQuantity());
                }else{
                    shopDiscountPrice.put(orderItem.getShopId(),orderItem.getDiscountPrice()*orderItem.getQuantity());
                }

                if(shopOriginPrice.containsKey(orderItem.getShopId())){
                    shopOriginPrice.put(orderItem.getShopId(),shopOriginPrice.get(orderItem.getShopId())+orderItem.getPrice()*orderItem.getQuantity());
                }else{
                    shopOriginPrice.put(orderItem.getShopId(),orderItem.getPrice()*orderItem.getQuantity());
                }

                if(shopPoint.containsKey(orderItem.getShopId())){
                    shopPoint.put(orderItem.getShopId(),shopPoint.get(orderItem.getShopId())+orderItem.getPoint());
                }else{
                    shopPoint.put(orderItem.getShopId(),orderItem.getPoint());
                }
            }
        }
        Set<Long> shopIds = shopDiscountPrice.keySet();
        for(Long shopId:shopIds){
            Order subOrder = Common.cloneVo(order,Order.class);
            subOrder.setId(null);
            subOrder.setOriginPrice(shopOriginPrice.get(shopId));
            subOrder.setDiscountPrice(Math.round(shopDiscountPrice.get(shopId)*1.0/10));
            subOrder.setPoint(shopPoint.get(shopId));
            subOrder.setOrderSn(Common.genSeqNum(1));
            subOrder.setCustomerId(order.getCreatorId());
            subOrder.setState(state);
            subOrder.setExpressFee(0L);
            subOrder.setPid(order.getId());
            subOrder.setShopId(shopId);
            subOrder.setBeDeleted((byte)0);
            Common.setPoCreatedFields(subOrder,order.getCreatorId(),order.getCreatorName());
            ReturnObject returnObject2 = orderDao.addOrder(subOrder);
            if(returnObject2.getCode()!=ReturnNo.OK){
                return returnObject2;
            }
        }
        //更新父订单状态
        ReturnObject returnObject2 = orderDao.updateOrder(order);
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        return new ReturnObject(ReturnNo.OK);
    }

    public ReturnObject refundOrdersByOrderSn(SimpleRefundVo simpleRefundVo){
        ReturnObject returnObject = orderDao.showOrdersByOrderSn(simpleRefundVo.getDocumentId());
        if(returnObject.getCode()!=ReturnNo.OK){
            return returnObject;
        }
        List<Order>orders = (List<Order>) returnObject.getData();
        if(orders.size()<=0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        Order order = orders.get(0);
        order.setState(Order.State.REFUNDED.getCode());
        ReturnObject returnObject1 = orderDao.updateOrder(order);
        if(returnObject1.getCode()!=ReturnNo.OK){
            return returnObject1;
        }
        ReturnObject returnObject2 = orderDao.updateSubOrderIdStateByOrder(order.getId(),Order.State.REFUNDED.getCode());
        if(returnObject2.getCode()!=ReturnNo.OK){
            return returnObject2;
        }
        return new ReturnObject(ReturnNo.OK);
    }
}
