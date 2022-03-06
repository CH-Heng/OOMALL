package cn.edu.xmu.oomall.order.controller;


import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.model.vo.*;
import cn.edu.xmu.oomall.order.service.OrderService;
import cn.edu.xmu.oomall.order.service.RocketMqService;
import cn.edu.xmu.privilegegateway.annotation.aop.Audit;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginName;
import cn.edu.xmu.privilegegateway.annotation.aop.LoginUser;
import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@RequestMapping(value = "/",produces = "application/json;charset=UTF-8")
@RestController
public class OrdersController {

    @Autowired
    OrderService orderService;

    @Autowired
    RocketMqService rocketMqService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 查看优惠活动模块的所有活动
     * @return List<Map<String, Object>>
     */
    @GetMapping("orders/states")
    public Object showAllState(){
        return  Common.decorateReturnObject(orderService.showAllState());
    }


    /**
     * 买家查询名下订单（概要）
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
    @Audit
    @GetMapping("orders")
    public Object showCustomerOwnOrderInfo(@RequestParam(required = false)String orderSn,
                                           @RequestParam(required = false)Integer state,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ") @RequestParam(required = false) ZonedDateTime beginTime,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ") @RequestParam(required = false)ZonedDateTime endTime,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @LoginUser Long userId,
                                           @LoginName  String userName) {
        if(beginTime!=null&&endTime!=null){
            if(beginTime.compareTo(endTime)>0){
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
            }
        }
        return Common.decorateReturnObject(orderService.showCustomerOwnOrderInfo(orderSn,state,beginTime,endTime,page,pageSize,userId,userName));
    }

    /**
     * 买家申请建立订单（普通，团购，预售）
     * @param userId 用户Id
     * @param userName 用户姓名
     * @param orderInfoVo 指定新订单的资料
     * @param bindingResult bindingResult
     * @return 新建结果
     */
    @Audit
    @PostMapping("orders")
    public Object customerPostNewNormalOrder(@LoginUser Long userId, @LoginName String userName, @Valid @RequestBody OrderInfoVo orderInfoVo,
                                             HttpServletResponse httpServletResponse,BindingResult bindingResult
                                             ){
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }
        ZonedDateTime beginTime = ZonedDateTime.now(ZoneId.systemDefault());
        orderInfoVo.setOrderSn(cn.edu.xmu.privilegegateway.annotation.util.Common.genSeqNum(1));
        rocketMqService.sendCancelOrderMessage(orderInfoVo);
        ReturnObject returnObject = orderService.customerPostNewNormalOrder(userId,userName, orderInfoVo,beginTime);
        if(returnObject.getCode()== ReturnNo.OK){
            httpServletResponse.setStatus(HttpServletResponse.SC_CREATED);
        }
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家查询订单完整信息（普通，团购，预售）
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return 查询结果
     */
    @Audit
    @GetMapping("orders/{id}")
    public Object showOrderInfoById(@PathVariable Long id,@LoginUser Long userId,@LoginName String userName){
        return Common.decorateReturnObject(orderService.showOwnOrderInfoById(id,userId,userName));
    }


    /**
     * 买家修改本人名下订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param orderUpdateFieldVo 订单修改信息
     * @return 修改信息
     */
    @Audit
    @PutMapping("orders/{id}")
    public Object updateOrderUsingField(@PathVariable Long id,@LoginUser Long userId,@LoginName String userName,@Valid @RequestBody OrderUpdateFieldVo orderUpdateFieldVo,
                                        BindingResult bindingResult){
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }
        return Common.decorateReturnObject(orderService.updateOwnOrderUsingField(id,userId,userName,orderUpdateFieldVo));
    }

    /**
     * 买家逻辑删除本人名下订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 删除信息
     */
    @Audit
    @DeleteMapping("orders/{id}")
    public Object deleteOrderById(@PathVariable Long id, @LoginUser Long userId, @LoginName String userName){
        return Common.decorateReturnObject(orderService.deleteOwnOrderById(id,userId,userName));
    }

    /**
     * 买家取消本人名下订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 取消结果
     */
    @Audit
    @PutMapping("orders/{id}/cancel")
    public Object cancelOrderById(@PathVariable Long id,@LoginUser Long userId,@LoginName String userName){
        return Common.decorateReturnObject(orderService.cancelOrderById(id,userId,userName));
    }

    /**
     * 买家标记确认收货
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 修改结果
     */
    @Audit
    @PutMapping("orders/{id}/confirm")
    public Object confirmOrderById(@PathVariable Long id,@LoginUser Long userId,@LoginName String userName){
        return Common.decorateReturnObject(orderService.confirmOrderById(id,userId,userName));
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
    @Audit(departName = "shops")
    @GetMapping("shops/{shopId}/orders")
    public Object showShopOwnOrderInfo(@PathVariable Long shopId,
                                           @RequestParam(required = false) Long customerId,
                                           @RequestParam(required = false) String orderSn,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ") @RequestParam(required = false) ZonedDateTime beginTime,
                                           @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ") @RequestParam(required = false)ZonedDateTime endTime,
                                           @RequestParam(defaultValue = "1") Integer page,
                                           @RequestParam(defaultValue = "10") Integer pageSize,
                                           @LoginUser Long userId,
                                           @LoginName  String userName) {
        if(beginTime!=null&&endTime!=null){
            if(beginTime.compareTo(endTime)>0){
                return Common.decorateReturnObject(new ReturnObject(ReturnNo.LATE_BEGINTIME));
            }
        }
        return Common.decorateReturnObject(orderService.showShopOwnOrderInfo(shopId,customerId,orderSn,beginTime,endTime,page,pageSize,userId,userName));
    }

    /**
     * 店家修改订单 (留言)
     * @param id 订单id
     * @param shopId 店铺id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param messageVo 消息
     * @return 修改结果
     */
    @Audit(departName = "shops")
    @PutMapping("shops/{shopId}/orders/{id}")
    public Object updateOrderMessageById(@PathVariable Long id, @PathVariable Long shopId,
                                         @Valid @RequestBody MessageVo messageVo, @LoginUser Long userId, @LoginName String userName,
                                         BindingResult bindingResult){
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }
        return Common.decorateReturnObject(orderService.updateOrderMessageById(id,shopId,userId,userName, messageVo.getMessage()));
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     * @param id 订单id
     * @param shopId 店铺id
     * @param userId 操作者id
     * @param userName 操作者姓名
     * @return 查询结果
     */
    @Audit(departName = "shops")
    @GetMapping("/shops/{shopId}/orders/{id}")
    public Object showShopOwnOrderInfoById(@PathVariable Long id,@PathVariable Long shopId,@LoginUser Long userId,@LoginName String userName){
        return Common.decorateReturnObject(orderService.showShopOwnOrderInfoById(id,shopId,userId,userName));
    }

    /**
     * 管理员取消本店铺订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 删除信息
     */
    @Audit(departName = "shops")
    @DeleteMapping("shops/{shopId}/orders/{id}")
    public Object deleteShopOrderById(@PathVariable Long id,@PathVariable Long shopId,@LoginUser Long userId,@LoginName String userName){
        return Common.decorateReturnObject(orderService.deleteShopOrderById(id,shopId,userId,userName));
    }

    /**
     *
     * 店家对订单标记发货
     * @param id 订单id
     * @param shopId 店铺id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param freightSnVo 指定发货咨询
     * @return 修改结果
     */
    @Audit(departName = "shops")
    @PutMapping("shops/{shopId}/orders/{id}/deliver")
    public Object postFreights(@PathVariable Long id, @PathVariable Long shopId, @LoginUser Long userId, @LoginName String userName,
                               @Valid @RequestBody FreightSnVo freightSnVo, BindingResult bindingResult){
        Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            return obj;
        }

        return Common.decorateReturnObject(orderService.postFreights(id,shopId,userId,userName,freightSnVo.getShipmentSn()));
    }

    /**
     * 查询自己订单的支付信息
     * @param id 订单id
     * @param userId 用户id
     * @param userName 用户名
     * @return 支付信息
     */
    @Audit
    @GetMapping("orders/{id}/payment")
    public Object showOwnPayment(@PathVariable Long id,@LoginUser Long userId, @LoginName String userName){
        return Common.decorateReturnObject(orderService.showOwnPayment(id,userId,userName));
    }

    /**
     * 查询自己订单的退款信息
     * @param id 订单id
     * @param userId 用户id
     * @param userName 用户名
     * @return 支付信息
     */
    @Audit
    @GetMapping("orders/{id}/refund")
    public Object showOwnRefund(@PathVariable Long id,@LoginUser Long userId, @LoginName String userName){
        return Common.decorateReturnObject(orderService.showOwnRefund(id,userId,userName));
    }

    /**
     * 内部API-确认团购订单
     * @param id 团购订单Id
     * @param shopId 店铺id
     * @param userId 用户id
     * @param userName 用户姓名
     * @return 结果
     */
    @Audit
    @PutMapping("internal/shops/{shopId}/grouponorders/{id}/confirm")
    public Object confirmGrouponOrder(@PathVariable Long id,@PathVariable Long shopId,@LoginUser Long userId, @LoginName String userName){
        return Common.decorateReturnObject(orderService.confirmGrouponOrder(id,shopId,userId,userName));
    }


    /**
     * 内部API-取消订单
     * @param id 团购订单Id
     * @param shopId 店铺id
     * @param userId 用户id
     * @param userName 用户姓名
     * @return 结果
     */
    @Audit
    @PutMapping("internal/shops/{shopId}/orders/{id}/cancel")
    public Object cancelOrder(@PathVariable Long id,@PathVariable Long shopId,@LoginUser Long userId, @LoginName String userName){
        return Common.decorateReturnObject(orderService.cancelOrder(id,shopId,userId,userName));
    }


    /**
     * 内部API-管理员建立售后订单
     * @param shopId 店铺Id
     * @param userId 用户id
     * @param userName 用户名
     * @param orderInfo 订单信息
     * @param bindingResult bindingResult
     * @return 结果
     */
    @Audit
    @PostMapping("internal/shops/{shopId}/orders")
    public Object adminPostAfterSaleOrder(@PathVariable Long shopId, @LoginUser Long userId, @LoginName String userName,@Valid @RequestBody AfterSaleOrderInfo orderInfo,
                                                        BindingResult bindingResult ){
         Object obj = Common.processFieldErrors(bindingResult,httpServletResponse);
        if (null != obj) {
            Map<String,Object>ret = (Map<String, Object>)obj;
            return new InternalReturnObject(ReturnNo.FIELD_NOTVALID.getCode(),ret.get("errmsg").toString());
        }
        return Common.decorateReturnObject(orderService.adminPostAfterSaleOrder(shopId,userId,userName,orderInfo));
    }

    /**
     * 内部API-根据orderItem获得orderItemInfo
     * @param id orderItemId
     * @return orderInfo
     */
    @Audit
    @GetMapping("internal/orderitems/{id}")
    public Object showOrderItemsByOrderId(@PathVariable Long id,@LoginUser Long userId,@LoginName String userName){
        return Common.decorateReturnObject(orderService.showOrderItemsByOrderId(id,userId,userName));
    }

    /**
     * 内部API
     * 根据 orderSn 获得 Order
     * @return OrderRetVo
     */
    @Audit
    @GetMapping("internal/order")
    public Object showOrderByOrderSn(@RequestParam String orderSn){
        return Common.decorateReturnObject(orderService.showOrderByOrderSn(orderSn));
    }

    /**
     * 内部API
     * 根据 OrderId 获得 OrderItem
     * @return OrderItemRetVo
     */
    @Audit
    @GetMapping("internal/orderitem")
    public Object showOrderItemByOrderId(@RequestParam Long orderId,
                                               @RequestParam(required = false,defaultValue = "1") Integer page,
                                               @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        return Common.decorateReturnObject(orderService.showOrderItemByOrderId(orderId,page,pageSize));
    }
}
