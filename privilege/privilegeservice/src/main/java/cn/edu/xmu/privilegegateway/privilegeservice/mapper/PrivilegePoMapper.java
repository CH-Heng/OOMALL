package cn.edu.xmu.privilegegateway.privilegeservice.mapper;

import cn.edu.xmu.privilegegateway.privilegeservice.model.po.PrivilegePo;
import cn.edu.xmu.privilegegateway.privilegeservice.model.po.PrivilegePoExample;
import java.util.List;

public interface PrivilegePoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    int insert(PrivilegePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    int insertSelective(PrivilegePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    List<PrivilegePo> selectByExample(PrivilegePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    PrivilegePo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(PrivilegePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table auth_privilege
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(PrivilegePo record);
}