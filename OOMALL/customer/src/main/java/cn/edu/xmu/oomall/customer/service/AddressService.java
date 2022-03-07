package cn.edu.xmu.oomall.customer.service;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.dao.AddressDao;
import cn.edu.xmu.oomall.customer.microservice.FreightService;
import cn.edu.xmu.oomall.customer.model.bo.Address;
import cn.edu.xmu.oomall.customer.model.po.AddressPo;
import cn.edu.xmu.oomall.customer.model.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Service
public class AddressService {
    @Autowired
    AddressDao addressDao;

    @Resource
    FreightService freightService;

    /**
     * 买家新增地址
     */
    public ReturnObject addAddress(Long userId,String userName,AddAddressVo addAddressVo){
        ReturnObject returnObjectSelect=addressDao.selectAddressByUserId(userId);
        List<AddressPo> list=(List<AddressPo>) returnObjectSelect.getData();
        if(list.size()>=20){
            return new ReturnObject(ReturnNo.ADDRESS_OUTLIMIT);
        }
        ReturnObject returnObject=addressDao.insertAddress(userId,userName,addAddressVo);
        return returnObject;
    }

    /**
     * 买家设置默认地址
     */
    public ReturnObject setDefaultAddress(Long userId,String userName,Long id){
        ReturnObject returnObjectSelect=addressDao.selectAddressById(id);
        Address address=(Address) returnObjectSelect.getData();
        if(!address.getCreatorId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject returnObject=addressDao.setDefaultAddress(userId,userName,id);
        return returnObject;
    }

    /**
     * 买家修改自己的地址
     */
    public ReturnObject changeAddressInfo(Long userId,String userName,Long id,AddAddressVo addAddressVo){
        ReturnObject returnObject=addressDao.updateAddressInfo(userId,userName,id,addAddressVo);
        return returnObject;
    }

    /**
     * 买家删除地址
     */
    public ReturnObject delAddress(Long userId,Long id){
        ReturnObject returnObjectSelect=addressDao.selectAddressById(id);
        Address address=(Address) returnObjectSelect.getData();
        if(!address.getCreatorId().equals(userId)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE);
        }
        ReturnObject returnObject=addressDao.delAddress(id);
        return returnObject;
    }
}
