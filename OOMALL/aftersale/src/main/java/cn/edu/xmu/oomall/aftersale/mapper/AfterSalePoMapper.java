package cn.edu.xmu.oomall.aftersale.mapper;

import cn.edu.xmu.oomall.aftersale.model.po.AfterSalePo;
import cn.edu.xmu.oomall.aftersale.model.po.AfterSalePoExample;
import java.util.List;

public interface AfterSalePoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    int insert(AfterSalePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    int insertSelective(AfterSalePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    List<AfterSalePo> selectByExample(AfterSalePoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    AfterSalePo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(AfterSalePo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_aftersale_service
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(AfterSalePo record);
}