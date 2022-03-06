package cn.edu.xmu.privilegegateway.privilegeservice.mapper;

import cn.edu.xmu.privilegegateway.privilegeservice.model.po.UserRolePo;
import cn.edu.xmu.privilegegateway.privilegeservice.model.po.UserRolePoExample;
import java.util.List;

public interface UserRolePoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int deleteByExample(UserRolePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int insert(UserRolePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int insertSelective(UserRolePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    List<UserRolePo> selectByExample(UserRolePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    UserRolePo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(UserRolePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_user_role
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(UserRolePo record);
}