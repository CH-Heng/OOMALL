package cn.edu.xmu.oomall.goods.dao;

import cn.edu.xmu.oomall.core.util.ReturnNo;
import cn.edu.xmu.oomall.core.util.ReturnObject;
import cn.edu.xmu.oomall.goods.mapper.OnSalePoMapper;
import cn.edu.xmu.oomall.goods.model.bo.OnSale;
import cn.edu.xmu.oomall.goods.model.po.OnSalePo;
import cn.edu.xmu.oomall.goods.model.po.OnSalePoExample;
import cn.edu.xmu.privilegegateway.annotation.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoCreatedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.setPoModifiedFields;
import static cn.edu.xmu.privilegegateway.annotation.util.Common.cloneVo;
import static cn.edu.xmu.oomall.core.util.Common.getAvgArray;

/**
 * @author yujie lin 22920192204242
 * @date 2021/11/10
 */
@Repository
public class OnSaleDao {
    private final static String ONSALE_STOCK_GROUP_KEY = "onsale_%d_stockgroup_%d";
    private final static String ONSALE_SET_KEY="onsale_%d_set";

    private final static String DECREASE_PATH = "stock/decrease.lua";
    private final static String INCREASE_PATH = "stock/increase.lua";
    private final static String LOAD_PATH = "stock/load.lua";

    private final static String ONSALE_ID="o_%d";


    private Logger logger = LoggerFactory.getLogger(OnSaleDao.class);
    @Autowired
    private OnSalePoMapper onSalePoMapper;
    @Autowired
    private RedisUtil redis;


    /**
     * 创建Onsale对象
     *
     * @param onSale 传入的Onsale对象
     * @return 返回对象ReturnObj
     */
    public ReturnObject createOnSale(OnSale onSale, Long userId, String userName) {
        try {
            OnSalePo onsalePo = (OnSalePo) cloneVo(onSale, OnSalePo.class);
            setPoCreatedFields(onsalePo, userId, userName);
            onSalePoMapper.insertSelective(onsalePo);
            return new ReturnObject((OnSale) cloneVo(onsalePo, OnSale.class));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject onlineOrOfflineOnSale(OnSale onsale, Long userId, String userName) {
        try {
            OnSalePo po = (OnSalePo) cloneVo(onsale, OnSalePo.class);
            setPoModifiedFields(po, userId, userName);
            onSalePoMapper.updateByPrimaryKeySelective(po);
            return new ReturnObject();
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    public ReturnObject onlineOrOfflineOnSaleAct(Long actId, Long userId, String userName, OnSale.State cntState, OnSale.State finalState) {
        try {
            OnSalePoExample oe = new OnSalePoExample();
            OnSalePoExample.Criteria cr = oe.createCriteria();
            cr.andActivityIdEqualTo(actId);
            Byte s1 = cntState.getCode().byteValue();
            cr.andStateEqualTo(s1);

            Byte s2 = finalState.getCode().byteValue();
            List<OnSalePo> pos = onSalePoMapper.selectByExample(oe);

            for (OnSalePo po : pos) {
                po.setState(s2);
                setPoModifiedFields(po, userId, userName);
                redis.del(String.format(ONSALE_ID,po.getId()));
                if (finalState == OnSale.State.OFFLINE) {
                    //如果结束时间晚于当前时间且开始时间早于当前时间，修改结束时间为当前时间
                    if (po.getEndTime().isAfter(LocalDateTime.now()) && po.getBeginTime().isBefore(LocalDateTime.now())) {
                        po.setEndTime(LocalDateTime.now());
                    }
                } else if (finalState == OnSale.State.ONLINE) {
                    //如果开始时间早于当前时间且结束时间晚于当前时间，修改开始时间为当前时间
                    if (po.getBeginTime().isBefore(LocalDateTime.now()) && po.getEndTime().isAfter(LocalDateTime.now())) {
                        po.setBeginTime(LocalDateTime.now());
                    }
                }

                onSalePoMapper.updateByPrimaryKeySelective(po);
            }
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    public ReturnObject getOnSaleById(Long id) {
        try {
            OnSalePo po = onSalePoMapper.selectByPrimaryKey(id);
            if (po == null) {
                OnSale ret = null;
                return new ReturnObject(ret);
            }
            OnSale ret = (OnSale) cloneVo(po, OnSale.class);
            return new ReturnObject(ret);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }

    public ReturnObject deleteOnSale(Long id) {
        try {
            onSalePoMapper.deleteByPrimaryKey(id);
            redis.del(String.format(ONSALE_ID,id));
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject deleteOnSaleAct(Long actId) {
        try {
            OnSalePoExample oe = new OnSalePoExample();
            OnSalePoExample.Criteria cr = oe.createCriteria();
            cr.andActivityIdEqualTo(actId);
            cr.andStateEqualTo(OnSale.State.DRAFT.getCode().byteValue());
            List<OnSalePo> pos = onSalePoMapper.selectByExample(oe);
            for (OnSalePo po : pos) {
                onSalePoMapper.deleteByPrimaryKey(po.getId());
                redis.del(String.format(ONSALE_ID,po.getId()));
            }
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    public ReturnObject onSaleShopMatch(Long id, Long shopId) {
        try {
            if(shopId==0) {
                return new ReturnObject(true);
            }
            OnSalePoExample oe = new OnSalePoExample();
            OnSalePoExample.Criteria cr = oe.createCriteria();
            cr.andIdEqualTo(id);
            cr.andShopIdEqualTo(shopId);
            List<OnSalePo> l1 = onSalePoMapper.selectByExample(oe);
            if (l1.size() > 0) {
                return new ReturnObject(true);
            }
            return new ReturnObject(false);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }


    public ReturnObject timeCollided(OnSale onsale) {
        try {
            OnSalePoExample oe = new OnSalePoExample();
            OnSalePoExample.Criteria cr = oe.createCriteria();
            cr.andProductIdEqualTo(onsale.getProductId());
            List<OnSalePo> l1 = onSalePoMapper.selectByExample(oe);
            for(OnSalePo onSalePo:l1){
                if(onSalePo.getBeginTime().isBefore(onsale.getBeginTime())&&onSalePo.getEndTime().isAfter(onsale.getBeginTime())){
                    return new ReturnObject(true);
                }else if(onSalePo.getBeginTime().isBefore(onsale.getEndTime())&&onSalePo.getEndTime().isAfter(onsale.getEndTime())){
                    return new ReturnObject(true);
                }
            }
            return new ReturnObject(false);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }

    }


    public ReturnObject updateOnSale(OnSale onsale, Long userId, String userName) {
        try {
            OnSalePo po = (OnSalePo) cloneVo(onsale, OnSalePo.class);
            setPoModifiedFields(po, userId, userName);
            onSalePoMapper.updateByPrimaryKeySelective(po);
            redis.del(String.format(ONSALE_ID,po.getId()));

            if(po.getShareActId()!=null&& po.getShareActId().equals(-1L)){
                OnSalePo newPo= onSalePoMapper.selectByPrimaryKey(po.getId());
                newPo.setShareActId(null);
                onSalePoMapper.updateByPrimaryKey(newPo);
            }
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 减Redis中的预扣库存
     * 基本思想用脚本实现如下逻辑：
     * 1. 如果有多个key的话，维持一个xx集合，记录值>0的key
     * 2. 随机从多个key减库存，如果库存减到0，则从xx集合中去除这个key
     * 3. 如果key库存不够扣，则从XX集合中选取下个key，看是否够，够按照2处理，不够继续找下一个，直到找到足够扣的key，或者所有key都不够扣
     * 这个逻辑的基本思想是在开始的时候都是直接随机扣
     * 在结束阶段的逻辑是先随机扣，发现是0，再到集合中寻找适合扣的key
     * 这样可以保证开始的时候比较快完成，结束的时候保证公平
     * @param id
     * @param quantity
     * @param groupNum
     * @param wholeQuantity
     * @return
     */
    public ReturnObject decreaseOnSaleQuantity(Long id, Integer quantity, Integer groupNum, Integer wholeQuantity) {
        try {
            String setKey=String.format(ONSALE_SET_KEY,id);

            if(!redis.hasKey(setKey)) {
                loadQuantity(setKey, id, groupNum, wholeQuantity);
            }

            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource(DECREASE_PATH)));
            script.setResultType(Long.class);

            Long timeSeed=  System.currentTimeMillis();
            Long res = redis.executeScript(script,
                    Stream.of(setKey).collect(Collectors.toList()), quantity,timeSeed);
            if (res >= 0) {
                return new ReturnObject(ReturnNo.OK);
            }
            return new ReturnObject(ReturnNo.GOODS_STOCK_SHORTAGE, "扣库存失败");
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    /**
     * 增加预扣库存
     * 如果有多个key的话，需要把不为0的key加到集合中
     * @param id
     * @param quantity
     * @param groupNum
     * @return
     */
    public ReturnObject increaseOnSaleQuantity(Long id, Integer quantity, Integer groupNum) {
        try {
            String setKey=String.format(ONSALE_SET_KEY,id);

            DefaultRedisScript<Long> script = new DefaultRedisScript<>();
            script.setScriptSource(new ResourceScriptSource(new ClassPathResource(INCREASE_PATH)));
            script.setResultType(Long.class);

            int[] incr = getAvgArray(groupNum, quantity);

            Random r = new Random();
            int init = r.nextInt(groupNum);

            for (int i = 0; i < groupNum; i++) {
                String key = String.format(ONSALE_STOCK_GROUP_KEY, id, (init + i) % groupNum);
                List<String> keys = Stream.of(setKey,key).collect(Collectors.toList());
                Long res = redis.executeScript(script, keys, incr[i]);
                if (res == -1) {
                    return new ReturnObject(ReturnNo.GOODS_ONSALE_NOTEFFECTIVE, "加库存失败");
                }
            }
            return new ReturnObject(ReturnNo.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ReturnObject(ReturnNo.INTERNAL_SERVER_ERR, e.getMessage());
        }
    }

    private void loadQuantity(String setKey,Long id, Integer groupNum, Integer wholeQuantity) {

        int[] incr = getAvgArray(groupNum, wholeQuantity);
        DefaultRedisScript script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource(LOAD_PATH)));

        for (int i = 0; i < groupNum; i++) {
            redis.executeScript(script,
                    Stream.of(setKey,String.format(ONSALE_STOCK_GROUP_KEY, id, i)).collect(Collectors.toList()), incr[i]);
        }
    }


}
