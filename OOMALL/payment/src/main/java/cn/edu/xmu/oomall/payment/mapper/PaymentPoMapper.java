package cn.edu.xmu.oomall.payment.mapper;

import cn.edu.xmu.oomall.payment.model.po.PaymentPo;
import cn.edu.xmu.oomall.payment.model.po.PaymentPoExample;
import java.util.List;

public interface PaymentPoMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    int insert(PaymentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    int insertSelective(PaymentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    List<PaymentPo> selectByExample(PaymentPoExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    PaymentPo selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(PaymentPo record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oomall_payment
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(PaymentPo record);
}