package cn.edu.xmu.oomall.activity.dao;

import cn.edu.xmu.oomall.activity.mapper.AdvanceSalePoMapper;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSale;
import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePo;
import cn.edu.xmu.oomall.core.util.Common;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.edu.xmu.oomall.activity.model.bo.AdvanceSaleState;
import cn.edu.xmu.oomall.activity.model.po.AdvanceSalePoExample;
import cn.edu.xmu.oomall.activity.model.vo.SimpleAdvanceSaleRetVo;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
/**
 * @author GXC 22920192204194
 */
@Repository
@Slf4j
public class AdvanceSaleDao {

    @Autowired
    AdvanceSalePoMapper advanceSalePoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Value("${oomall.activity.advancesale.expiretime}")
    private Long categoryTimeout;

    private static Logger logger = LoggerFactory.getLogger(Common.class);

    public final static String ADVANCESALE_KEY = "advanceSale_%d";
    /**
     * 根据id查找预售活动
     * @param id
     * @return
     */
    public ReturnObject selectAdvanceSaleByKey(Long id){
        ReturnObject returnObject=null;
        try{
            AdvanceSalePo po=null;
            AdvanceSale bo=(AdvanceSale) redisUtil.get(String.format(ADVANCESALE_KEY,id));
            if(bo!=null) {
                po=(AdvanceSalePo) cloneVo(bo,AdvanceSalePo.class);
            }
            else {
                po=advanceSalePoMapper.selectByPrimaryKey(id);
                if(po!=null){
                    bo=(AdvanceSale) cloneVo(po,AdvanceSale.class);
                    redisUtil.set(String.format(ADVANCESALE_KEY,id),bo,categoryTimeout);
                }
            }
            returnObject=new ReturnObject(po);
        }catch(Exception e){
            logger.error(e.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return returnObject;
    }
    /**
     * 更新预售活动
     * @param po
     * @return
     */
    public ReturnObject updateAdvanceSale(AdvanceSalePo po){
        ReturnObject returnObject=null;
        try{
            redisUtil.flush();
            advanceSalePoMapper.updateByPrimaryKeySelective(po);
            returnObject=new ReturnObject();
        }catch(Exception e){
            logger.error(e.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return returnObject;
    }
    /**
     * 物理删除预售活动
     * @param id
     * @return
     */
    public ReturnObject deleteAdvanceSale(Long id){
        ReturnObject returnObject=null;
        try{
            redisUtil.flush();
            advanceSalePoMapper.deleteByPrimaryKey(id);
            returnObject=new ReturnObject();
        }catch(Exception e){
            logger.error(e.toString());
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
        return returnObject;
    }

    /**
     * @author Jiawei Zheng
     * @date 2021-11-26
     */

    /**
     * 查询所有预售活动
     * @param shopId
     * @param state
     * @param activityIdList
     * @param page
     * @param pageSize
     * @return
     */
    public ReturnObject getAllAdvanceSale(Long shopId, Byte state, List<Long> activityIdList, Integer page, Integer pageSize){
        AdvanceSalePoExample example = new AdvanceSalePoExample();
        AdvanceSalePoExample.Criteria criteria = example.createCriteria();
        if(shopId!=null) {
            criteria.andShopIdEqualTo(shopId);
        }
        if(state!=null)
        {
            criteria.andStateEqualTo(state);
        }
        if(activityIdList!=null&&!activityIdList.isEmpty())
        {
            criteria.andIdIn(activityIdList);
        }
        try {
            PageHelper.startPage(page, pageSize);
            List<AdvanceSalePo> poList = advanceSalePoMapper.selectByExample(example);
            PageInfo<AdvanceSalePo> pageInfo = new PageInfo<>(poList);
            ReturnObject returnObject = new ReturnObject(pageInfo);
            return Common.getPageRetVo(returnObject, SimpleAdvanceSaleRetVo.class);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getAdvanceSaleInfo(AdvanceSaleState state,Long id){
        try {
            //先查redis
            AdvanceSale advanceSale = (AdvanceSale) redisUtil.get(String.format(ADVANCESALE_KEY,id));
            //redis没有查到
            if(advanceSale==null) {
                AdvanceSalePo po = advanceSalePoMapper.selectByPrimaryKey(id);
                //数据库也查不到
                if(po==null) {
                    return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST,"活动不存在");
                }
                //数据库查到，放入redis
                else {
                    advanceSale = (AdvanceSale) cloneVo(po, AdvanceSale.class);
                    redisUtil.set(String.format(ADVANCESALE_KEY,id), advanceSale, categoryTimeout);
                }
            }
            //如果传入状态不为空，判断活动是否为上线状态
            if (state != null && !advanceSale.getState().equals(state.getCode())) {
                return new ReturnObject<>(ReturnNo.STATENOTALLOW, "预售活动未上线");
            }
            return new ReturnObject(advanceSale);

        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员新增预售
     * @param adminId
     * @param adminName
     * @param advanceSaleBo
     * @return
     */
    public ReturnObject addAdvanceSale(Long adminId,String adminName,AdvanceSale advanceSaleBo){
        try {
            AdvanceSalePo advanceSalePo = (AdvanceSalePo) cloneVo(advanceSaleBo, AdvanceSalePo.class);
            setPoCreatedFields(advanceSalePo,adminId,adminName);
            advanceSalePoMapper.insert(advanceSalePo);
            return new ReturnObject(cloneVo(advanceSalePo, AdvanceSale.class));
        }
        catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }
}
