package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.mapper.OrderPoMapper;
import cn.edu.xmu.oomall.order.microservice.FreightService;
import cn.edu.xmu.oomall.order.microservice.GoodsService;
import cn.edu.xmu.oomall.order.microservice.vo.FreightCalculatingPostVo;
import cn.edu.xmu.oomall.order.microservice.vo.FreightCalculatingRetVo;
import cn.edu.xmu.oomall.order.model.bo.Order;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.model.po.OrderPoExample;
import cn.edu.xmu.oomall.order.model.vo.OrderRetVo;
import cn.edu.xmu.oomall.order.microservice.vo.ProductRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@Repository
public class OrderDao {
    @Autowired
    OrderPoMapper orderPoMapper;

    @Autowired
    OrderItemDao orderItemDao;

    @Resource
    GoodsService goodsService;

    @Resource
    FreightService freightService;

    /**
     * 查看订单模块的所有活动
     * @return ReturnObject<List<Map<String, Object>>>
     */
    public ReturnObject<List<Map<String, Object>>> showAllState() {
        List<Map<String, Object>> stateList = new ArrayList<>();
        for (Order.State states : Order.State.values()) {
            Map<String, Object> temp = new HashMap<>();
            temp.put("code", states.getCode());
            temp.put("name", states.getDescription());
            stateList.add(temp);
        }
        return new ReturnObject<>(stateList);
    }

    /**
     * 买家查询名下订单
     * @param example 查询条件
     * @param page 页
     * @param pageSize 页大小
     * @return 带分页的订单详情
     */
    public ReturnObject<PageInfo<VoObject>> showCustomerOwnOrderByField(OrderPoExample example,Integer page,Integer pageSize){
        try{
            PageHelper.startPage(page,pageSize);
            List<OrderPo>orderPos = orderPoMapper.selectByExample(example);
            List<VoObject>orderRetVos = new ArrayList<>();
            for(OrderPo orderPo:orderPos){
                OrderRetVo orderRetVo = Common.cloneVo(orderPo,OrderRetVo.class);
                orderRetVos.add(orderRetVo);
            }

            PageInfo<VoObject>pageInfo = new PageInfo<>(orderRetVos);
            ReturnObject ret = new ReturnObject<>(pageInfo);
            return cn.edu.xmu.oomall.core.util.Common.getPageRetVo(ret, OrderRetVo.class);
        }catch(Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据orderSn查找订单
     * @param orderSn 订单编号
     * @return 带分页的订单详情
     */
    public ReturnObject<List<Order>> showOrdersByOrderSn(String orderSn){
        try{
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andOrderSnEqualTo(orderSn);
            List<OrderPo>orderPos = orderPoMapper.selectByExample(example);
            List<Order>orders = new ArrayList<>();
            for(OrderPo orderPo:orderPos){
                Order order =  Common.cloneVo(orderPo,Order.class);
                orders.add(order);
            }
            return new ReturnObject<>(orders);
        }catch(Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据grouponId查找订单
     * @param grouponId 订单编号
     * @return 带分页的订单详情
     */
    public ReturnObject<List<Order>> showOrdersByGrouponId(Long grouponId){
        try{
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andGrouponIdEqualTo(grouponId);
            // 只有待成团的状态才能返回
            criteria.andStateEqualTo(Order.State.GROUPON_THRESHOLD_TO_BE_REACH.getCode());
            //被删除不返回
            criteria.andBeDeletedEqualTo((byte)0);
            //只找父订单
            criteria.andPidEqualTo(0L);
            List<OrderPo>orderPos = orderPoMapper.selectByExample(example);
            List<Order>orders = new ArrayList<>();
            for(OrderPo orderPo:orderPos){
                Order order =  Common.cloneVo(orderPo,Order.class);
                orders.add(order);
            }
            return new ReturnObject<>(orders);
        }catch(Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家取消本人名下订单
     * @param id 订单id
     * @return 删除结果
     */
    public ReturnObject<Order> cancelOrderById(Long id){
        try{
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(id);
            if(orderPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            OrderPo orderPo1 = new OrderPo();
            orderPo1.setId(id);
            //修改状态
            orderPo1.setState(Order.State.TO_REFUND.getCode());
            orderPo1.setGmtModified(LocalDateTime.now());
            Common.setPoModifiedFields(orderPo1,orderPo.getCreatorId(),orderPo.getCreatorName());
            orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            return new ReturnObject<>(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }



    /**
     * 新建order
     * @param order 新建订单信息
     * @return 返回新建结果
     */
    public ReturnObject addOrder(Order order){
        try{
            OrderPo orderPo = Common.cloneVo(order,OrderPo.class);
            int ret = orderPoMapper.insert(orderPo);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            }
            Order order1 = Common.cloneVo(orderPo,Order.class);
            return new ReturnObject<>(order1);

        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家查询订单完整信息（普通，团购，预售）
     * @param id 订单id
     * @param userId 操作者id
     * @return 查询结果
     */
    public ReturnObject<Order> showOrderById(Long id,Long userId){
        try{
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(id);
            if(orderPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            // 被删除就不返回
            if(orderPo.getBeDeleted()==1){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>(Common.cloneVo(orderPo,Order.class));
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 计算运费
     * @param regionId 地区Id
     * @param orderItems 订单明细
     * @return 订单
     */
    public ReturnObject findFeightPrice(Long regionId,List<OrderItem> orderItems){
        try{
            // TODO: 2021/12/16 需要判断redis缓存
            List<FreightCalculatingPostVo> postVos = new ArrayList<>();
            for (OrderItem orderItem : orderItems) {
                FreightCalculatingPostVo item = new FreightCalculatingPostVo();
                item.setProductId(orderItem.getProductId());
                item.setQuantity(orderItem.getQuantity().intValue());
                ProductRetVo productRetVo  = goodsService.getProductDetails(item.getProductId()).getData();
                item.setFreightId(productRetVo.getFreightId());
                item.setWeight(productRetVo.getWeight().intValue());
                postVos.add(item);
            }
            FreightCalculatingRetVo freightCalculatingVo =  freightService.calculateFreight(regionId, postVos).getData();
            Long weight = freightCalculatingVo.getFreightPrice();
            return new ReturnObject(weight);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }

    /**
     * 根据父订单id获得子订单
     * @param pId
     * @return
     */
    public ReturnObject getSubOrderByPid(Long pId){
        try{
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andPidEqualTo(pId);
            List<OrderPo>subOrders = orderPoMapper.selectByExample(example);
            return new ReturnObject(subOrders);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家修改本人名下订单
     * id一定是pid
     * @param id  订单id
     * @param order 订单信息
     * @return 修改订单
     */
    public ReturnObject<Order> updateOwnOrderUsingField(Long id,Order order){
        try{
            OrderPo orderPo1 = Common.cloneVo(order,OrderPo.class);
            orderPo1.setId(id);
            orderPo1.setGmtModified(LocalDateTime.now());
            orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            ReturnObject returnObject = getSubOrderByPid(id);
            if(returnObject.getCode()!=ReturnNo.OK){
                return returnObject;
            }
            List<OrderPo>subOrders = (List<OrderPo>) returnObject.getData();
            for(OrderPo orderPo:subOrders){
                orderPo1.setId(orderPo.getId());
                orderPo1.setGmtModified(LocalDateTime.now());
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
            }
            return new ReturnObject<>(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }



    /**
     * 买家逻辑删除本人名下订单
     * @param id 订单id 一定是pid
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 删除结果
     */
    public ReturnObject<Order> deleteOwnOrderById(Long id,Long userId,String userName){
        try{
            OrderPo orderPo1 = new OrderPo();
            orderPo1.setId(id);
            //进行逻辑删除
            orderPo1.setBeDeleted((byte) 1);
            orderPo1.setGmtModified(LocalDateTime.now());
            Common.setPoModifiedFields(orderPo1,userId,userName);
            orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            ReturnObject returnObject = getSubOrderByPid(id);
            if(returnObject.getCode()!=ReturnNo.OK){
                return returnObject;
            }
            List<OrderPo>subOrders = (List<OrderPo>) returnObject.getData();
            for(OrderPo orderPo:subOrders){
                orderPo.setBeDeleted((byte)1);
                orderPo.setGmtModified(LocalDateTime.now());
                Common.setPoModifiedFields(orderPo,userId,userName);
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
            }
            return new ReturnObject<>(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }


    /**
     * 买家取消本人名下订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 取消结果
     */
    public ReturnObject<Order> cancelOwnOrderById(Long id,Long userId,String userName){
        try{
            OrderPo orderPo1 = new OrderPo();
            orderPo1.setId(id);
            orderPo1.setState(Order.State.REFUNDED.getCode());
            orderPo1.setGmtModified(LocalDateTime.now());
            Common.setPoModifiedFields(orderPo1,userId,userName);
            int ret = orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            } else {
                ReturnObject returnObject = updateSubOrderIdStateByOrder(id,Order.State.REFUNDED.getCode());
                if(returnObject.getCode()!=ReturnNo.OK){
                    return returnObject;
                }
                return new ReturnObject<>(ReturnNo.OK);
            }
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家确认收货
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 确认结果
     */
    public ReturnObject<Order> confirmOwnOrderById(Long id,Long userId,String userName){
        try{
            OrderPo orderPo1 = new OrderPo();
            orderPo1.setId(id);
            orderPo1.setState(Order.State.COMPLETED.getCode());
            orderPo1.setGmtModified(LocalDateTime.now());
            Common.setPoModifiedFields(orderPo1,userId,userName);
            int ret = orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            } else {
                ReturnObject returnObject = updateSubOrderIdStateByOrder(id,Order.State.COMPLETED.getCode());
                if(returnObject.getCode()!=ReturnNo.OK){
                    return returnObject;
                }
                return new ReturnObject<>(ReturnNo.OK);
            }
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 店家修改订单 (留言)
     * @param id 订单id
     * @param shopId 店铺id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param message 消息
     * @return 修改结果
     */
    public ReturnObject<Order> updateOwnOrderMessageById(Long id,Long shopId,Long userId,String userName,String message){
        try{
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(id);
            if(orderPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!orderPo.getCreatorId().equals(userId)){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            if(!orderPo.getShopId().equals(shopId)){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            // 已经被删除的也不能进行确认
            if(orderPo.getBeDeleted()==1){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            OrderPo orderPo1 = new OrderPo();
            orderPo1.setId(id);
            orderPo1.setMessage(message);
            orderPo1.setGmtModified(LocalDateTime.now());
            Common.setPoModifiedFields(orderPo1,userId,userName);
            int ret = orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            } else {
                return new ReturnObject<>(ReturnNo.OK);
            }
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 店铺删除本店铺订单
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @return 删除结果
     */
    public ReturnObject<Order> deleteShopOrderById(Long id,Long shopId,Long userId,String userName){
        try{
            OrderPo orderPo = orderPoMapper.selectByPrimaryKey(id);
            if(orderPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!orderPo.getCreatorId().equals(userId)){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            // 已经被删除的也不能进行删除
            if(orderPo.getBeDeleted()==1){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!orderPo.getShopId().equals(shopId)){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            if(orderPo.getState().equals(Order.State.DELIVERED.getCode())
                    ||orderPo.getState().equals(Order.State.PAID.getCode())
                    ||orderPo.getState().equals(Order.State.GROUPON_THRESHOLD_TO_BE_REACH.getCode())
                    ||orderPo.getState().equals(Order.State.TO_BE_RECEIVED.getCode())){
                OrderPo orderPo1 = new OrderPo();
                orderPo1.setId(id);
                orderPo1.setState(Order.State.TO_REFUND.getCode());
                orderPo1.setGmtModified(LocalDateTime.now());
                Common.setPoModifiedFields(orderPo1,userId,userName);
                int ret = orderPoMapper.updateByPrimaryKeySelective(orderPo1);
                if (ret == 0) {
                    return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
                } else {
                    ReturnObject returnObject = updateSubOrderIdStateByOrder(id,Order.State.TO_REFUND.getCode());
                    if(returnObject.getCode()!=ReturnNo.OK){
                        return returnObject;
                    }
                    return new ReturnObject<>(ReturnNo.OK);
                }
            }else{
                return new ReturnObject<>(ReturnNo.STATENOTALLOW);
            }
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     *
     * 店家对订单标记发货
     * @param id 订单id
     * @param userId 操作者id
     * @param userName 操作者信息
     * @param freightSn 指定发货咨询
     * @return 修改结果
     */
    public ReturnObject<Order> postFreights(Long id,Long userId,String userName,String freightSn){
        try{
            OrderPo orderPo1 = new OrderPo();
            orderPo1.setId(id);
            orderPo1.setShipmentSn(freightSn);
            //商家可以用此 API 将一个状态为待发货的订单改为待收货，并记录运单信息
            orderPo1.setState(Order.State.DELIVERED.getCode());
            orderPo1.setGmtModified(LocalDateTime.now());
            Common.setPoModifiedFields(orderPo1,userId,userName);
            int ret = orderPoMapper.updateByPrimaryKeySelective(orderPo1);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            } else {//只需修改子订单状态即可
                ReturnObject returnObject = updateSubOrderIdStateByOrder(id,Order.State.DELIVERED.getCode());
                if(returnObject.getCode()!=ReturnNo.OK){
                    return returnObject;
                }
                return new ReturnObject<>(ReturnNo.OK);
            }
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 更新order
     * @param order 待更新订单信息
     * @return 返回新建结果
     */
    public ReturnObject updateOrder(Order order){
        try{
            OrderPo orderPo = Common.cloneVo(order,OrderPo.class);
            int ret = orderPoMapper.updateByPrimaryKeySelective(orderPo);
            if (ret == 0) {
                return new ReturnObject<>(ReturnNo.FIELD_NOTVALID);
            }
            Order order1 = Common.cloneVo(orderPo,Order.class);
            return new ReturnObject<>(order1);

        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据OrderId更新SubObject
     * @param orderId 父订单orderId
     * @param state 需要更改的状态
     * @return 返回更新结果
     */
    public ReturnObject updateSubOrderIdStateByOrder(Long orderId,Integer state){
        try{
            OrderPoExample example = new OrderPoExample();
            OrderPoExample.Criteria criteria = example.createCriteria();
            criteria.andPidEqualTo(orderId);
            List<OrderPo>subOrders = orderPoMapper.selectByExample(example);
            for(OrderPo orderPo:subOrders){
                orderPo.setState(state);
                orderPoMapper.updateByPrimaryKeySelective(orderPo);
            }
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
}
