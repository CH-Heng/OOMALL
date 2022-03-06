package cn.edu.xmu.oomall.liquidation.mapper;

import cn.edu.xmu.oomall.liquidation.model.po.ExpenditureItemPo;
import cn.edu.xmu.oomall.liquidation.model.po.ExpenditureItemPoExample;
import java.util.List;

public interface ExpenditureItemPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    int insert(ExpenditureItemPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    int insertSelective(ExpenditureItemPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    List<ExpenditureItemPo> selectByExample(ExpenditureItemPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    ExpenditureItemPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(ExpenditureItemPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_expenditure_item
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(ExpenditureItemPo record);
}