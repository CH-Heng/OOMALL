package cn.edu.xmu.oomall.order.dao;

import cn.edu.xmu.oomall.core.model.VoObject;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.order.mapper.OrderItemPoMapper;
import cn.edu.xmu.oomall.order.model.bo.OrderItem;
import cn.edu.xmu.oomall.order.model.po.OrderItemPo;
import cn.edu.xmu.oomall.order.model.po.OrderItemPoExample;
import cn.edu.xmu.oomall.order.model.po.OrderPo;
import cn.edu.xmu.oomall.order.model.vo.OrderItemInfo;
import cn.edu.xmu.oomall.order.model.vo.OrderRetVo;
import cn.edu.xmu.privilegegateway.annotation.util.Common;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author RenJieZheng 22920192204334
 * @date 2021/11/16
 */
@Repository
public class OrderItemDao {
    @Autowired
    OrderItemPoMapper orderItemPoMapper;

    /**
     * 根据订单id查订单明细，不区分是订单id还是子订单id
     * @param orderId 订单id
     * @return 订单明细列表
     */
    public ReturnObject<List<OrderItem>> showOwnOrderItemByOrderId(Long orderId){
        try{
            OrderItemPoExample example = new OrderItemPoExample();
            OrderItemPoExample.Criteria criteria1 = example.createCriteria();
            criteria1.andOrderIdEqualTo(orderId);
            example.or(criteria1);
            List<OrderItemPo>orderItemPos = orderItemPoMapper.selectByExample(example);
            List<OrderItem>orderItems = new ArrayList<>();
            for(OrderItemPo orderItemPo:orderItemPos){
                OrderItem orderItem = Common.cloneVo(orderItemPo, OrderItem.class);
                orderItems.add(orderItem);
            }
            return new ReturnObject<>(orderItems);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 新建订单明细
     * @param orderItem 订单明细
     * @return 新建结果
     */
    public ReturnObject addOrderItem(OrderItem orderItem){
        try{
            OrderItemPo orderItemPo = Common.cloneVo(orderItem,OrderItemPo.class);
            int ret = orderItemPoMapper.insert(orderItemPo);
            if(ret==0){
                return new ReturnObject(ReturnNo.FIELD_NOTVALID);
            }
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }

    /**
     * 根据orderItemId找orderItem
     * @param orderItemId orderItemId
     * @param userId userId
     * @return 查询结果
     */
    public ReturnObject showOrderItem(Long orderItemId,Long userId){
        try{
            OrderItemPo orderItemPo = orderItemPoMapper.selectByPrimaryKey(orderItemId);
            if(orderItemPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(Common.cloneVo(orderItemPo,OrderItem.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }

    }

    /**
     * 根据订单id查订单明细，不区分是订单id还是子订单id
     * @param orderId 订单id
     * @return 订单明细列表
     */
    public ReturnObject<PageInfo<VoObject>> showOwnOrderItemByOrderId(Long orderId,Integer page,Integer pageSize){
        try{
            OrderItemPoExample example = new OrderItemPoExample();
            OrderItemPoExample.Criteria criteria1 = example.createCriteria();
            criteria1.andOrderIdEqualTo(orderId);
            example.or(criteria1);
            PageHelper.startPage(page,pageSize);
            List<OrderItemPo>orderItemPos = orderItemPoMapper.selectByExample(example);
            List<VoObject>orderItemInfos = new ArrayList<>();
            for(OrderItemPo orderItemPo:orderItemPos){
                OrderItemInfo orderItemInfo = Common.cloneVo(orderItemPo, OrderItemInfo.class);
                orderItemInfos.add(orderItemInfo);
            }
            PageInfo<VoObject> pageInfo = new PageInfo<>(orderItemInfos);
            return new ReturnObject<>(pageInfo);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

}
