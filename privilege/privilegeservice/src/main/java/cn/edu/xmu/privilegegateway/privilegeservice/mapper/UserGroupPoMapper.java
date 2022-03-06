package cn.edu.xmu.privilegegateway.privilegeservice.mapper;

import cn.edu.xmu.privilegegateway.privilegeservice.model.po.UserGroupPo;
import cn.edu.xmu.privilegegateway.privilegeservice.model.po.UserGroupPoExample;
import java.util.List;

public interface UserGroupPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    int deleteByExample(UserGroupPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    int insert(UserGroupPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    int insertSelective(UserGroupPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    List<UserGroupPo> selectByExample(UserGroupPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    UserGroupPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(UserGroupPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_group
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(UserGroupPo record);
}