package cn.edu.xmu.oomall.payment.dao;

import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.payment.mapper.ErrorPaymentPoMapper;
import cn.edu.xmu.oomall.payment.model.bo.ErrorPayment;
import cn.edu.xmu.oomall.payment.util.CsvBeanFilter;
import cn.edu.xmu.oomall.payment.model.po.ErrorPaymentPo;
import cn.edu.xmu.oomall.payment.model.po.ErrorPaymentPoExample;
import cn.edu.xmu.oomall.payment.model.vo.SimpleErrorPaymentRetVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author Zijun Min
 * @sn 22920192204257
 * @createTime 2021/12/3 10:54
 **/
@Repository
public class ReconciliationDao {
    @Autowired
    private PaymentDao paymentDao;
    @Autowired
    private ErrorPaymentPoMapper errorPaymentPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(PaymentDao.class);


    /**
     * 根据条件查询simple错账
     */
    public ReturnObject getErrorPayment(String documentId, Byte state, LocalDateTime beginTime,
                                        LocalDateTime endTime, Integer page, Integer pageSize){
        try {
            ErrorPaymentPoExample errorPaymentPoExample = new ErrorPaymentPoExample();
            ErrorPaymentPoExample.Criteria criteria = errorPaymentPoExample.createCriteria();
            if (documentId != null) {
                criteria.andDocumentIdEqualTo(documentId);
            }
            if (state != null) {
                criteria.andStateEqualTo(state);
            }
            if (beginTime != null) {
                criteria.andTimeGreaterThan(beginTime);
            }
            if (endTime != null) {
                criteria.andTimeLessThan(endTime);
            }
            PageHelper.startPage(page, pageSize);
            List<ErrorPaymentPo> errorPaymentPoList = errorPaymentPoMapper.selectByExample(errorPaymentPoExample);
            PageInfo<ErrorPaymentPo> poPageInfo=new PageInfo<ErrorPaymentPo>(errorPaymentPoList);
            ReturnObject returnObject=new ReturnObject(poPageInfo);
            return Common.getPageRetVo(returnObject, SimpleErrorPaymentRetVo.class);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 根据id查询错账po
     */
    public ReturnObject getErrorPaymentById(Long id){
        try {
            ErrorPaymentPo errorPaymentPo = errorPaymentPoMapper.selectByPrimaryKey(id);
            if(errorPaymentPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject(errorPaymentPo);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * update错账信息
     */
    public ReturnObject updateErrorPayment(ErrorPaymentPo errorPaymentPo){
        try {
            errorPaymentPoMapper.updateByPrimaryKey(errorPaymentPo);
            return new ReturnObject(errorPaymentPo);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * add错账
     */
    public ReturnObject addErrorPayment(ErrorPayment errorPayment,Long loginUser,String loginName){
        try {
            if(errorPayment==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ErrorPaymentPo errorPaymentPo=cloneVo(errorPayment,ErrorPaymentPo.class);
            cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields(errorPaymentPo,loginUser,loginName);
            errorPaymentPoMapper.insert(errorPaymentPo);
            return new ReturnObject<>(ReturnNo.OK);
        }catch (Exception e){
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

}
