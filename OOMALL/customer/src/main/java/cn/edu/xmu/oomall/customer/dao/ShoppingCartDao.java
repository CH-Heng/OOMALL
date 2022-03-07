package cn.edu.xmu.oomall.customer.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.oomall.customer.model.bo.ShoppingCart;
import cn.edu.xmu.oomall.customer.model.po.*;
import cn.edu.xmu.oomall.customer.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Repository
public class ShoppingCartDao {
    @Autowired
    ShoppingCartPoMapper shoppingCartPoMapper;

    /**
     * 通过用户id获取购物车列表
     */
    public ReturnObject showAllCarts(Long id,Integer page,Integer pageSize){
        ShoppingCartPoExample example=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria=example.createCriteria();
        criteria.andCreatorIdEqualTo(id);
        example.or(criteria);
        List<ShoppingCartPo> poList=null;
        try{
            poList=shoppingCartPoMapper.selectByExample(example);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }

        List<ShoppingCart> shoppingCartList=new ArrayList<>();
        int size=poList.size();
        for(int i=0;i<size;i++){
            ShoppingCartPo shoppingCartPo= poList.get(i);
            ShoppingCart shoppingCart=cloneVo(shoppingCartPo,ShoppingCart.class);
            shoppingCartList.add(shoppingCart);
        }
        PageHelper.startPage(page,pageSize);
        PageInfo<ShoppingCart> pageInfo=new PageInfo<>(shoppingCartList);
        return Common.getPageRetVo(new ReturnObject(pageInfo),ShoppingCart.class);
    }

    /**
     * 买家添加商品到购物车
     */
    public ReturnObject addToCart(Long userId,String userName,Long price,AddCartVo addCartVo){
        try{
            ShoppingCartPo shoppingCartPo=new ShoppingCartPo();
            shoppingCartPo.setCustomerId(userId);
            shoppingCartPo.setProductId(addCartVo.getProductId());
            shoppingCartPo.setQuantity(addCartVo.getQuantity());
            shoppingCartPo.setPrice(price);
            setPoCreatedFields(shoppingCartPo,userId,userName);
            shoppingCartPoMapper.insert(shoppingCartPo);
            AddCartRetVo addCartRetVo=new AddCartRetVo();
            addCartRetVo.setId(shoppingCartPo.getId());
            addCartRetVo.setPrice(shoppingCartPo.getPrice());
            addCartRetVo.setQuantity(shoppingCartPo.getQuantity());
            return new ReturnObject(addCartRetVo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     *买家清空购物车
     */
    public ReturnObject clearGoods(Long userId){
        ShoppingCartPoExample example=new ShoppingCartPoExample();
        ShoppingCartPoExample.Criteria criteria= example.createCriteria();
        criteria.andCreatorIdEqualTo(userId);
        try{
            List<ShoppingCartPo> list;
            list=shoppingCartPoMapper.selectByExample(example);
            for(int i=0;i<list.size();i++)
            {
                shoppingCartPoMapper.deleteByPrimaryKey(list.get(i).getId());
            }
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据购物车id核对信息
     */
    public ReturnObject searchByCartId(Long userId,Long id,Long productId){
        try {
            ShoppingCartPo shoppingCartPo = shoppingCartPoMapper.selectByPrimaryKey(id);
            if(shoppingCartPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!Objects.equals(userId, shoppingCartPo.getCreatorId())){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            if(!Objects.equals(shoppingCartPo.getProductId(), productId)){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家修改购物车单个商品的数量或规格
     */
    public ReturnObject changeCartInfo(Long id,ChangeCartInfoVo changeCartInfoVo){
        try{
            ShoppingCartPo shoppingCartPo=shoppingCartPoMapper.selectByPrimaryKey(id);
            shoppingCartPo.setQuantity(changeCartInfoVo.getQuantity());
            shoppingCartPo.setProductId(changeCartInfoVo.getProductId());
            shoppingCartPoMapper.updateByPrimaryKeySelective(shoppingCartPo);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }


    /**
     * 买家删除购物车中商品
     */
    public ReturnObject delGoods(Long id,Long userId){
        try{
            ShoppingCartPo shoppingCartPo=shoppingCartPoMapper.selectByPrimaryKey(id);
            if(!Objects.equals(userId, shoppingCartPo.getCreatorId())){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            shoppingCartPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
}
