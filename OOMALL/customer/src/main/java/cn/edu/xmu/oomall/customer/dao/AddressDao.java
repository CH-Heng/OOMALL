package cn.edu.xmu.oomall.customer.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.mapper.AddressPoMapper;
import cn.edu.xmu.oomall.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.oomall.customer.mapper.ShoppingCartPoMapper;
import cn.edu.xmu.oomall.customer.mapper.CouponPoMapper;
import cn.edu.xmu.oomall.customer.microservice.Vo.SimpleProductVo;
import cn.edu.xmu.oomall.customer.model.bo.Address;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.bo.ShoppingCart;
import cn.edu.xmu.oomall.customer.model.po.*;
import cn.edu.xmu.oomall.customer.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import net.bytebuddy.asm.Advice;
import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.*;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Repository
public class AddressDao {
    @Autowired
    AddressPoMapper addressPoMapper;
    /**
     *根据买家id查询所有地址
     */
    public ReturnObject selectAddressByUserId(Long userId){
        try{
            AddressPoExample example=new AddressPoExample();
            AddressPoExample.Criteria criteria=example.createCriteria();
            criteria.andCreatorIdEqualTo(userId);
            List<AddressPo> list;
            list=addressPoMapper.selectByExample(example);
            return new ReturnObject(list);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家新增地址
     */
    public ReturnObject insertAddress(Long userId,String userName,AddAddressVo addAddressVo){
        try{
            AddressPo addressPo = cloneVo(addAddressVo,AddressPo.class);
            addressPo.setCustomerId(userId);
            setPoCreatedFields(addressPo,userId,userName);
            addressPoMapper.insert(addressPo);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据地址Id获取地址
     */
    public ReturnObject selectAddressById(Long id){
        try{
            AddressPo addressPo=addressPoMapper.selectByPrimaryKey(id);
            if(addressPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            Address address=cloneVo(addressPo,Address.class);
            return new ReturnObject(address);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 设置默认地址
     */
    public ReturnObject setDefaultAddress(Long userId,String userName,Long id){
        AddressPoExample example=new AddressPoExample();
        AddressPoExample.Criteria criteria= example.createCriteria();;
        criteria.andCreatorIdEqualTo(userId);
        try{
            List<AddressPo> list;
            list=addressPoMapper.selectByExample(example);
            for(int i=0;i<list.size();i++){
                AddressPo addressPo=list.get(i);
                if(addressPo.getId().equals(id)){
                    addressPo.setBeDefault((byte)1);
                }else if(addressPo.getBeDefault()==1){
                    addressPo.setBeDefault((byte)0);
                }
                setPoModifiedFields(addressPo,userId,userName);
                addressPoMapper.updateByPrimaryKeySelective(addressPo);
            }
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家修改自己的地址
     */
    public ReturnObject updateAddressInfo(Long userId,String userName,Long id,AddAddressVo addAddressVo){
        try{
            AddressPo addressPo=null;
            addressPo=addressPoMapper.selectByPrimaryKey(id);
            if(addressPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(!addressPo.getCreatorId().equals(userId)){
                return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
            }
            setPoModifiedFields(addressPo,userId,userName);
            addressPo.setDetail(addAddressVo.getDetail());
            addressPo.setMobile(addAddressVo.getMobile());
            addressPo.setConsignee(addAddressVo.getConsignee());
            addressPo.setRegionId(addAddressVo.getRegionId());
            addressPoMapper.updateByPrimaryKeySelective(addressPo);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 买家删除自己的地址
     */
    public ReturnObject delAddress(Long id){
        try{
            addressPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject(ReturnNo.OK);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
}
