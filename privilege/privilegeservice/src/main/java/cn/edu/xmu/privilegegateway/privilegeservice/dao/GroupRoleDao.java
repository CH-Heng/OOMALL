package cn.edu.xmu.privilegegateway.privilegeservice.dao;

import cn.edu.xmu.privilegegateway.annotation.util.ReturnNo;
import cn.edu.xmu.privilegegateway.annotation.util.ReturnObject;
import cn.edu.xmu.privilegegateway.privilegeservice.mapper.GroupRolePoMapper;
import cn.edu.xmu.privilegegateway.privilegeservice.model.po.GroupRolePo;
import cn.edu.xmu.privilegegateway.privilegeservice.model.po.GroupRolePoExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cn.edu.xmu.privilegegateway.privilegeservice.dao.RoleDao.ROLEKEY;
@Repository
public class GroupRoleDao {
    @Autowired
    private GroupRolePoMapper groupRolePoMapper;

    private final static String GROUPKEY = "g_%d";

    public ReturnObject<List<GroupRolePo>> selectByRoleId(Long roleId){
        try{
            Set<String> resultSet=new HashSet<String>();
            GroupRolePoExample example1=new GroupRolePoExample();
            resultSet.add(String.format(ROLEKEY,roleId));
            GroupRolePoExample.Criteria criteria1=example1.createCriteria();
            criteria1.andRoleIdEqualTo(roleId);
            List<GroupRolePo> gList=groupRolePoMapper.selectByExample(example1);
            return new ReturnObject<>(gList);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

//    public ReturnObject<List<GroupRolePo>> selectBygroupId(Long groupId){
//        try{
//            Set<String> resultSet=new HashSet<String>();
//            GroupRolePoExample example1=new GroupRolePoExample();
//            resultSet.add(String.format(GROUPKEY,groupId));
//            GroupRolePoExample.Criteria criteria1=example1.createCriteria();
//            criteria1.andGroupIdEqualTo(groupId);
//            List<GroupRolePo> gList=groupRolePoMapper.selectByExample(example1);
//            return new ReturnObject<>(gList);
//        }catch (Exception e){
//            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
//        }
//    }
}
