package cn.edu.xmu.oomall.activity.dao;

import cn.edu.xmu.oomall.activity.mapper.ShareActivityPoMapper;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivity;
import cn.edu.xmu.oomall.activity.model.bo.ShareActivityBo;
import cn.edu.xmu.oomall.activity.model.po.ShareActivityPo;
import cn.edu.xmu.oomall.activity.model.po.ShareActivityPoExample;
import cn.edu.xmu.oomall.activity.model.vo.*;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import cn.edu.xmu.oomall.core.util.*;
import cn.edu.xmu.privilegegateway.annotation.util.JacksonUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.edu.xmu.oomall.core.util.Common;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/11/12 12:55
 */
@Repository
public class ShareActivityDao {
    private static final Logger logger = LoggerFactory.getLogger(ShareActivityDao.class);
    @Autowired
    private ShareActivityPoMapper shareActivityPoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${oomall.activity.share.expiretime}")
    private Long shareActivityExpireTime;

    private final static String SHARE_BY_ID="shareactivivybyid_%d";
    private final static String SHARE_BY_ID_AND_SHOP_ID="shareactivivybyid_%d_shop_%d";

    /**
     * 显示分享活动列表
     *
     * @param bo       shareActivity bo对象
     * @param shareIds 相关的分享活动id 集合
     * @param page     页码
     * @param pageSize 每页数目
     * @return
     */
    public ReturnObject getShareByShopId(ShareActivityBo bo, List<Long> shareIds, Integer page, Integer pageSize) {
        ShareActivityPoExample example = new ShareActivityPoExample();
        ShareActivityPoExample.Criteria criteria = example.createCriteria();
        ShareActivityPo shareActivityPo = cloneVo(bo, ShareActivityPo.class);
        if (shareActivityPo.getShopId() != null) {
            criteria.andShopIdEqualTo(shareActivityPo.getShopId());
        }
        if (shareIds != null && !shareIds.isEmpty()) {
            criteria.andIdIn(shareIds);
        }
        if (shareActivityPo.getBeginTime() != null) {
            criteria.andBeginTimeGreaterThanOrEqualTo(shareActivityPo.getBeginTime());
        }
        if (shareActivityPo.getEndTime() != null) {
            criteria.andEndTimeLessThanOrEqualTo(shareActivityPo.getEndTime());
        }
        if (shareActivityPo.getShopId() != null) {
            criteria.andShopIdEqualTo(shareActivityPo.getShopId());
        }
        if (shareActivityPo.getState() != null) {
            criteria.andStateEqualTo(shareActivityPo.getState());
        }
        try {
            PageHelper.startPage(page, pageSize);
            List<ShareActivityPo> shareActivityPos = shareActivityPoMapper.selectByExample(example);
            PageInfo pageInfo = new PageInfo(shareActivityPos);
            ReturnObject returnObject = Common.getPageRetVo(new ReturnObject<>(pageInfo),RetShareActivityListVo.class);
            return returnObject;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 管理员新增分享活动
     *
     * @param shareActivityBo shareActivityBo对象
     * @return
     */
    public ReturnObject addShareAct(ShareActivityBo shareActivityBo) {
        ShareActivityPo shareActivityPo = cloneVo(shareActivityBo, ShareActivityPo.class);
        shareActivityPo.setStrategy(JacksonUtil.toJson(shareActivityBo.getStrategy()));
        try {
            int flag = shareActivityPoMapper.insert(shareActivityPo);
            shareActivityBo = cloneVo(shareActivityPo, ShareActivityBo.class);
            if (shareActivityPo.getStrategy() != null) {
                List<StrategyVo> strategyVos = (List<StrategyVo>) JacksonUtil.toObj(shareActivityPo.getStrategy(), new ArrayList<StrategyVo>().getClass());
                shareActivityBo.setStrategy(strategyVos);
            }
            return new ReturnObject(shareActivityBo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    /**
     * 查看分享活动详情 只显示上线状态的分享活动
     *
     * @param id 分享活动Id
     * @return
     */
    public ReturnObject getShareActivityById(Long id) {
        String key = String.format(SHARE_BY_ID,id);
        try {
            ShareActivityBo shareActivityBo = (ShareActivityBo) redisUtil.get(key);
            if (shareActivityBo != null) {
                return new ReturnObject(shareActivityBo);
            }
            ShareActivityPo shareActivityPo = shareActivityPoMapper.selectByPrimaryKey(id);
            if (shareActivityPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            shareActivityBo = cloneVo(shareActivityPo, ShareActivityBo.class);
            List<StrategyVo> strategy=new ArrayList<>();
            if (shareActivityPo.getStrategy() != null) {
                List<HashMap<String,Integer>> strategyVos = (List<HashMap<String, Integer>>) JacksonUtil.toObj(shareActivityPo.getStrategy(), new ArrayList<StrategyVo>().getClass());
                if(strategyVos!=null){
                    for(int i=0;i<strategyVos.size();i++){
                        HashMap<String, Integer> stringObjectHashMap = strategyVos.get(i);
                        StrategyVo strategyVo = new StrategyVo(stringObjectHashMap.get("quantity"), stringObjectHashMap.get("percentage"));
                        strategy.add(strategyVo);
                    }
                    shareActivityBo.setStrategy(strategy);
                }
            }
            redisUtil.set(key, shareActivityBo, shareActivityExpireTime);
            return new ReturnObject(shareActivityBo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 查看特定分享活动详情,显示所有状态的分享活动
     *
     * @param shopId 店铺Id
     * @param id     分享活动Id
     * @return
     */
    public ReturnObject getShareActivityByShopIdAndId(Long shopId, Long id) {
        String key = String.format(SHARE_BY_ID_AND_SHOP_ID,id,shopId);
        try {
            ShareActivityBo shareActivityBo = (ShareActivityBo) redisUtil.get(key);
            if (shareActivityBo != null) {
                return new ReturnObject(shareActivityBo);
            }
            ShareActivityPoExample shareActivityPoExample = new ShareActivityPoExample();
            ShareActivityPoExample.Criteria criteria = shareActivityPoExample.createCriteria();
            criteria.andIdEqualTo(id);
            criteria.andShopIdEqualTo(shopId);
            List<ShareActivityPo> shareActivityPos = shareActivityPoMapper.selectByExample(shareActivityPoExample);
            if (shareActivityPos.isEmpty()) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            ShareActivityPo shareActivityPo = shareActivityPos.get(0);
            shareActivityBo = cloneVo(shareActivityPo, ShareActivityBo.class);
            List<StrategyVo> strategy=new ArrayList<>();
            if (shareActivityPo.getStrategy() != null) {
                List<HashMap<String,Integer>> strategyVos = (List<HashMap<String, Integer>>) JacksonUtil.toObj(shareActivityPo.getStrategy(), new ArrayList<StrategyVo>().getClass());
                if (strategyVos!=null){
                    for(int i=0;i<strategyVos.size();i++){
                        HashMap<String, Integer> stringObjectHashMap = strategyVos.get(i);
                        StrategyVo strategyVo = new StrategyVo(stringObjectHashMap.get("quantity"), stringObjectHashMap.get("percentage"));
                        strategy.add(strategyVo);
                    }
                    shareActivityBo.setStrategy(strategy);
                }
            }
            redisUtil.set(key, shareActivityBo, shareActivityExpireTime);
            return new ReturnObject(shareActivityBo);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }


    /**
     * 根据分享活动id查找分享活动
     * @param id 分享活动id
     * @return shareActivity
     * @author BingShuai Liu 22920192204245
     */
    public ReturnObject<ShareActivityPo> selectShareActivityById(Long id){
        try{
            ShareActivityPo shareActivityPo=shareActivityPoMapper.selectByPrimaryKey(id);
            if (shareActivityPo==null){
                return new ReturnObject<>(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            return new ReturnObject<>(shareActivityPo);
        }catch (Exception e){
            return new ReturnObject<>(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

    /**
     * 根据分享活动的id, 修改对应表项
     * @param shareActivityPo
     * @return
     * @author BingShuai Liu 22920192204245
     */
    public ReturnObject modifyShareActivity(ShareActivityPo shareActivityPo){
        int ret;
        try {
            ret = shareActivityPoMapper.updateByPrimaryKeySelective(shareActivityPo);
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(ret == 0){
            return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
        }else{
            ReturnObject returnObject = selectShareActivityById(shareActivityPo.getId());
            if (returnObject.getCode()!=null){
                ShareActivityPo shareActivityPo1 = (ShareActivityPo) returnObject.getData();
                ShareActivityBo shareActivityBo = cloneVo(shareActivityPo1,ShareActivityBo.class);
                String key1 = String.format(SHARE_BY_ID,shareActivityBo.getId());
                String key2 = String.format(SHARE_BY_ID_AND_SHOP_ID,shareActivityBo.getId(),shareActivityBo.getShopId());
                if (redisUtil.hasKey(key1)){
                    redisUtil.set(key1,shareActivityBo,shareActivityExpireTime);
                }
                if (redisUtil.hasKey(key2)){
                    redisUtil.set(key2,shareActivityBo,shareActivityExpireTime);
                }
            }
            return new ReturnObject(ReturnNo.OK);
        }
    }

    /**
     * 根据分享活动的id 删除对应表项
     * 需要判断state是否为0(草稿态)
     * @param id
     * @return
     * @author BingShuai Liu 22920192204245
     */
    public ReturnObject deleteShareActivity(Long id){
        int ret;
        try {
            ShareActivityPo shareActivityPo = shareActivityPoMapper.selectByPrimaryKey(id);
            if (shareActivityPo == null) {
                return new ReturnObject(ReturnNo.RESOURCE_ID_NOTEXIST);
            }
            shareActivityPoMapper.deleteByPrimaryKey(id);

            String key = String.format(SHARE_BY_ID,id);
            String key2 = String .format(SHARE_BY_ID_AND_SHOP_ID,id,shareActivityPo.getShopId());
            if (redisUtil.hasKey(key)){
                redisUtil.del(key);
            }
            if (redisUtil.hasKey(key2)){
                redisUtil.del(key2);
            }
            return new ReturnObject();
        }catch (Exception e){
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR,e.getMessage());
        }
    }

}
