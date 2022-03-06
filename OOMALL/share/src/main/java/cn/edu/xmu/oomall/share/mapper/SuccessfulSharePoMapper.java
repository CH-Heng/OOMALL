package cn.edu.xmu.oomall.share.mapper;

import cn.edu.xmu.oomall.share.model.po.SuccessfulSharePo;
import cn.edu.xmu.oomall.share.model.po.SuccessfulSharePoExample;
import java.util.List;

public interface SuccessfulSharePoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    int insert(SuccessfulSharePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    int insertSelective(SuccessfulSharePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    List<SuccessfulSharePo> selectByExample(SuccessfulSharePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    SuccessfulSharePo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(SuccessfulSharePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_successful_share
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(SuccessfulSharePo record);
}