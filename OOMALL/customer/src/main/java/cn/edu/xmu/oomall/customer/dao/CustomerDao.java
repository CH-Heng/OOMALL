package cn.edu.xmu.oomall.customer.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.customer.mapper.CustomerPoMapper;
import cn.edu.xmu.oomall.customer.model.bo.Customer;
import cn.edu.xmu.oomall.customer.model.po.*;
import cn.edu.xmu.oomall.customer.model.vo.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.*;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;

/**
 *
 * @author Heng Chen 22920192204172
 * @date 2021/11/27
 */
@Repository
public class CustomerDao {
    @Autowired
    CustomerPoMapper customerPoMapper;

    /**
     * 用户注册
     */
    public ReturnObject insertUser(CreateUserVo createUserVo){
        try {
            CustomerPo customerPo=new CustomerPo();
            customerPo.setUserName(createUserVo.getUserName());
            customerPo.setPassword(createUserVo.getPassword());
            customerPo.setEmail(createUserVo.getEmail());
            customerPo.setMobile(createUserVo.getMobile());
            customerPo.setName(createUserVo.getName());
            customerPo.setGmtCreate(LocalDateTime.now());
            customerPoMapper.insert(customerPo);
            CustomerPoExample example=new CustomerPoExample();
            CustomerPoExample.Criteria criteria=example.createCriteria();
            criteria.andUserNameEqualTo(createUserVo.getUserName());
            List<CustomerPo> list=customerPoMapper.selectByExample(example);
            SimpleCustomer simpleCustomer=new SimpleCustomer(list.get(0).getId(),list.get(0).getName());
            return new ReturnObject(simpleCustomer);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 根据邮箱查询用户
     */
    public ReturnObject<List<CustomerPo>> searchByEmail(String email){
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andEmailEqualTo(email);
        example.or(criteria);
        List<CustomerPo> customerPos=null;
        try{
            customerPos=customerPoMapper.selectByExample(example);
        }
        catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if(customerPos.size()==0){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        else{
            return new ReturnObject<>(ReturnNo.OK);
        }
    }

    /**
     * 根据用户名查询用户
     */
    public ReturnObject searchByUserName(String userName){
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        example.or(criteria);
        List<CustomerPo> customerPos=null;
        try{
            customerPos=customerPoMapper.selectByExample(example);
        }
        catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if(customerPos.size()==0){
            return new ReturnObject(ReturnNo.CUSTOMERID_NOTEXIST);
        }
        else{
            CustomerPo customerPo=customerPos.get(0);
            return new ReturnObject(customerPo);
        }
    }

    /**
     * 根据电话查询用户
     */
    public ReturnObject<List<CustomerPo>> searchByMobile(String mobile){
        CustomerPoExample example=new CustomerPoExample();
        CustomerPoExample.Criteria criteria=example.createCriteria();
        criteria.andMobileEqualTo(mobile);
        List<CustomerPo> customerPos=null;
        try{
            customerPos=customerPoMapper.selectByExample(example);
        }
        catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
        if(customerPos.size()==0){
            return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
        }
        else{
            CustomerPo customerPo=customerPos.get(0);
            return new ReturnObject(customerPo);
        }
    }

    /**
     * 根据主码直接返回用户个人信息
     */
    public ReturnObject<Customer>showCustomer(Long id){
        try{
            CustomerPo customerPo=customerPoMapper.selectByPrimaryKey(id);
            if(customerPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>((Customer) cloneVo(customerPo,Customer.class));
        }catch(Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 用户修改自己的信息
     */
    public ReturnObject updateCustomer(Customer customer)
    {
        try{
            CustomerPo oldCustomer= customerPoMapper.selectByPrimaryKey(customer.getId());
            oldCustomer.setUserName(customer.getUserName());
            setPoModifiedFields(oldCustomer,customer.getModifierId(),customer.getModifierName());
            customerPoMapper.updateByPrimaryKeySelective(oldCustomer);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 用户修改密码
     */
    public ReturnObject changePassword(Customer customer)
    {
        try{
            CustomerPo oldCustomer =customerPoMapper.selectByPrimaryKey(customer.getId());
            if(customer.getPassword().equals(oldCustomer.getPassword())){
                return new ReturnObject(ReturnNo.CUSTOMER_PASSWORDSAME);
            }
            oldCustomer.setPassword(customer.getPassword());
            setPoModifiedFields(oldCustomer,customer.getModifierId(),customer.getModifierName());
            customerPoMapper.updateByPrimaryKeySelective(oldCustomer);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 平台管理员获取所有用户列表
     */
    public ReturnObject<FullCustomer> getAllUsers(Integer page,Integer pageSize){
        CustomerPoExample example=new CustomerPoExample();
        try{
            PageHelper.startPage(page,pageSize);
            List<CustomerPo> poList=customerPoMapper.selectByExample(example);
            PageInfo<CustomerPo> pageInfo=new PageInfo<>(poList);
            return Common.getPageRetVo(new ReturnObject(pageInfo),SimpleCustomer.class);
        }
        catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 管理员封禁买家
     */
    public ReturnObject banUser(Long id,Long userId,String userName){
        try {
            CustomerPo customerPo=customerPoMapper.selectByPrimaryKey(id);
            if(customerPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            customerPo.setState((byte)1);
            setPoModifiedFields(customerPo,userId,userName);
            customerPoMapper.updateByPrimaryKeySelective(customerPo);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }

    /**
     * 管理员解禁买家
     */
    public ReturnObject releaseUser(Long id,Long userId,String userName){
        try {
            CustomerPo customerPo=customerPoMapper.selectByPrimaryKey(id);
            if(customerPo==null){
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            customerPo.setState((byte)0);
            setPoModifiedFields(customerPo,userId,userName);
            customerPoMapper.updateByPrimaryKeySelective(customerPo);
            return new ReturnObject(ReturnNo.OK);
        }
        catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR);
        }
    }
}
