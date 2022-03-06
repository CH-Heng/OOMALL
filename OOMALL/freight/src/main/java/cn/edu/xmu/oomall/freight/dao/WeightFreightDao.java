package cn.edu.xmu.oomall.freight.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.freight.mapper.WeightFreightPoMapper;
import cn.edu.xmu.oomall.freight.model.bo.WeightFreight;
import cn.edu.xmu.oomall.freight.model.po.WeightFreightPo;
import cn.edu.xmu.oomall.freight.model.po.WeightFreightPoExample;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author ziyi guo
 * @date 2021/11/16
 */
@Repository
public class WeightFreightDao {

    @Autowired
    private WeightFreightPoMapper weightFreightPoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.freight.model.expiretime}")
    private long timeout;

    public final static String WEIGHT_FREIGHT_REGION_KEY = "weightfreight_%d_region_%d";

    /**
     * 管理员新增重量模板明细
     * @param weightFreightPo,userId,userName
     * @return ReturnObject
     */
    public ReturnObject addWeightItems(WeightFreightPo weightFreightPo, Long userId, String userName){

        try {
            setPoCreatedFields(weightFreightPo, userId, userName);

            WeightFreightPoExample example = new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria = example.createCriteria();
            criteria.andFreightModelIdEqualTo(weightFreightPo.getFreightModelId());
            criteria.andRegionIdEqualTo(weightFreightPo.getRegionId());
            List<WeightFreightPo> list = weightFreightPoMapper.selectByExample(example);
            if (list!=null && list.size() > 0) {
                return new ReturnObject(ReturnNo.FREIGHT_REGIONEXIST);
            }

            weightFreightPoMapper.insertSelective(weightFreightPo);
            WeightFreightPo newWeightFreightPo = weightFreightPoMapper.selectByPrimaryKey(weightFreightPo.getId());
            return new ReturnObject(cloneVo(newWeightFreightPo, WeightFreight.class));
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员查询某个重量运费模板的明细
     *
     * @param freightModelId
     * @return ReturnObject
     */
    public ReturnObject getWeightItems(Long freightModelId, Integer page, Integer pageSize) {
        try {
            PageHelper.startPage(page, pageSize);

            WeightFreightPoExample example=new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria=example.createCriteria();
            criteria.andFreightModelIdEqualTo(freightModelId);
            List<WeightFreightPo> weightFreightPoList = weightFreightPoMapper.selectByExample(example);
            List<WeightFreight> weightFreightBoList = new ArrayList<>();
            for (WeightFreightPo wfPo : weightFreightPoList) {
                weightFreightBoList.add((WeightFreight) cloneVo(wfPo,WeightFreight.class));
            }

            PageInfo pageInfo = PageInfo.of(weightFreightPoList);
            pageInfo.setList(weightFreightBoList);
            return new ReturnObject(pageInfo);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 店家或管理员通过默认模板id删除Freight Items
     *
     * @param freightModelId
     * @return ReturnObject
     */
    public ReturnObject deleteWeightItemsByFreightModelId(Long freightModelId) {
        try {

            WeightFreightPoExample example=new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria=example.createCriteria();
            criteria.andFreightModelIdEqualTo(freightModelId);
            List<WeightFreightPo> weightFreightPoList = weightFreightPoMapper.selectByExample(example);
            List<WeightFreight> weightFreightBoList = new ArrayList<>();
            for (WeightFreightPo wfPo : weightFreightPoList) {
                deleteWeightItems(wfPo.getId());
            }
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 店家或管理员修改重量运费模板明细
     * @param weightFreightPo,userId,userName
     * @return ReturnObject
     */
    public ReturnObject updateWeightItems(WeightFreightPo weightFreightPo, Long userId, String userName){

        try {
           setPoModifiedFields(weightFreightPo,userId,userName);

            WeightFreightPo wp = weightFreightPoMapper.selectByPrimaryKey(weightFreightPo.getId());
            if (wp == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            if(weightFreightPo.getRegionId()!=null) {
                WeightFreightPoExample example = new WeightFreightPoExample();
                WeightFreightPoExample.Criteria criteria = example.createCriteria();
                criteria.andRegionIdEqualTo(weightFreightPo.getRegionId());
                List<WeightFreightPo> list = weightFreightPoMapper.selectByExample(example);
                if (list!=null && list.size() > 0) {
                    if (!list.get(0).getId().equals(weightFreightPo.getId())) {
                        return new ReturnObject(ReturnNo.FREIGHT_REGIONSAME);
                    }
                }
            }

            weightFreightPoMapper.updateByPrimaryKeySelective(weightFreightPo);
            return new ReturnObject();
        } catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 店家或管理员删除重量运费模板明细
     * @param id
     * @return ReturnObject
     */
    public ReturnObject deleteWeightItems(Long id){
        try {
            int ret = weightFreightPoMapper.deleteByPrimaryKey(id);
            if (ret == 0) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            } else {
                return new ReturnObject(ReturnNo.OK);
            }
        } catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject getWeightItem(Long fid, List<Long> regionIds) {
        try {
            var regionId = regionIds.get(regionIds.size() - 1);
            var redisRet = redisUtil.get(String.format(WEIGHT_FREIGHT_REGION_KEY, fid, regionId));
            if (redisRet != null) {
                return new ReturnObject(redisRet);
            }
            for (int i = regionIds.size() - 1; i >= 0; --i) {
                var example = new WeightFreightPoExample();
                var criteria = example.createCriteria();
                criteria.andFreightModelIdEqualTo(fid).andRegionIdEqualTo(regionIds.get(i));
                var poList = weightFreightPoMapper.selectByExample(example);
                if (!poList.isEmpty()) {
                    var bo = (WeightFreight) cloneVo(poList.get(0), WeightFreight.class);
                    redisUtil.set(String.format(WEIGHT_FREIGHT_REGION_KEY, fid, regionId), bo, timeout);
                    return new ReturnObject(bo);
                }
            }
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

        return new ReturnObject(ReturnNo.FREIGHT_REGION_NOTREACH, "根据指定地区和运费模板未找到需要的运费模板明细");
    }

    public List<WeightFreightPo> getAllWeightItems(Long id) {
        try {
            WeightFreightPoExample example=new WeightFreightPoExample();
            WeightFreightPoExample.Criteria criteria=example.createCriteria();
            criteria.andFreightModelIdEqualTo(id);
            List<WeightFreightPo> weightFreightPoList = weightFreightPoMapper.selectByExample(example);
            return weightFreightPoList;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
